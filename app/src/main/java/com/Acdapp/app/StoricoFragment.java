package com.Acdapp.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StoricoFragment extends Fragment {

    private ArrayList<Lettura> listaLetture = null;

    /*proprietà di nostro interesse*/
    private RecyclerView recyclerView ;
    private AdapterLetture adapterLetture;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard

        View v = inflater.inflate(R.layout.lista_letture, container, false);


        recyclerView = v.findViewById(R.id.lista_letture);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));




        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query capitalCities = db.collection("Letture").whereEqualTo("codiceUser", Main2Activity.codiceUser);
        capitalCities.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                listaLetture = new ArrayList<>();
                List<DocumentSnapshot> query = Objects.requireNonNull(task.getResult()).getDocuments();
                for (DocumentSnapshot document : query)
                    listaLetture.add(new Lettura(document));
            }

        });

        adapterLetture = new AdapterLetture(this.getContext(), listaLetture);
        recyclerView.setAdapter(adapterLetture);

        return v;
    }
}
