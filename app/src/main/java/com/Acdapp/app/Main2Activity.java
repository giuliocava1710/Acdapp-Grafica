package com.Acdapp.app;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity implements UserInfoDialog.UserInfoDialogListener {

    private TextView mTextMessage;
    private NavController nav = null;

    /*pubblica perchè viene usata nel fragment storico*/
    public static String codiceUser;
    public static FirebaseUser user;
    private Context context;
    UserInfoDialog dialog;

    /*Gestione fragment tramite botton navigation bar*/
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    nav.navigate(R.id.action_global_homeFragment);
                    return true;
                case R.id.navigation_storico:
                    nav.navigate(R.id.action_global_storicoFragment);
                    return true;
                case R.id.navigation_autolettura:
                    nav.navigate(R.id.action_global_autoletturaFragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);

        nav = Navigation.findNavController(this, R.id.nav_host_fragment);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        user =  FirebaseAuth.getInstance().getCurrentUser();
        context = getApplicationContext();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userid = user.getUid();

        /*controllo se l'utente è gia stato loggato e quindi ha gia inserito le sue informazioni nel database altrimenti
         * procedo a offrire l'interfaccia per eseguire le autoletture*/
        db.collection("user").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(!doc.exists()){
                        /*L'utente non è presente nel db perciò deve confermare le sue credenziali*/

                        /*Passo oggetto user a dialog*/
                        ArrayList<String> infoUtente = new ArrayList<>();

                        /*prendo informazionin dell'utente loggato*/
                        infoUtente.add(0,user.getDisplayName());
                        infoUtente.add(1,user.getEmail());


                        /*Se il telefono dell'utente è disponibile viene estratto e inserito nel
                         * db altrimenti si inserisce una stringa vuota */
                        if(user.getPhoneNumber()!= null){
                            Toast toast = new Toast(context);
                            toast.makeText(context,user.getPhoneNumber().toString(),Toast.LENGTH_LONG);
                            infoUtente.add(2,user.getPhoneNumber());
                        }else{
                            infoUtente.add(2,"");
                        }

                        /*il codice utente viene preipostato e posto nel in un campo della raccolta letture
                         * le letture avranno un codice che viene creato automaticamente da firebase*/


                        /*instanzia il dialog per conferma delle informazioni*/
                        dialog = UserInfoDialog.newInstance();

                        final Bundle bundle = new Bundle();
                        /*passaggio dei dati al dialog tramite Bundle*/
                        bundle.putSerializable("UserBundle", infoUtente);
                        dialog.setArguments(bundle);


                        /*evita che con un doppio tap l'utente skippi il dialog*/
                        dialog.setCancelable(false);
                        dialog.show(getSupportFragmentManager(), "USER INFO DIALOG");



                    }
                }
            }
        });

        /*codice utente su firebase*/
        codiceUser = user.getUid().toString();


    }

    /*il metodin del dialog sono utilizzabili da questa activity perchè è come se fosse
     * un interfaccia che viene implementata in questa activity */
    @Override
    public void onUserInfoDialogOkPressed(String nome, String cognome,String mail,String telefono,Boolean ret) {
        /* una volta confermate le informazioni tramite il dialog da questo metodo occorre
         * caricare le informazioni  dell'utente su firebase */ //TODO

        Log.d("USERINFODIALOG", nome + " " + cognome + " " + mail + "" + telefono );
        if(ret){
            dialog.dismiss();
        }else{
            Toast.makeText(getApplicationContext(),"Controllare campi",Toast.LENGTH_LONG).show();
        }

    }

}
