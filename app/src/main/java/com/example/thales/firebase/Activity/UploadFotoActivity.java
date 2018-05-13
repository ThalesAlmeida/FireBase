package com.example.thales.firebase.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.example.thales.firebase.DAO.ConfiguracaoFirebase;
import com.example.thales.firebase.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class UploadFotoActivity extends AppCompatActivity {

    private BootstrapButton btnUpload;
    private BootstrapButton btnCancelar;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth autenticacao;
    private ImageView imageView;
    private String emaiUsuarioLogado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_foto);

        storageReference = ConfiguracaoFirebase.getFirebaseStorageReference();

        autenticacao = ConfiguracaoFirebase.getFireBaseAuth();

        emaiUsuarioLogado = autenticacao.getCurrentUser().getEmail();

        imageView = findViewById(R.id.imagemCadFotoPerfil);
        carregaImagemPadrao();


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent.createChooser(intent, "Seleciona uma imagem"), 123);
            }
        });
        btnUpload = findViewById(R.id.btnUploadFoto);
        btnCancelar = findViewById(R.id.btnCancelar);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastrarFotoUsuario();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chamarTelaPrincipal();
            }
        });
    }

    private void cadastrarFotoUsuario(){
        StorageReference montaImagemReferencia = storageReference.child("fotoPerfilUsuario/" + emaiUsuarioLogado + ".jpg");

        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();

        Bitmap bitmap = imageView.getDrawingCache();

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray);
        byte[] data = byteArray.toByteArray();

        UploadTask uploadTask = montaImagemReferencia.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Toast.makeText(UploadFotoActivity.this, "Foto cadastrada com sucesso", Toast.LENGTH_SHORT).show();
                carregaImagemPadrao();
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final int height = 300;
        final int width = 300;

        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == 123) {
                Uri imagemSelecionada = data.getData();
                Picasso.get().load(imagemSelecionada.toString()).resize(width, height).centerCrop().into(imageView);
            }
        }
    }

    private void carregaImagemPadrao(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageReference = storage.getReferenceFromUrl("gs://androidfirebase-5224a.appspot.com/fotoPerfilUsuario/" + emaiUsuarioLogado + ".jpg");

        final int height = 300;
        final int width = 300;


        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri.toString()).resize(width, height).centerCrop().into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void chamarTelaPrincipal(){
        Intent intent = new Intent(UploadFotoActivity.this, PrincipalActivityCliente.class);
        startActivity(intent);
        finish();
    }
}
