package com.example.thales.firebase.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.thales.firebase.Classes.BaseActivity;
import com.example.thales.firebase.Classes.Usuario;
import com.example.thales.firebase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PrincipalActivityProfissional extends BaseActivity {

    private FirebaseAuth autenticacao;
    private DatabaseReference databaseReference;
    private TextView tipoUsuario;
    private Usuario usuario;
    private String tipoUsuarioEmail;
    private Menu menu1;

    private DrawerLayout drawerLayout;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_profissional);

        tipoUsuario = findViewById(R.id.txtTipoUsuario);
        autenticacao = FirebaseAuth.getInstance();

        drawerLayout = findViewById(R.id.drawer_layout);


        databaseReference = FirebaseDatabase.getInstance().getReference();

        setUpToolbar();
        setUpNavDrawer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.clear();
        this.menu1 = menu;
        //Recebendo o email do usuario logado no momento
        String email = autenticacao.getCurrentUser().getEmail().toString();


        databaseReference.child("usuariosProfissional").orderByChild("email").equalTo(email.toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    tipoUsuarioEmail = dataSnapshot1.child("tipoUsuario").getValue().toString();

                    tipoUsuario.setText(tipoUsuarioEmail);

                    menu1.clear();

                    getMenuInflater().inflate(R.menu.nav_drawer_menu_profissional, menu1);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.child("usuariosCliente").orderByChild("email").equalTo(email.toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot2: dataSnapshot.getChildren()){
                    tipoUsuarioEmail = dataSnapshot2.child("tipoUsuario").getValue().toString();

                    tipoUsuario.setText(tipoUsuarioEmail);

                    menu1.clear();

                    getMenuInflater().inflate(R.menu.nav_drawer_menu_cliente, menu1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //getMenuInflater().inflate(R.menu.menu_profissional, menu);
        return true;
    }

    protected void setUpToolbar() {
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if(toolbar!=null){
            setSupportActionBar(toolbar);
        }
    }

    //Configura o NavDrawer
    protected void setUpNavDrawer() {
        final ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                item.setChecked(true);

                drawerLayout.closeDrawers();

                onNavDrawerItemSelected(item);
                return true;
            }
        });

    }

    private void onNavDrawerItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_alterar_cadastro:
                abrirTelaAlterarCadastro();
                break;
            case R.id.action_sair_profissional:
                deslogarUsuario();
                break;
        }

    }

    private void deslogarUsuario(){
        autenticacao.signOut();

        Intent intent = new Intent(PrincipalActivityProfissional.this, MainActivity.class);
        startActivity(intent);
        finish();

    }
    private void uploadFotoPerfil(){
        Intent intent = new Intent(PrincipalActivityProfissional.this, UploadFotoActivity.class);
        startActivity(intent);
    }

    public void verProfissionais() {
        Intent intent = new Intent(PrincipalActivityProfissional.this, UsuariosActivity.class);
        startActivity(intent);
    }

    private void abrirTelaAlterarCadastro(){
        Intent intent = new Intent(PrincipalActivityProfissional.this,AlterarCadastroProfissional.class );
        startActivity(intent);
    }
}
