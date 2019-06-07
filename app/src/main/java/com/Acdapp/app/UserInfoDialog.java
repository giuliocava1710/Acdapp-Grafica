
package com.Acdapp.app;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/*bisogna vedere come indicare i campi obbligatori  che l'utente deve confermare o evetualmente inserire
* incare con un asterisco e controllare la validatà dei campi , quidi inserire dati nel db
* 1) aggiungere delle label che indicano i campi da confermare ,per ora ci sono solo text box///TODO


/*dialog creato dopo il login che prima di dare accesso alla main activity chiedi all'utente di confermare le informazioni
* in questo modo ci facciamo inserire nome e cognome come due informazioni divise e le poniamo nella tabella dell'anagrafica utenti
* nel database*/


public class UserInfoDialog extends DialogFragment {

    UserInfoDialogListener mListener = null;

    public interface UserInfoDialogListener {
        void onUserInfoDialogOkPressed(String nome, String cognome,String mail,String telefono);
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
        final View vM = inflater.inflate(R.layout.user_info_diaglog, container, false); //TODO


/*prendo dati dal bundle*/


        Bundle bundle = getArguments();
        ArrayList infoUtente = (ArrayList) bundle.getSerializable("UserBundle");


/*i dati che si possono prendere dallo user li pongo gia nei campi di del dialog
        * una sorta di precompilazione con le informazioni che gia possediamo dell'utente
        * delle quali poi chiederemo conferma*/


        EditText txtNome =(EditText) vM.findViewById(R.id.txtNomeDialog);
        EditText txtCognome =(EditText) vM.findViewById(R.id.txtCognomeDialog);

        String splitNomeCognome []= infoUtente.get(0).toString().split(" ");
        txtNome.setText(splitNomeCognome[0],TextView.BufferType.EDITABLE);
        txtCognome.setText(splitNomeCognome[1],TextView.BufferType.EDITABLE);


        EditText txtMail =(EditText) vM.findViewById(R.id.txtMailDialog);
        txtMail.setText(infoUtente.get(1).toString(),TextView.BufferType.EDITABLE);

        EditText txtTelefono = vM.findViewById(R.id.txtTelefonoDialog);
        txtTelefono.setText(infoUtente.get(2).toString(),TextView.BufferType.EDITABLE);




/*EditText editText = (EditText)findViewById(R.id.edit_text);
        editText.setText("Google is your friend.", TextView.BufferType.EDITABLE);*/

        vM.findViewById(R.id.btnConfermaDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void

            onClick(View v) {

                EditText nomeUtente = (EditText) vM.findViewById(R.id.txtNomeDialog);
                EditText cognomeUtente = (EditText) vM.findViewById(R.id.txtCognomeDialog);
                EditText mailUtente = (EditText) vM.findViewById(R.id.txtMailDialog);
                EditText telefonoUtente = (EditText) vM.findViewById(R.id.txtTelefonoDialog);

                mListener.onUserInfoDialogOkPressed( nomeUtente.getText().toString(),
                                                    cognomeUtente.getText().toString(),
                                                    mailUtente.getText().toString(),
                                                     telefonoUtente.getText().toString());


/*Caricamento dati utente sul database*/

                FirebaseFirestore db = FirebaseFirestore.getInstance();



                // Caricamento dati su firestore*/
                Map<String, Object> user = new HashMap<>();
                user.put("nomeUtente",nomeUtente.getText().toString());
                user.put("cognomeUtente", cognomeUtente.getText().toString());
                user.put("mailUtente", mailUtente.getText().toString());
                user.put("telefonoUtente", telefonoUtente.getText().toString());



/*Inserimento della lettura del contatore all'evento click sul pulsante invia
                La lettura viene legata al codice UID dell'utente loggato
                * */

                db.collection("user").document( FirebaseAuth.getInstance().getCurrentUser().getUid())
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
        return vM;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}

