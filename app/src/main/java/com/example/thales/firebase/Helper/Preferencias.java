package com.example.thales.firebase.Helper;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferencias {

    private Context context;
    private SharedPreferences preferences;
    private String NOME_ARQUIVO = "app.preferences";
    private int MODE = 0;
    private SharedPreferences.Editor editor;

    private final String EMAIL_USUARIO_LOGADO = "email_usuario_logado";
    private final String SENHA_USUARIO_LOGADO = "senha_usuario_logado";

    public Preferencias(Context contextParametro) {
        context = contextParametro;

        preferences = context.getSharedPreferences(NOME_ARQUIVO, MODE);

        //Associar o nosso preferences edit()
        editor = preferences.edit();
    }

    public void salvarUsuarioPreferences(String email, String senha){

        //Salvar no arquivo o email e a senha do usuario logado

        editor.putString(EMAIL_USUARIO_LOGADO, email);
        editor.putString(SENHA_USUARIO_LOGADO, senha);
        editor.commit();
    }
    public String getEmailUsuarioLogado(){
        return preferences.getString(EMAIL_USUARIO_LOGADO, null);
    }

    public String getSenhaUsuarioLogado(){
        return preferences.getString(SENHA_USUARIO_LOGADO, null);
    }

}
