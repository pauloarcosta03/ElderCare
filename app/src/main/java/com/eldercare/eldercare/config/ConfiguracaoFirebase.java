package com.eldercare.eldercare.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFirebase {

    private static FirebaseAuth autenticacao;
    private static DatabaseReference firebaseRef;

    //Dar a referencia ao login do firebase à variável autenticacao
    public static FirebaseAuth getFirebaseAutenticacao(){

        if (autenticacao == null){
            autenticacao = FirebaseAuth.getInstance();
        }

        return autenticacao;
    }

    public static DatabaseReference getFirebaseRef(){

        if (firebaseRef == null){
            firebaseRef = FirebaseDatabase.getInstance().getReference();
        }

        return firebaseRef;
    }

}
