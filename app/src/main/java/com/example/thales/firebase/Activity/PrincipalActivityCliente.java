package com.example.thales.firebase.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
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

public class PrincipalActivityCliente extends BaseActivity {

    private FirebaseAuth autenticacao;
    private DatabaseReference databaseReference;
    private TextView tipoUsuario;
    private String tipoUsuarioEmail;
    private Menu menu1;

    private DrawerLayout drawerLayout;

    private boolean backPressedOnce = false;
    private Handler backPressedHandler = new Handler();

    private static final int BACK_PRESSED_DELAY = 2000;

    private final Runnable backPressedTimeoutAction = new Runnable() {
        @Override
        public void run() {
            backPressedOnce = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_cliente);

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
                    Intent intent = new Intent(PrincipalActivityCliente.this, PrincipalActivityProfissional.class);
                    startActivity(intent);
                    finish();
                    }
                }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return true;
    }


    protected void setUpToolbar() {
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        if(toolbar!=null){
            setSupportActionBar(toolbar);
        }
    }

    //Configura o NavDrawer
    protected void setUpNavDrawer() {
        final ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
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
            case R.id.action_ver_profissionais:
                verProfissionais();
                break;
            case R.id.action_cad_foto_perfil_cliente:
                uploadFotoPerfil();
                break;
            case R.id.action_sair_cliente:
                deslogarUsuario();
                break;
            case R.id.action_about:
                break;
        }

    }

    private void deslogarUsuario(){
        autenticacao.signOut();

        Intent intent = new Intent(PrincipalActivityCliente.this, MainActivity.class);
        startActivity(intent);
        finish();

    }
    private void uploadFotoPerfil(){
        Intent intent = new Intent(PrincipalActivityCliente.this, UploadFotoActivity.class);
        startActivity(intent);
    }

    public void verProfissionais() {
        Intent intent = new Intent(PrincipalActivityCliente.this, UsuariosActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                if(drawerLayout != null){
                    openDrawer();
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    protected void openDrawer(){
        if(drawerLayout !=null){
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    protected void closeDrawer(){
        if(drawerLayout !=null){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void onBackPressed() {
        // Back pressionado

        if (this.backPressedOnce) {
            // Finaliza a aplicacao

            finish();
            autenticacao.signOut();
            return;
        }

        this.backPressedOnce = true;

        Toast.makeText(this, "Pressione novamente para sair",
                Toast.LENGTH_SHORT).show();

        backPressedHandler.postDelayed(backPressedTimeoutAction, BACK_PRESSED_DELAY);

    }

}
