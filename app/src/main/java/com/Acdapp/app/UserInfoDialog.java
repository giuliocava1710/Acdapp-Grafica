package com.Acdapp.app;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;





/* DESCRIZIONE:
Dialog creato dopo il login che prima di dare accesso alla main activity chiedi all'utente di confermare le informazioni
* in questo modo ci facciamo inserire nome e cognome come due informazioni divise e le poniamo nella tabella dell'anagrafica utenti
* nel database*/

public class UserInfoDialog extends DialogFragment {

    UserInfoDialogListener mListener = null;

    private TextInputEditText nomeUtente = null;
    private TextInputEditText cognomeUtente = null;
    private TextInputEditText mailUtente = null;
    private TextInputEditText telefonoUtente = null;

    public boolean valoreControllo;

    public interface UserInfoDialogListener {
        void onUserInfoDialogOkPressed(String nome, String cognome,String mail,String telefono,Boolean ret);
    }
    /*costruttore dialog*/
    static public UserInfoDialog newInstance() {
        return new UserInfoDialog();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            if(getActivity() != null)
                // Viene collegato il chiamante
                mListener = (UserInfoDialogListener) getActivity();
            else throw new ClassCastException();
        } catch (ClassCastException e) {
            throw  new ClassCastException(UserInfoDialog.class + ": Deve essere implementata l'interfaccia di comunicazione nel chiamante");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentManager fm = getChildFragmentManager();
        final View vM = inflater.inflate(R.layout.user_info_dialog, container, false); //TODO

        nomeUtente = (TextInputEditText) vM.findViewById(R.id.txtNomeDIalog);
        cognomeUtente = (TextInputEditText) vM.findViewById(R.id.txtCognomeDialog);
        mailUtente = (TextInputEditText) vM.findViewById(R.id.txtMailDialog);
        telefonoUtente = (TextInputEditText) vM.findViewById(R.id.txtTelefonoDialog);

        /*prendo dati dal bundle*/

        Bundle bundle = getArguments();
        ArrayList infoUtente = (ArrayList) bundle.getSerializable("UserBundle");

        /*i dati che si possono prendere dallo user li pongo gia nei campi di del dialog
         * una sorta di precompilazione con le informazioni che gia possediamo dell'utente
         * delle quali poi chiederemo conferma*/



        String splitNomeCognome []= infoUtente.get(0).toString().split(" ");
        nomeUtente.setText(splitNomeCognome[0],TextView.BufferType.EDITABLE);
        cognomeUtente.setText(splitNomeCognome[1],TextView.BufferType.EDITABLE);

        mailUtente.setText(infoUtente.get(1).toString(),TextView.BufferType.EDITABLE);
        telefonoUtente.setText(infoUtente.get(2).toString(),TextView.BufferType.EDITABLE);




        /*EditText editText = (EditText)findViewById(R.id.edit_text);
        editText.setText("Google is your friend.", TextView.BufferType.EDITABLE);*/

        vM.findViewById(R.id.btnConfermaDialog).setOnClickListener(new View.OnClickListener() {

            /*Caricamento dati utente sul database*/
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            @Override
            public void onClick(View v) {



                /*controllo dei campi tramite apposito metodo*/
                if (controllo()) {

                    Toast.makeText(getContext(),"Dati inseriti correttamente",Toast.LENGTH_LONG).show();
                    // Caricamento dati su firestore
                    Map<String, Object> user = new HashMap<>();
                    user.put("nomeUtente", nomeUtente.getText().toString());
                    user.put("cognomeUtente", cognomeUtente.getText().toString());
                    user.put("mailUtente", mailUtente.getText().toString());
                    user.put("telefonoUtente", telefonoUtente.getText().toString());


                /* Inserimento della lettura del contatore all'evento click sul pulsante invia
                La lettura viene legata al codice UID dell'utente loggato
                * */
                    db.collection("user").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
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


                } else {
                    /*errore */
                    Toast.makeText(getContext(),"Controllare i dati",Toast.LENGTH_LONG);
                }

                mListener.onUserInfoDialogOkPressed(nomeUtente.getText().toString(),
                        cognomeUtente.getText().toString(),
                        mailUtente.getText().toString(),
                        telefonoUtente.getText().toString(),valoreControllo);

            }

        });
        return vM;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public boolean controllo(){
        valoreControllo = true;
        if(!AutoletturaFragment.validateNameSurname(nomeUtente.getText().toString())){
            valoreControllo=false;
            nomeUtente.setError("Error: nome non valido!");
        }else{
            nomeUtente.setBackgroundResource(0);
        }
        if(!AutoletturaFragment.validateNameSurname(cognomeUtente.getText().toString())){
            valoreControllo= false;
            cognomeUtente.setError("Error: cognome non valido!");
        }else{
            cognomeUtente.setBackgroundResource(0);
        }
        if(mailUtente.getText().toString().equals("")){
            valoreControllo=false;
            mailUtente.setError("Error: mail non valida!");
        }else{
            mailUtente.setBackgroundResource(0);
        }
        if(!validatePhone(telefonoUtente.getText().toString())){
            valoreControllo=false;
            telefonoUtente.setError("Error: telefono non valido!");
        }else{
            telefonoUtente.setBackgroundResource(0);
        }
        return  valoreControllo;
    }

    /*controllo campo telefono con regex (per telefoni italiani)*/
    public static boolean validatePhone( String param )
    {
        return param.matches( "^(([+]|00)39)?((3[1-6][0-9]))(\\d{7})$" );
    }
}

/*bisogna vedere come indicare i campi obbligatori  che l'utente deve confermare o evetualmente inserire
 * incare con un asterisco e controllare la validatà dei campi , quidi inserire dati nel db
 * 1) aggiungere delle label che indicano i campi da confermare ,per ora ci sono solo text box
 * 2)controllare campo telefono se viene settato oppure no
 * 3) vedere questione fragment e storico letture , possibilità all utetente di fare piu lettura
 * adesso se l'utente immette una due letture la seconda sovrascrive la prima*/ /// TODO