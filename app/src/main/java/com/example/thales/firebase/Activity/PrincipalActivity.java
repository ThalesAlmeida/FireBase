package com.example.thales.firebase.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.thales.firebase.Classes.Usuario;
import com.example.thales.firebase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PrincipalActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private DatabaseReference databaseReference;
    private TextView tipoUsuario;
    private Usuario usuario;
    private String tipoUsuarioEmail;
    private Menu menu1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        tipoUsuario = findViewById(R.id.txtTipoUsuario);
        autenticacao = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.clear();
        this.menu1 = menu;
        //Recebendo o email do usuario logado no momento
        String email = autenticacao.getCurrentUser().getEmail().toString();


        databaseReference.child("usuarios").orderByChild("email").equalTo(email.toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    tipoUsuarioEmail = dataSnapshot1.child("tipoUsuario").getValue().toString();

                    tipoUsuario.setText(tipoUsuarioEmail);

                    menu1.clear();
                    if(tipoUsuarioEmail.equals("Profissional")){
                        getMenuInflater().inflate(R.menu.menu_profissional, menu1);
                    }else if (tipoUsuarioEmail.equals("Cliente")){
                        getMenuInflater().inflate(R.menu.menu_cliente, menu1);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        getMenuInflater().inflate(R.menu.menu_profissional, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_add_usuario){
            abrirTelaCadastroUsuario();
        }else if (id == R.id.action_sair_profissional){
            deslogarUsuario();
        }else if (id == R.id.action_sair_cliente) {
            deslogarUsuario();
        }else if (id == R.id.action_cad_foto_perfil_cliente) {
            uploadFotoPerfil();
        }
        return super.onOptionsItemSelected(item);
    }

    public void abrirTelaCadastroUsuario(){
        Intent intent = new Intent(PrincipalActivity.this, CadastroUsuarioActivity.class);
        startActivity(intent);
    }

    private void deslogarUsuario(){
        autenticacao.signOut();

        Intent intent = new Intent(PrincipalActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }
    private void uploadFotoPerfil(){
        Intent intent = new Intent(PrincipalActivity.this, UploadFotoActivity.class);
        startActivity(intent);
    }


}
