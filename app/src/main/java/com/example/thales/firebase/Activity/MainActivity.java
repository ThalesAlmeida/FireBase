package com.example.thales.firebase.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.thales.firebase.Classes.Usuario;
import com.example.thales.firebase.DAO.ConfiguracaoFirebase;
import com.example.thales.firebase.Helper.Preferencias;
import com.example.thales.firebase.R;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private BootstrapEditText edtEmailLogin;
    private BootstrapEditText edtSenhaLogin;
    private BootstrapButton btnLogin;
    private BootstrapButton btnRegistrarProfissional;
    private BootstrapButton btnRegistrarCliente;
    private TextView txtRecuperarSenha;
    private AlertDialog alertDialog;
    private Usuario usuario;

    private BootstrapButton btnGoogle;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        edtEmailLogin = findViewById(R.id.edtEmail);
        edtSenhaLogin = findViewById(R.id.edtSenha);
        btnLogin = findViewById(R.id.btnLogin);
        txtRecuperarSenha = findViewById(R.id.txtRecuperarSenha);
        btnRegistrarProfissional = findViewById(R.id.btnRegistrarProfissional);
        btnRegistrarCliente = findViewById(R.id.btnRegistrarCliente);
        btnGoogle = findViewById(R.id.btnRegistrarGoogle);

        final EditText editTextEmail = new EditText(MainActivity.this);
        editTextEmail.setHint("exemplo@exemplo.com");


        permission();

        if(usuarioLogado()){
            Intent intentMinhaConta = new Intent(MainActivity.this, PrincipalActivityProfissional.class);
            abrirNovaActivity(intentMinhaConta);
        }else{

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edtEmailLogin.getText().toString().equals("") && !edtSenhaLogin.getText().toString().equals("")){

                    usuario = new Usuario();

                    usuario.setEmail(edtEmailLogin.getText().toString());
                    usuario.setSenha(edtSenhaLogin.getText().toString());

                    validaLogin();

                }else{
                    Toast.makeText(MainActivity.this, "Por favor, preencha os campos de email e senha", Toast.LENGTH_SHORT).show();
                }
            }
        });
        }

        btnRegistrarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CadastroUsuarioClienteActivity.class);
                startActivity(intent);
            }
        });

        btnRegistrarProfissional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CadastroUsuarioProfissionalActivity.class);
                startActivity(intent);
            }
        });

        txtRecuperarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setCancelable(false);

                builder.setTitle("Recuperar Senha");

                builder.setMessage("Informe o seu email");

                builder.setView(editTextEmail);

                if(!editTextEmail.getText().equals("")){
                    builder.setPositiveButton("Recuperar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, final int i) {
                            autenticacao = FirebaseAuth.getInstance();

                            String emailRecuperar = editTextEmail.getText().toString();

                            autenticacao.sendPasswordResetEmail(emailRecuperar).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(MainActivity.this, "Você receberá um email", Toast.LENGTH_SHORT).show();
                                        Intent intent = getIntent();
                                        finish();
                                        startActivity(intent);

                                    }else{
                                        Toast.makeText(MainActivity.this, "Falha ao enviar o email, cadastre-se", Toast.LENGTH_SHORT).show();

                                        Intent intent = getIntent();
                                        finish();
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                    });

                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    });
                }else{
                    Toast.makeText(MainActivity.this, "Preencha o campo de email", Toast.LENGTH_SHORT).show();
                }

                alertDialog = builder.create();
                alertDialog.show();
            }
        });

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }



    private void validaLogin(){
        autenticacao = ConfiguracaoFirebase.getFireBaseAuth();
        autenticacao.signInWithEmailAndPassword(usuario.getEmail().toString(),usuario.getSenha().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if(task.isSuccessful()){
                    abrirTelaPrincipal();
                    Preferencias preferencias = new Preferencias(MainActivity.this);

                    preferencias.salvarUsuarioPreferences(usuario.getEmail(), usuario.getSenha());

                    Toast.makeText(MainActivity.this, "Login efetuado com sucesso", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "Usuário ou senha incorretos, tente novamente", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void abrirTelaPrincipal(){
        Intent intent = new Intent(MainActivity.this, PrincipalActivityCliente.class);
        finish();
        startActivity(intent);

    }

    public boolean usuarioLogado(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user !=null){
            return true;
        }else{
            return false;
        }
    }

    public void abrirNovaActivity(Intent intent){
        startActivity(intent);
    }

    public void abrirTelaCadastro(){

    }

    public void permission(){
        int PERMISSION_ALL = 1;
        String [] PERMISSION = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, PERMISSION, PERMISSION_ALL);
    }

}
