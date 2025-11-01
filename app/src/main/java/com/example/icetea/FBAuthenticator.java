package com.example.icetea;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FBAuthenticator {
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static void loginUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }

    public static void signUpUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }

    public static void logout() {
        mAuth.signOut();
    }
    public static FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }
    public static boolean isLoggedIn() {
        return FBAuthenticator.getCurrentUser() != null;
    }
}
