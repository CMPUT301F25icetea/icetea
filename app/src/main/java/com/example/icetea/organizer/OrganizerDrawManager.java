package com.example.icetea.organizer;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrganizerDrawManager {

    private final CollectionReference waitlistCollection;
    private final CollectionReference drawLogCollection;

    public OrganizerDrawManager() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        waitlistCollection = db.collection("waitlist");
        drawLogCollection = db.collection("drawLogs");
    }

    public void drawEntrants(String eventId, String eventName, int drawSize) {
        waitlistCollection.whereEqualTo("eventId", eventId)
                .whereEqualTo("status", "waiting")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> waitlistDocs = task.getResult().getDocuments();

                            if (waitlistDocs.isEmpty()) {
                                System.out.println("No entrants on waitlist.");
                                return;
                            }

                            Collections.shuffle(waitlistDocs);

                            int actualDrawSize = Math.min(drawSize, waitlistDocs.size());
                            List<String> selectedUserIds = new ArrayList<>();
                            List<String> loser = new ArrayList<>();

                            for (int i = 0; i < actualDrawSize; i++) {
                                DocumentSnapshot doc = waitlistDocs.get(i);
                                String userId = doc.getString("userId");
                                String email = doc.getString("email");

                                if (i < actualDrawSize) {
                                    selectedUserIds.add(userId);
                                    waitlistCollection.document(doc.getId())
                                            .update("status", "invited",
                                                    "invitedAt", Timestamp.now());
                                    sendNotificationAndEmail(userId, email, eventId, eventName, "won",
                                            "You won the draw for" + eventName);
                                } else {
                                    loser.add(userId);
                                    waitlistCollection.document(doc.getId())
                                            .update("status", "loss");
                                    sendNotificationAndEmail(userId, email, eventId, eventName, "lost",
                                            "Thank you for participating, you were not selected");
                                }
                            }

                            Map<String, Object> log = new HashMap<>();
                            log.put("eventId", eventId);
                            log.put("eventName", eventName);
                            log.put("drawnUsers", selectedUserIds);
                            log.put("drawSize", actualDrawSize);
                            log.put("drawTime", Timestamp.now());

                            drawLogCollection.add(log)
                                    .addOnSuccessListener(ref ->
                                            System.out.println("Draw logged successfully."))
                                    .addOnFailureListener(e ->
                                            System.err.println("Error logging draw: " + e.getMessage()));



                        } else {
                            System.err.println("Error getting waitlist: " + task.getException());
                        }
                    }
                });
    }

    private void sendNotificationAndEmail(String userId, String email,
                                          String eventId, String eventName,
                                          String type, String message) {

        OrganizerNotificationManager notificationManager = new OrganizerNotificationManager();
        notificationManager.sendNotification(userId, eventId, eventName, type, message);

        //ChatGPT, prompt "How do you send email notification through firebase"
        if (email != null && !email.isEmpty()) {
            FirebaseFirestore.getInstance().collection("notifications")
                    .add(new HashMap<String, Object>() {{
                        put("userId", userId);
                        put("email", email);
                        put("subject", type.equals("won")
                                ? "You’ve won the draw for " + eventName + "!"
                                : "Draw results for " + eventName);
                        put("message", message);
                        put("type", type);
                        put("timestamp", Timestamp.now());
                    }});
        }
    }

}

