package com.example.icetea.profile;

import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;

import com.example.icetea.models.UserDB;
import com.example.icetea.util.Callback;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Controller responsible for managing user profile operations.
 *
 * <p>Handles validation, updating, and deletion of user profile data
 * from Firestore and optionally Firebase Authentication.</p>
 */
public class ProfileController {

    /**
     * Updates a user's profile data in Firestore.
     *
     * @param fid      The Firebase ID of the user to update.
     * @param updates  A map of field-value pairs to update.
     * @param callback A callback invoked with success or failure of the operation.
     */
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

    /**
     * Validates a user's name.
     *
     * @param name The name to validate.
     * @return A validation error message if invalid, or null if valid.
     */
    public String validateName(String name) {
        if (name.isEmpty()) {
            return "Name cannot be empty";
        }
        return null;
    }

    /**
     * Validates a user's email.
     *
     * @param email The email to validate.
     * @return A validation error message if invalid, or null if valid.
     */
    public String validateEmail(String email) {
        if (email.isEmpty()) {
            return "Email cannot be empty";
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Email is invalid";
        }
        return null;
    }

    /**
     * Validates a user's phone number.
     *
     * <p>Empty strings are considered valid (optional field).</p>
     *
     * @param phone The phone number to validate.
     * @return A validation error message if invalid, or null if valid.
     */
    public String validatePhone(String phone) {
        if (phone.isEmpty()) {
            return null;
        }
        if (!Patterns.PHONE.matcher(phone).matches()) {
            return "Phone number is invalid";
        }
        return null;
    }

    /**
     * Deletes a user's profile along with their associated events and waitlist entries.
     *
     * <p>Performs batched deletion across the following collections:
     * <ul>
     *     <li>events (organized by the user)</li>
     *     <li>waitlist entries (owned by the user)</li>
     *     <li>user document itself</li>
     * </ul>
     * </p>
     *
     * @param userId   The ID of the user to delete.
     * @param callback A callback invoked upon success or failure of the deletion.
     */
    public void deleteProfile(String userId, Callback<Void> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference eventsCollection = db.collection("events");
        CollectionReference waitlistCollection = db.collection("waitlist");
        DocumentReference userDocRef = db.collection("users").document(userId);

        Task<QuerySnapshot> userEventsTask = eventsCollection.whereEqualTo("organizerId", userId).get();

        Task<QuerySnapshot> waitlistsForUserTask = waitlistCollection.whereEqualTo("userId", userId).get();

        Tasks.whenAllSuccess(userEventsTask, waitlistsForUserTask)
                .addOnSuccessListener(results -> {
                    QuerySnapshot eventsSnap = (QuerySnapshot) results.get(0);
                    QuerySnapshot waitlistsForUserSnap = (QuerySnapshot) results.get(1);

                    WriteBatch batch = db.batch();

                    for (DocumentSnapshot doc : eventsSnap.getDocuments()) {
                        batch.delete(doc.getReference());
                    }

                    for (DocumentSnapshot doc : waitlistsForUserSnap.getDocuments()) {
                        batch.delete(doc.getReference());
                    }

                    batch.delete(userDocRef);

                    batch.commit()
                            .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                            .addOnFailureListener(callback::onFailure);
                })
                .addOnFailureListener(callback::onFailure);
    }

}