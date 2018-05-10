package com.example.thales.firebase.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.thales.firebase.Classes.Usuario;
import com.example.thales.firebase.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.ViewHolder>{

    private List<Usuario> mUsuarioList;
    private Context context;
    private DatabaseReference databaseReference;
    private List<Usuario> usuarios;
    private Usuario todosUsuarios;

    public UsuariosAdapter(List<Usuario> l, Context context) {
        this.mUsuarioList = l;
        this.context = context;
    }

    @NonNull
    @Override
    public UsuariosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_todos_usuarios, parent, false);

        return new UsuariosAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final UsuariosAdapter.ViewHolder holder, int position) {
        final Usuario item = mUsuarioList.get(position);

        usuarios = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("usuariosProfissional");
        databaseReference.child("usuariosProfissional").orderByChild("keyUsuario").equalTo(item.getKeyUsuario()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usuarios.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    todosUsuarios = postSnapshot.getValue(Usuario.class);

                    usuarios.add(todosUsuarios);

                    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                    final int height = (displayMetrics.heightPixels / 4);
                    final int width = (displayMetrics.widthPixels / 2);

                    Picasso.get().load(todosUsuarios.getUrlImagem()).resize(width,height).centerCrop().into(holder.fotoUsuario);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.txtNomeUsuario.setText("Nome do profissional: " + item.getNome());
        holder.txtTelefoneUsuario.setText("Telefone do profissional: " + item.getTelefone());
        holder.txtEmailUsuario.setText("Email do profissional: "+ item.getEmail());
        holder.txtProfissaoUsuario.setText("Ocupação do profissional: "+ item.getProfissao());

        holder.linearLayoutUsuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsuarioList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView txtNomeUsuario;
        protected TextView txtTelefoneUsuario;
        protected TextView txtEmailUsuario;
        protected TextView txtProfissaoUsuario;
        protected ImageView fotoUsuario;

        protected LinearLayout linearLayoutUsuarios;

        public ViewHolder(View itemView) {
            super(itemView);

            txtNomeUsuario = itemView.findViewById(R.id.txtNomeUsuario);
            txtTelefoneUsuario = itemView.findViewById(R.id.txtTelefoneUsuario);
            txtEmailUsuario = itemView.findViewById(R.id.txtEmailUsuario);
            txtProfissaoUsuario = itemView.findViewById(R.id.txtProfissaoUsuario);

            fotoUsuario = itemView.findViewById(R.id.fotoUsuario);
            linearLayoutUsuarios = itemView.findViewById(R.id.linearLayoutUsuarios);
        }
    }
}
