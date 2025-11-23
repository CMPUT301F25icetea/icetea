package com.example.icetea.auth;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.installations.FirebaseInstallations;

public class FBInstallations {

    private static String fid;
    public static void getCurrentFID(OnCompleteListener<String> listener) {
        FirebaseInstallations.getInstance().getId().addOnCompleteListener(listener);
    }

    public static String getFid() {
        return fid;
    }

    public static void setFid(String fid) {
        FBInstallations.fid = fid;
    }
}
