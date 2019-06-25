package com.Acdapp.app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/* DESCRIZIONE:
* Questa classe crea una struttura composta da tanti oggetti di tipo letture quante sono le letture relative
* all'utente loggato presenti nel db. Questa struttura dovra essere compatible quindi riuscire a lavorare con
* la Recycler View presente nel layout del FragmentStoricoLetture*/

public class AdapterLetture extends RecyclerView.Adapter<AdapterLetture.LettureViewHolder> {

    private Context mCtx;
    private ArrayList<Lettura> listaLetture;

    public AdapterLetture(Context mCtx, ArrayList<Lettura> listaLetture) {
        this.mCtx = mCtx;
        this.listaLetture = listaLetture;

        //for(Lettura lettura : listaLetture) Log.d("LETTURA", lettura.toString());
    }

    @NonNull
    @Override
    public LettureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.lista_letture, parent, false);
        return new LettureViewHolder(LayoutInflater.from(mCtx).inflate(R.layout.storico_letture,
                null, false));
    }

    /* Dal nostro Arraylist contenente tutti i dati delle letture lette dal db genero questi oggetti
    * i quali graficamente saranno delle card che verranno disposti uno sotto l'altro nella
    * recycler View del FragmentStoricoLetture */
    @Override
    public void onBindViewHolder(@NonNull LettureViewHolder holder, int position) {
        Lettura lettura = listaLetture.get(position);
        holder.codiceUser.setText("Codice User: "+lettura.codiceUser);
        holder.codiceUtenteBolletta.setText("Codice Utente: "+lettura.codiceUtenteBolletta);
        holder.nomeUtente.setText("Nome: "+lettura.nomeUtente);
        holder.cognomeUtente.setText("Cognome: "+lettura.cognomeUtente);
        holder.valoreLettura.setText("Valore lettura: " + lettura.valoreLettura);
        holder.data.setText("Data: " + lettura.data);

        Picasso.get().load(lettura.imagePath).into(holder.imagePath);
        //holder.imagePath.setText("Path Immagine" + lettura.imagePath);
    }

    private void caricaImmagine(String pathImmagine, ImageView imgv) {
        Picasso.get().load(pathImmagine).into(imgv);
    }

    @Override
    public int getItemCount() {
        Log.d("COUNTZ", "COUNT");
        return listaLetture.size();
    }


    /* sottoclasse */
    class LettureViewHolder extends RecyclerView.ViewHolder {

        TextView codiceUser,codiceUtenteBolletta,nomeUtente,cognomeUtente,valoreLettura,data;
        ImageView imagePath;


        public LettureViewHolder(@NonNull View itemView) {
            super(itemView);

            codiceUser = itemView.findViewById(R.id.codiceUser);
            codiceUtenteBolletta = itemView.findViewById(R.id.codiceUtenteBolleta);
            nomeUtente = itemView.findViewById(R.id.nomeUtente);
            cognomeUtente = itemView.findViewById(R.id.cognomeUtente);
            valoreLettura = itemView.findViewById(R.id.valoreLettura);
            data = itemView.findViewById(R.id.data);
            imagePath = itemView.findViewById(R.id.imageView);

        }
    }

}

