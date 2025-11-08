package com.example.icetea.auth;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.rpc.context.AttributeContext;
/**
 * FBAuthenticator is a utility class for handling Firebase authentication.
 * Provides methods for login, sign-up, updating user email, deleting the user,
 * logging out, and retrieving information about the currently logged-in user.
 */
public class FBAuthenticator {
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    /**
     * Logs in a user with the given email and password.
     *
     * @param email The user's email.
     * @param password The user's password.
     * @param listener Callback listener for success or failure.
     */
    public static void loginUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }
    /**
     * Signs up a new user with the given email and password.
     *
     * @param email The user's email.
     * @param password The user's password.
     * @param listener Callback listener for success or failure.
     */
    public static void signUpUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }
    /**
     * Updates the currently logged-in user's email. Sends a verification email.
     *
     * @param email The new email address.
     * @param listener Callback listener for success or failure.
     */
    public static void updateUser(String email, OnCompleteListener<Void> listener) {
        //firebase recommends verifyBeforeUpdateEmail - sends a verification email
        getCurrentUser().verifyBeforeUpdateEmail(email).addOnCompleteListener(listener);
    }
    /**
     * Deletes the currently logged-in user.
     *
     * @param listener Callback listener for success or failure.
     */
    public static void deleteUser(OnCompleteListener<Void> listener) {
        getCurrentUser().delete().addOnCompleteListener(listener);
    }
    /**
     * Logs out the currently logged-in user.
     */
    public static void logout() {
        mAuth.signOut();
    }
    /**
     * Returns the currently logged-in Firebase user.
     *
     * @return The current FirebaseUser, or null if not logged in.
     */
    public static FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }
    /**
     * Returns the user ID of the currently logged-in user.
     *
     * @return The UID of the current FirebaseUser.
     */
    public static String getCurrentUserId() {return getCurrentUser().getUid();}
    /**
     * Checks whether a user is currently logged in.
     *
     * @return true if a user is logged in, false otherwise.
     */
    public static boolean isLoggedIn() {
        return FBAuthenticator.getCurrentUser() != null;
    }
}