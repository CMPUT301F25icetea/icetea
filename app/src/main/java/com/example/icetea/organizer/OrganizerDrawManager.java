package com.example.icetea.organizer;

import android.content.Context;
import android.widget.Toast;

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

    public static String getNotificationType(int index, int drawSize) {
        if (drawSize <= 0) throw new IllegalArgumentException("drawSize must be positive");
        return (index < drawSize) ? "won" : "lost";
    }

    public static List<String> selectWinners(List<String> waitlistUserIds, int drawSize) {
        if (waitlistUserIds == null || waitlistUserIds.isEmpty()) return new ArrayList<>();
        if (drawSize <= 0) throw new IllegalArgumentException("drawSize must be positive");

        List<String> shuffled = new ArrayList<>(waitlistUserIds);
        Collections.shuffle(shuffled);

        int actualDrawSize = Math.min(drawSize, shuffled.size());
        return new ArrayList<>(shuffled.subList(0, actualDrawSize));
    }

    public static List<String> getFinalEntrants(List<String> waitlistUserIds, int drawSize) {
        if (waitlistUserIds == null || waitlistUserIds.isEmpty()) return new ArrayList<>();
        if (drawSize <= 0) throw new IllegalArgumentException("drawSize must be positive");

        List<String> shuffled = new ArrayList<>(waitlistUserIds);
        Collections.shuffle(shuffled);

        int actualDrawSize = Math.min(drawSize, shuffled.size());
        return new ArrayList<>(shuffled.subList(0, actualDrawSize));
    }

    public void drawEntrants(Context context, String eventId, String eventName, int drawSize) {
        waitlistCollection.whereEqualTo("eventId", eventId)
                .whereEqualTo("status", "invited")
                .get()
                .addOnSuccessListener(existingWinners -> {
                    if (!existingWinners.isEmpty()) {
                        Toast.makeText(context, "You have already drawn winners for this event.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    waitlistCollection.whereEqualTo("eventId", eventId)
                            .whereEqualTo("status", "waiting")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        List<DocumentSnapshot> waitlistDocs = task.getResult().getDocuments();
                                        if (waitlistDocs.isEmpty()) {
                                            Toast.makeText(context, "No entrants on waitlist.", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        Collections.shuffle(waitlistDocs);
                                        int actualDrawSize = Math.min(drawSize, waitlistDocs.size());
                                        List<String> selectedUserIds = new ArrayList<>();
                                        List<String> loserIds = new ArrayList<>();

                                        for (int i = 0; i < waitlistDocs.size(); i++) {
                                            DocumentSnapshot doc = waitlistDocs.get(i);
                                            String userId = doc.getString("userId");
                                            String resultType = getNotificationType(i, actualDrawSize);

                                            if ("won".equals(resultType)) {
                                                selectedUserIds.add(userId);
                                                waitlistCollection.document(doc.getId())
                                                        .update("status", "invited",
                                                                "invitedAt", Timestamp.now());
                                                sendNotification(userId, eventId, eventName, "won",
                                                        "🎉 You won the draw for " + eventName + "!");
                                            } else {
                                                loserIds.add(userId);
                                                waitlistCollection.document(doc.getId())
                                                        .update("status", "loss");
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
                                                        Toast.makeText(context, "Draw completed successfully!", Toast.LENGTH_SHORT).show())
                                                .addOnFailureListener(e ->
                                                        Toast.makeText(context, "Error logging draw.", Toast.LENGTH_SHORT).show());
                                    } else {
                                        Toast.makeText(context, "Error retrieving waitlist.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Error checking existing winners.", Toast.LENGTH_SHORT).show());
    }

    protected void sendNotification(String userId, String eventId, String eventName,
                                    String type, String message) {
        OrganizerNotificationManager notificationManager = new OrganizerNotificationManager();
        notificationManager.sendNotification(userId, eventId, eventName, type, message);
    }
}

