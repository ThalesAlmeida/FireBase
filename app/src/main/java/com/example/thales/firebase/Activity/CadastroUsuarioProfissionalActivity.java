package com.example.thales.firebase.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.thales.firebase.Classes.Usuario;
import com.example.thales.firebase.DAO.ConfiguracaoFirebase;
import com.example.thales.firebase.Helper.Preferencias;
import com.example.thales.firebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CadastroUsuarioProfissionalActivity extends AppCompatActivity {

    private BootstrapEditText nome;
    private BootstrapEditText email;
    private BootstrapEditText senha;
    private BootstrapEditText senha1;
    private BootstrapEditText telefone;
    private BootstrapEditText profissao;
    private String profissoes;
    private List<String> profissoesList = new ArrayList<String>();

    private Spinner spinnerProfissoes;

    private RadioButton rbProfissional;
    private RadioButton rbCliente;

    private BootstrapButton btnRegistrar;
    private BootstrapButton btnCancelar;

    private FirebaseAuth autenticacao;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario_profissional);

        email = findViewById(R.id.edtCadEmail);
        senha = findViewById(R.id.edtCadSenha);
        senha1 = findViewById(R.id.edtCadSenha1);
        telefone = findViewById(R.id.edtCadTelefone);
        nome = findViewById(R.id.edtCadNome);
        profissao = findViewById(R.id.edtCadProfissao);

        spinnerProfissoes = findViewById(R.id.spinnerProfissoes);

        profissoesList.add("Pedreiro");
        profissoesList.add("Encanador");
        profissoesList.add("Carpinteiro");

        rbProfissional = findViewById(R.id.rbProfissional);
        rbCliente = findViewById(R.id.rbCliente);

        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnCancelar = findViewById(R.id.btnCancelar);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, profissoesList);
        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerProfissoes.setAdapter(spinnerArrayAdapter);

        spinnerProfissoes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
                });

                btnRegistrar.setOnClickListener(new View.OnClickListener() {
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
                            Toast.makeText(CadastroUsuarioProfissionalActivity.this, "As senhas não correspondem", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CadastroUsuarioProfissionalActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


    private void cadastrarUsuario(){
        autenticacao = ConfiguracaoFirebase.getFireBaseAuth();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(CadastroUsuarioProfissionalActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    insereUsuario(usuario);

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

                    Toast.makeText(CadastroUsuarioProfissionalActivity.this, "Erro + " + erroExcecao, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean insereUsuario(Usuario usuario){
        try{
            databaseReference = ConfiguracaoFirebase.getFirebase().child("usuariosProfissional");
            String key = databaseReference.push().getKey();
            usuario.setKeyUsuario(key);
            databaseReference.child(key).setValue(usuario);
            Toast.makeText(CadastroUsuarioProfissionalActivity.this, "Usuario gravado com sucesso", Toast.LENGTH_SHORT).show();
            abrirLoginProfissional();
            return true;

        }catch (Exception e){
            Toast.makeText(CadastroUsuarioProfissionalActivity.this, "Erro ao gravar o usuario", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
    }

    private void abrirLoginProfissional() {
            Intent intent = new Intent(CadastroUsuarioProfissionalActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
    }
}

