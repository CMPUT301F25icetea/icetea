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

/**
 * Handles random winner selection, Firestore updates,
 * and notifications for event draws.
 */
public class OrganizerDrawManager {

    private final CollectionReference waitlistCollection;
    private final CollectionReference drawLogCollection;

    /**
     * init Firestore collection references for managing waitlist and draw logs.
     */
    public OrganizerDrawManager() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        waitlistCollection = db.collection("waitlist");
        drawLogCollection = db.collection("drawLogs");
    }

    /**
     * Determines the notification type for a user
     *
     * @param index    the position of the user in the shuffled list
     * @param drawSize the number of winners to select
     * @return "won" if the user is in the draw size, otherwise "lost"
     * @throws IllegalArgumentException if drawSize is not positive
     */
    public static String getNotificationType(int index, int drawSize) {
        if (drawSize <= 0) throw new IllegalArgumentException("drawSize must be positive");
        return (index < drawSize) ? "won" : "lost";
    }

    /**
     * Randomly selects number of user IDs as winners from the provided waitlist.
     *
     * @param waitlistUserIds the list of user IDs on the waitlist
     * @param drawSize        the number of winners to select
     * @return a list of selected winner user IDs
     * @throws IllegalArgumentException if drawSize is not positive
     */
    public static List<String> selectWinners(List<String> waitlistUserIds, int drawSize) {
        if (waitlistUserIds == null || waitlistUserIds.isEmpty()) return new ArrayList<>();
        if (drawSize <= 0) throw new IllegalArgumentException("drawSize must be positive");

        List<String> shuffled = new ArrayList<>(waitlistUserIds);
        Collections.shuffle(shuffled);

        int actualDrawSize = Math.min(drawSize, shuffled.size());
        return new ArrayList<>(shuffled.subList(0, actualDrawSize));
    }

    /**
     * Randomly selects the final entrants (winners) from a list of waitlisted user IDs.
     *
     * @param waitlistUserIds the list of user IDs on the waitlist
     * @param drawSize        the number of winners to select
     * @return a list of final entrant user IDs
     * @throws IllegalArgumentException if drawSize is not positive
     */
    public static List<String> getFinalEntrants(List<String> waitlistUserIds, int drawSize) {
        if (waitlistUserIds == null || waitlistUserIds.isEmpty()) return new ArrayList<>();
        if (drawSize <= 0) throw new IllegalArgumentException("drawSize must be positive");

        List<String> shuffled = new ArrayList<>(waitlistUserIds);
        Collections.shuffle(shuffled);

        int actualDrawSize = Math.min(drawSize, shuffled.size());
        return new ArrayList<>(shuffled.subList(0, actualDrawSize));
    }

    /**
     * Executes the draw process for a specific event.
     *
     * @param context   the application context for displaying Toast messages
     * @param eventId   the unique Firestore ID of the event
     * @param eventName the name of the event for notification purposes
     * @param drawSize  the number of winners to select
     */
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

    /**
     * Sends a notification to a user after the draw has been completed.
     *
     * @param userId    the ID of the user to notify
     * @param eventId   the ID of the event associated with the draw
     * @param eventName the name of the event
     * @param type      the type of notification
     * @param message   the message to include in the notification
     */
    protected void sendNotification(String userId, String eventId, String eventName,
                                    String type, String message) {
        OrganizerNotificationManager notificationManager = new OrganizerNotificationManager();
        notificationManager.sendNotification(userId, eventId, eventName, type, message);
    }
}

