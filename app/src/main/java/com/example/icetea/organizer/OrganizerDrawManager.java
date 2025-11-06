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

    public void drawEntrants(String eventId, int drawSize) {
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

                            for (int i = 0; i < actualDrawSize; i++) {
                                DocumentSnapshot doc = waitlistDocs.get(i);
                                String userId = doc.getString("userId");
                                selectedUserIds.add(userId);

                                waitlistCollection.document(doc.getId())
                                        .update("status", "invited",
                                                "invitedAt", Timestamp.now());
                            }

                            Map<String, Object> log = new HashMap<>();
                            log.put("eventId", eventId);
                            log.put("drawnUsers", selectedUserIds);
                            log.put("drawSize", actualDrawSize);
                            log.put("drawTime", Timestamp.now());

                            drawLogCollection.add(log)
                                    .addOnSuccessListener(ref ->
                                            System.out.println("Draw logged successfully."))
                                    .addOnFailureListener(e ->
                                            System.err.println("Error logging draw: " + e.getMessage()));

                            notifySelectedEntrants(selectedUserIds, eventId);

                        } else {
                            System.err.println("Error getting waitlist: " + task.getException());
                        }
                    }
                });
    }

    private void notifySelectedEntrants(List<String> userIds, String eventId) {
        // TODO: Connect to app's notification logic
        for (String userId : userIds) {
            System.out.println("Notify user " + userId + " for event " + eventId);
        }
    }
}

