package com.Acdapp.app;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class AutoletturaFragment extends Fragment {


    Context context;
    /*non controllata la data*/
    private EditText txtDate = null;
    private  final Calendar myCalendar = Calendar.getInstance();
    static FirebaseUser user;

    private EditText codiceUtente;
    private EditText nomeUtente;
    private EditText cognomeUtente;
    private EditText valoreLettura;
    private String imagePath = "";
    private Uri pathInternal;

    private TextInputLayout textInputLayout7;

    static String codiceUser = null;
    public final static int PICK_IMAGE = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard

        View v = inflater.inflate(R.layout.fragment_autolettura, null);



        codiceUtente = (EditText) v.findViewById(R.id.txtCodiceUtente);
        nomeUtente = (EditText) v.findViewById(R.id.txtNomeUtente);
        cognomeUtente = (EditText) v.findViewById(R.id.txtCognomeUtente);
        valoreLettura = (EditText) v.findViewById(R.id.txtValoreLettura);

        textInputLayout7 = (TextInputLayout) v.findViewById(R.id.textInputLayout7) ;

        user =  FirebaseAuth.getInstance().getCurrentUser();

        txtDate =  (EditText) v.findViewById(R.id.txtDate) ;


        /*Setta nella text field  la data corrente*/
        String data = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        txtDate.setText(data.toString());

        /*codice utente su firebase*/
        codiceUser = user.getUid().toString();

        context = getContext();

        /*Image picker sul bottone carica foto*/
        v.findViewById(R.id.btnCaricaFoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                /*viene evocato il metodo sotto che esegue fisicamente il caricamneto dell'immagine au firebase*/
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        v.findViewById(R.id.btnInvia).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                if(codiceUtente.getText().toString().equals("")){
                    codiceUtente.setError("Errore");
                } */

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                if(checkSubmit()){
                    // Caricamento dati su firestore

                    Map<String, Object> user = new HashMap<>();

                    user.put("codiceUser",codiceUser);
                    user.put("codiceUtenteBolletta",codiceUtente.getText().toString());
                    user.put("nomeUtente",nomeUtente.getText().toString());
                    user.put("cognomeUtente",cognomeUtente.getText().toString());
                    /*controllo sui campi nome e cognome se errore la textfield si colora di rosso*/
                    /*chiedere come vogliono questo valore , con quante cifre significative*/
                    user.put("valoreLettura", valoreLettura.getText().toString());
                    user.put("data",txtDate.getText().toString());
                    user.put("imagePath",imagePath);



                    /**/
                    db.collection("Letture").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(context,"Lettura aquistia",Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,"Problemi in lettura",Toast.LENGTH_LONG).show();
                        }
                    });




                /* Inserimento della lettura del contatore all'evento click sul pulsante invia
                La lettura viene legata al codice UID dell'utente loggato
                *
                db.collection("Letture").document( FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .set(user, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("success", "DocumentSnapshot added with ID: ");
                        Toast toast = new Toast(context);
                        toast.makeText(context,"Lettura aquistia",Toast.LENGTH_LONG).show();

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("failure", "Error adding document", e);
                            }
                        });

                     */
                }else{
                    Toast.makeText(getContext(),"Controllare i campi in rosso",Toast.LENGTH_LONG).show();

                }
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            pathInternal = data.getData();
            Log.d("Uri", pathInternal.toString());
            caricaImagine();
        }
    }




    /*Data picker l'utente puo scegliere se mantenere la data corrente o cambiarla*/

    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };




    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALY);
        txtDate.setText(sdf.format(myCalendar.getTime()));
    }


    private void caricaImagine(){
        final ProgressDialog pd=new ProgressDialog(getContext());
        pd.setCancelable(false);
        pd.setMessage("Caricamento immagine");
        pd.show();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference riversRef = storage.getReference().child("images/" + pathInternal.getLastPathSegment());
        final UploadTask uploadTask = riversRef.putFile(pathInternal);

        final Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return riversRef.getDownloadUrl();

                // Handle failures
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    /*prendere questo percorso e immetterlo nella raccolta lettura come campo della
                     * singola lettura*/
                    Uri downloadUri = task.getResult();
                    Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();
                    //mostra il percorso della foto caricata

                    /*Metto nella variabile statica imagePath il percorso dell'immagine inserita dal'utente
                     * dopodiche imagePsth verra usata per la scrittura sul db*/
                    imagePath = downloadUri.toString();
                    Toast.makeText(context, downloadUri.toString() , Toast.LENGTH_LONG).show();

                    Log.d("Uri download",downloadUri.toString());
                    Log.d("Storage ref",task.getResult().getPath());

                    pd.dismiss();

                } else {
                    /*Errore nel caricamento dell'immagine*/
                    Toast.makeText(context, "Errore nel caricamento", Toast.LENGTH_LONG).show();
                }
            }
        });

            /*
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

            */
    }

    /*Metodi per il controllo dei campi effettuati tramite regex all'invio della lettura*/
    /*metodo utilizzato sia per il nome che per il cognome*/
    public static boolean validateNameSurname( String param )
    {
        return param.matches( "^[A-Za-zèùàòé][a-zA-Z'èùàòé ]*$");
    } // end method validateFirstName

    // ritorna vero se tutti i campi sono settati correttamente
    private boolean checkSubmit()
    {
        boolean ret=true;
        /* sul codice non puo esserci un controllo perchè sono dati appartenenti all'acda
         * quindi si controlla solo se il campo non è vuoto*/
        if(codiceUtente.getText().toString().equals("")) {
            ret = false;
            codiceUtente.setError("Error");
        }else{
            codiceUtente.setBackgroundResource(0);
        }
        if(!validateNameSurname(nomeUtente.getText().toString())){
            ret=false;
            nomeUtente.setError("Error: nome non valido");
        }else{
            nomeUtente.setBackgroundResource(0);
        }
        if(!validateNameSurname(cognomeUtente.getText().toString())){
            ret= false;
            cognomeUtente.setError("Error: cognome non valido");
        }else{
            cognomeUtente.setBackgroundResource(0);
        }
        if(valoreLettura.getText().toString().equals("")){

            ret=false;
            valoreLettura.setError("Error");
        }else{
            valoreLettura.setBackgroundResource(0);
        }
        if(imagePath.equals("")){
            ret=false;
            Toast.makeText(getContext(),"Per favore selezionare un'immagine della lettura"
                    ,Toast.LENGTH_LONG).show();
        }
        return ret;
    }
}
