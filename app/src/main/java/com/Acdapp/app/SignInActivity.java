package com.Acdapp.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.firebase.ui.auth.ui.idp.SingleSignInActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;


import java.util.Arrays;
import java.util.List;


public class SignInActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_sign_in);
        // Inizializzazione dei servizi di Firebase (richiesto)
        FirebaseApp.initializeApp(this);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(SignInActivity.this, Main2Activity.class));
            finish();
        } else {
            // Log-in non effettuato
            // Selezione del layout personalizzato per la procedura di accesso
            /*AuthMethodPickerLayout customLoginLayout = new AuthMethodPickerLayout
                    .Builder(R.layout.activity_sign_in)
                    .setGoogleButtonId(R.id.google_button) // Selezione dell'id pulsante di accesso con Google nel layout
                    .setEmailButtonId(R.id.email_button) // Selezione dell'id del pulsante di accesso con l'email nel layout
                    .build();
            */


            // You must provide a custom layout XML resource and configure at least one
            // provider button ID. It's important that that you set the button ID for every provider
            // that you have enabled.


            AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                    .Builder(R.layout.activity_sign_in)
                    .setGoogleButtonId(R.id.btnGoogle)
                    .setEmailButtonId(R.id.btnEmail)
                    .build();


            // Selezione dei metodi di accesso consentiti
            List<AuthUI.IdpConfig> availableProviders = Arrays.asList(
                    new AuthUI.IdpConfig.GoogleBuilder().build(), // Servizio di accesso tramite Google
                    new AuthUI.IdpConfig.EmailBuilder().build()); // Servizio di accesso tramite email


            startActivityForResult(
                    AuthUI.getInstance().createSignInIntentBuilder()
                            .setAuthMethodPickerLayout(customLayout)
                            .setAvailableProviders(availableProviders)//.setTheme(R.style.AppTheme) // Impostazione del tema da applicare al layout
                            .setIsSmartLockEnabled(false)// Disattivazione di smartLock
                            .setAlwaysShowSignInMethodScreen(true) // Impostazione dei servizi disponibili
                            //.setAuthMethodPickerLayout(customLoginLayout) // Impostazione del layout personzalizato
                            //.setTheme(R.style.AppTheme) // Impostazione del tema da applicare al layout
                            //.setIsSmartLockEnabled(false)// Disattivazione di smartLock
                            //.setAlwaysShowSignInMethodScreen(true)
                            .build(),
                    RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN) {
            // Activity login
            // Istanziazione oggetto che gestisce le risposte ritornate dall'activity di login
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if(resultCode == RESULT_OK) {
                // Acceduto correttamente
                // Apertura dell'activity delle impostazioni
                startActivity(new Intent(SignInActivity.this, Main2Activity.class));
                finish();
            } else {
                // Errore nell'accesso
                if(response == null) {
                    // E' stato premuto il tasto indietro, chiusura dell'applicazione
                    // TODO: uscire utilizzando l'animazione standard di uscita
                    this.finish();
                    System.exit(0);
                }

                if(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    // E' stato generato un errore
                    // TODO: gestire varie casistiche di errore
                }

            }
        }
    }
}
