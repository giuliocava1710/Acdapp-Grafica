package com.Acdapp.app;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.MenuItem;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {
    private TextView mTextMessage;
    private NavController nav = null;

    /*pubblica perchè viene usata nel fragment storico*/
    public static String codiceUser;

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
    }

}
