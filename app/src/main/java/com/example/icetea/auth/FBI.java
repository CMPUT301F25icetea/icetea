package com.example.icetea.auth;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.installations.FirebaseInstallations;

public class FBI {

    public static void getCurrentFID(OnCompleteListener<String> listener) {
        FirebaseInstallations.getInstance().getId().addOnCompleteListener(listener);
    }
}
