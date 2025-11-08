package com.example.icetea.organizer;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class OrganizerNotificationManager {
    private final CollectionReference notifCollection;

    public OrganizerNotificationManager() {
            notifCollection = FirebaseFirestore.getInstance().collection("Notification");
    }
    //TODO: ADD CALLBACK
    public void sendNotification(String userId, String eventId, String eventName,
                                 String type, String message) {
        Map<String, Object> notifData = new HashMap<>();
        notifData.put("userId", userId);
        notifData.put("eventId", eventId);
        notifData.put("eventName", eventName);
        notifData.put("type", type);
        notifData.put("message", message);
        notifData.put("timestamp", Timestamp.now());

        notifCollection.add(notifData)
                .addOnSuccessListener(ref ->
                        System.out.println("Notification sent to " + userId))
                .addOnFailureListener(e ->
                        System.err.println("Failed to send notification: " + e.getMessage()));
    }
}
