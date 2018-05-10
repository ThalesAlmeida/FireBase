package com.example.thales.firebase.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.thales.firebase.Adapter.UsuariosAdapter;
import com.example.thales.firebase.Classes.Usuario;
import com.example.thales.firebase.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsuariosActivity extends AppCompatActivity {

    private RecyclerView mRecyclerViewUsuarios;

    private UsuariosAdapter adapter;

    private List<Usuario> usuarios;

    private DatabaseReference databaseReference;

    private Usuario todosUsuarios;

    private LinearLayoutManager mLinearLayoutManagerTodosUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);

        mRecyclerViewUsuarios = findViewById(R.id.recyclerViewTodosUsuarios);

        carregarTodosUsuarios();
    }

    public void carregarTodosUsuarios(){
        mRecyclerViewUsuarios.setHasFixedSize(true);
        mLinearLayoutManagerTodosUsuarios = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);

        mRecyclerViewUsuarios.setLayoutManager(mLinearLayoutManagerTodosUsuarios);

        usuarios = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("usuariosProfissional").orderByChild("nomeUsuario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    todosUsuarios = postSnapshot.getValue(Usuario.class);

                    usuarios.add(todosUsuarios);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        adapter = new UsuariosAdapter(usuarios, this);
        mRecyclerViewUsuarios.setAdapter(adapter);
    }
}
