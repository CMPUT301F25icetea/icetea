package com.example.icetea.auth;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.rpc.context.AttributeContext;

public class FBAuthenticator {
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static void loginUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }

    public static void signUpUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }
    public static void updateUser(String email, OnCompleteListener<Void> listener) {
        //firebase recommends verifyBeforeUpdateEmail - sends a verification email
        getCurrentUser().verifyBeforeUpdateEmail(email).addOnCompleteListener(listener);
    }

    public static void deleteUser(OnCompleteListener<Void> listener) {
        getCurrentUser().delete().addOnCompleteListener(listener);
    }
    public static void logout() {
        mAuth.signOut();
    }
    public static FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public static String getCurrentUserId() {return getCurrentUser().getUid();}
    public static boolean isLoggedIn() {
        return FBAuthenticator.getCurrentUser() != null;
    }
}