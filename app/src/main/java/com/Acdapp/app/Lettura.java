package com.Acdapp.app;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;

/* DESCRIZIONE:
   Oggetto lettura che rappresenta il singolo record della tabella letture sul db.
   Al momento della lettura dal db delle singole letture dell'utente viene craato un
   Arraylist di oggetti di questa classe.
   */
public class Lettura implements Serializable {
    public String codiceUtenteBolletta,codiceUser,nomeUtente,cognomeUtente,data,imagePath;
    public String valoreLettura;

    public Lettura(DocumentSnapshot document) {
        codiceUtenteBolletta = document.getString("codiceUtenteBolletta");
        codiceUser = document.getString("codiceUser");
        nomeUtente = document.getString("nomeUtente");
        cognomeUtente = document.getString("cognomeUtente");
        data = document.getString("data");
        imagePath = document.getString("imagePath");
        valoreLettura = document.getString("valoreLettura");
    }

    @Override
    public String toString() {
        return "Lettura{" +
                "codiceUtenteBolletta='" + codiceUtenteBolletta + '\'' +
                ", codiceUser='" + codiceUser + '\'' +
                ", nomeUtente='" + nomeUtente + '\'' +
                ", cognomeUtente='" + cognomeUtente + '\'' +
                ", data='" + data + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", valoreLettura='" + valoreLettura + '\'' +
                '}';
    }
}
