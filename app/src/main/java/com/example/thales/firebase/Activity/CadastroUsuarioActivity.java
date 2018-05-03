package com.example.thales.firebase.Activity;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.thales.firebase.Classes.Usuario;
import com.example.thales.firebase.DAO.ConfiguracaoFirebase;
import com.example.thales.firebase.Helper.Preferencias;
import com.example.thales.firebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CadastroUsuarioActivity extends AppCompatActivity {
    private BootstrapEditText email;
    private BootstrapEditText senha;
    private BootstrapEditText senha1;
    private BootstrapEditText nome;
    private BootstrapButton telefone;
    private BootstrapButton profissao;
    private RadioButton rbProfissional;
    private RadioButton rbCliente;
    private BootstrapButton btnCadastrar;
    private BootstrapButton btnCancelar;

    private FirebaseAuth autenticacao;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        email = findViewById(R.id.edtCadEmail);
        senha = findViewById(R.id.edtCadSenha);
        senha1 = findViewById(R.id.edtCadSenha1);
        nome = findViewById(R.id.edtCadNome);
        telefone = findViewById(R.id.edtCadTelefone);
        profissao = findViewById(R.id.edtCadProfissao);
        rbProfissional = findViewById(R.id.rbProfissional);
        rbCliente = findViewById(R.id.rbCliente);
        btnCadastrar = findViewById(R.id.btnRegistrar);
        btnCancelar = findViewById(R.id.btnCancelar);

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (senha.getText().toString().equals(senha1.getText().toString())) {
                    usuario = new Usuario();

                    usuario.setEmail(email.getText().toString());
                    usuario.setSenha(senha.getText().toString());
                    usuario.setNome(nome.getText().toString());
                    usuario.setTelefone(telefone.getText().toString());
                    usuario.setProfissao(profissao.getText().toString());
                    usuario.setTipoUsuario("Profissional");

                    if (rbProfissional.isChecked()) {
                        usuario.setTipoUsuario("Profissional");
                    } else if (rbCliente.isChecked()) {
                        usuario.setTipoUsuario("Cliente");
                    }
                    cadastrarUsuario();
                } else {
                    Toast.makeText(CadastroUsuarioActivity.this, "As senhas não correspondem", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CadastroUsuarioActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


    private void cadastrarUsuario(){
        autenticacao = ConfiguracaoFirebase.getFireBaseAuth();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(CadastroUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    insereUsuario(usuario);
                    finish();

                    //Deslogar ao adicionar o usuario
                    autenticacao.signOut();

                    //Abrir tela principal após a re-autenticação
                    abrirTelaPrincipal();
                }else{
                    String erroExcecao = "";

                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        erroExcecao = "Digite uma senha mais forte";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erroExcecao = "O email é inválido";
                    }catch (FirebaseAuthUserCollisionException e){
                        erroExcecao = "O email já esta cadastrado";
                    } catch (Exception e) {
                        erroExcecao = "Erro ao efetuar o cadastro";
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroUsuarioActivity.this, "Erro + " + erroExcecao, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean insereUsuario(Usuario usuario){
        try{
            databaseReference = ConfiguracaoFirebase.getFirebase().child("usuarios");
            String key = databaseReference.push().getKey();
            usuario.setKeyUsuario(key);
            databaseReference.child(key).setValue(usuario);
            //databaseReference.push().setValue(usuario);
            Toast.makeText(CadastroUsuarioActivity.this, "Usuario gravado com sucesso", Toast.LENGTH_SHORT).show();
            return true;

        }catch (Exception e){
            Toast.makeText(CadastroUsuarioActivity.this, "Erro ao gravar o usuario", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
    }

    private void abrirTelaPrincipal(){
        autenticacao = ConfiguracaoFirebase.getFireBaseAuth();
        Preferencias preferencias = new Preferencias(CadastroUsuarioActivity.this);

        autenticacao.signInWithEmailAndPassword(preferencias.getEmailUsuarioLogado(), preferencias.getSenhaUsuarioLogado()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(CadastroUsuarioActivity.this, PrincipalActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(CadastroUsuarioActivity.this, "Falha", Toast.LENGTH_LONG).show();
                    autenticacao.signOut();
                    Intent intent = new Intent(CadastroUsuarioActivity.this, MainActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        });
    }
}


