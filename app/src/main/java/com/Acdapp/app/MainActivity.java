package com.Acdapp.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity<setOnClickListener> extends AppCompatActivity  /*implements UserInfoDialog.UserInfoDialogListener*/ {

    Context context;
    //UserInfoDialog dialog;

    public final static int PICK_IMAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //FirebaseApp.initializeApp(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("USERINFO", "Nome : " + user.getDisplayName() + " Codie : " + user.getUid());

        context = getApplicationContext();

        /*Passo oggetto user a dialog*/
        ArrayList <String>infoUtente = new ArrayList<>();

        /*prendo informazionin dell'utente loggato*/
        infoUtente.add(0,user.getDisplayName());
        infoUtente.add(1,user.getEmail());
        infoUtente.add(2,user.getPhoneNumber());


        /*toast che verifica il valore del numero , l'altra volta dava errore */
        if(infoUtente.get(2)!= null){
            Toast toast = new Toast(context);
            toast.makeText(context,infoUtente.get(2).toString(),Toast.LENGTH_LONG);
        }


       //dialog = UserInfoDialog.newInstance();

        Bundle bundle = new Bundle();
        bundle.putSerializable("UserBundle", infoUtente);
        //dialog.setArguments(bundle);


        /*evita che con un doppio tap l'utente skippi il dialog*/
        //dialog.setCancelable(false);
        //dialog.show(getSupportFragmentManager(), "USER INFO DIALOG");

        findViewById(R.id.btnCaricaFoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        findViewById(R.id.btnInvia).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inizializzo un'istanza di Cloud Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                EditText codiceUtente = (EditText) findViewById(R.id.txtCodiceUtente);
                EditText nomeUtente = (EditText) findViewById(R.id.txtNomeUtente);
                EditText cognomeUtente = (EditText) findViewById(R.id.txtCognomeUtente);
                EditText valoreLettura = (EditText) findViewById(R.id.txtValoreLettura);

                // Caricamento dati su firestore
                Map<String, Object> user = new HashMap<>();
                user.put("codiceUtente",codiceUtente.getText().toString());
                user.put("nomeUtente",nomeUtente.getText().toString());
                user.put("cognomeUtente", cognomeUtente.getText().toString());
                user.put("valoreLettura", valoreLettura.getText().toString());


                /* Inserimento della lettura del contatore all'evento click sul pulsante invia
                La lettura viene legata al codice UID dell'utente loggato
                * */
                db.collection("Letture").document( FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .set(user, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("success", "DocumentSnapshot added with ID: ");
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("failure", "Error adding document", e);
                            }
                        });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            Uri path = data.getData();

            Log.d("Uri", path.toString());

            FirebaseStorage storage = FirebaseStorage.getInstance();
            final StorageReference riversRef = storage.getReference().child("images/" + path.getLastPathSegment());
            final UploadTask uploadTask = riversRef.putFile(path);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Fail", Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("Download Uri", uri.toString());

                        }
                    });
                    Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    /*il metodin del dialog sono utilizzabili da questa activity perchè è come se fosse
    * un interfaccia che viene implementata in questa activity
    @Override */
    public void onUserInfoDialogOkPressed(String nome, String cognome,String mail,String telefono) {
        /* una volta confermate le informazioni tramite il dialog da questo metodo occorre
        * caricare le informazioni  dell'utente su firebase */ //TODO
        Log.d("USERINFODIALOG", nome + " " + cognome + " " + mail + "" + telefono );
        //dialog.dismiss();
    }


}
