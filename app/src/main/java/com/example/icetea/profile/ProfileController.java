package com.example.icetea.profile;

import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;

import com.example.icetea.models.UserDB;
import com.example.icetea.util.Callback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;

/**
 * Controller responsible for managing the user's profile data.
 *
 * Handles loading, updating, and deleting user information from both
 * Firebase Authentication and Firestore database.
 */
public class ProfileController {

    public void updateProfile(String fid, HashMap<String, Object> updates, Callback<Void> callback) {

        UserDB.getInstance().updateUser(fid, updates, task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(null);
            } else {
                Exception e = task.getException() != null
                        ? task.getException()
                        : new Exception("Failed to update profile");
                callback.onFailure(e);
            }
        });
    }

    public String validateName(String name) {
        if (name.isEmpty()) {
            return "Name cannot be empty";
        }
        return null;
    }

    public String validateEmail(String email) {
        if (email.isEmpty()) {
            return "Email cannot be empty";
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Email is invalid";
        }
        return null;
    }

    public String validatePhone(String phone) {
        if (phone.isEmpty()) {
            return null;
        }
        if (!Patterns.PHONE.matcher(phone).matches()) {
            return "Phone number is invalid";
        }
        return null;
    }

    public void deleteProfile(String userId, Callback<Void> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Step 1: Delete all events owned by the user
        db.collection("events")
                .whereEqualTo("organizerId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    WriteBatch batch = db.batch();

                    // Delete each event owned by this user
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        batch.delete(doc.getReference());
                    }

                    batch.commit().addOnSuccessListener(aVoid -> {
                        // Step 2: Delete all waiting list entries for this user
                        deleteWaitingListEntries(db, userId, callback);
                    }).addOnFailureListener(e -> {
                        Log.e("ProfileController", "Failed to delete events", e);
                        callback.onFailure(e);
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileController", "Failed to query events", e);
                    callback.onFailure(e);
                });
    }

    private void deleteWaitingListEntries(FirebaseFirestore db, String userId, Callback<Void> callback) {
        // Delete all waitlist documents where this user is a participant
        db.collection("waitlist")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    WriteBatch batch = db.batch();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        batch.delete(doc.getReference());
                    }

                    batch.commit().addOnSuccessListener(aVoid -> {
                        // Step 3: Delete the user profile
                        deleteUserProfile(db, userId, callback);
                    }).addOnFailureListener(e -> {
                        Log.e("ProfileController", "Failed to delete waiting list entries", e);
                        callback.onFailure(e);
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileController", "Failed to query waiting lists", e);
                    callback.onFailure(e);
                });
    }

    private void deleteUserProfile(FirebaseFirestore db, String userId, Callback<Void> callback) {
        // Delete the user document
        db.collection("users").document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("ProfileController", "User profile deleted successfully");

                    // Delete from Firebase Authentication
                    deleteFirebaseAuthUser(callback);
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileController", "Failed to delete user profile", e);
                    callback.onFailure(e);
                });
    }

    private void deleteFirebaseAuthUser(Callback<Void> callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUser.delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("ProfileController", "Firebase Auth user deleted successfully");
                        callback.onSuccess(null);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ProfileController", "Failed to delete Firebase Auth user", e);
                        // Still consider it a success if Firestore data was deleted
                        // The auth account can be cleaned up later
                        callback.onSuccess(null);
                    });
        } else {
            callback.onSuccess(null);
        }
    }

}
