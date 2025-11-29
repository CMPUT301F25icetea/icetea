package com.example.icetea.home;

import com.example.icetea.models.Event;
import com.example.icetea.models.EventDB;
import com.example.icetea.models.Notification;
import com.example.icetea.models.NotificationDB;
import com.example.icetea.models.UserDB;
import com.example.icetea.models.Waitlist;
import com.example.icetea.models.WaitlistDB;
import com.example.icetea.util.Callback;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ManageEventController {

    public void getEventObject(String eventId, Callback<Event> callback) {
        EventDB.getInstance().getEvent(eventId, task -> {
            if (!task.isSuccessful() || task.getResult() == null || !task.getResult().exists()) {
                callback.onFailure(task.getException() != null ? task.getException() : new Exception("Error loading event"));
                return;
            }
            Event event = task.getResult().toObject(Event.class);
            if (event == null) {
                callback.onFailure(new Exception("Event is null"));
                return;
            }
            callback.onSuccess(event);
        });
    }

    public void drawWinners(Event event, int count, Callback<Void> callback) {
        if (count <= 0) {
            callback.onFailure(new IllegalArgumentException("Count must be greater than 0"));
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WaitlistDB waitlistDB = WaitlistDB.getInstance();

        waitlistDB.getEntrantsByStatus(event.getEventId(), Waitlist.STATUS_WAITING, task -> {
            if (!task.isSuccessful()) {
                callback.onFailure(task.getException() != null ? task.getException() :
                        new Exception("Failed to fetch waiting entrants"));
                return;
            }

            QuerySnapshot querySnapshot = task.getResult();
            if (querySnapshot == null || querySnapshot.isEmpty()) {
                callback.onFailure(new Exception("No waiting entrants found"));
                return;
            }

            List<DocumentSnapshot> waitingList = querySnapshot.getDocuments();

            if (waitingList.size() < count) {
                callback.onFailure(new Exception("Not enough entrants to draw " + count + " winners"));
                return;
            }

            Collections.shuffle(waitingList);
            List<DocumentSnapshot> selectedList = waitingList.subList(0, Math.min(count, waitingList.size()));

            WriteBatch batch = db.batch();

            for (DocumentSnapshot doc : selectedList) {
                batch.update(doc.getReference(), "status", Waitlist.STATUS_SELECTED);
            }

            DocumentReference eventRef = db.collection("events").document(event.getEventId());
            batch.update(eventRef, "alreadyDrew", true);


            batch.commit()
                    .addOnSuccessListener(aVoid -> {
                        for (DocumentSnapshot doc : selectedList) {
                            String userId = doc.getString("userId");
                            sendNotificationIfEnabled(
                                    userId,
                                    "You're a winner!",
                                    "You have been selected for the event: " + event.getName(),
                                    event.getEventId()
                            );
                        }
                        callback.onSuccess(null);
                    })
                    .addOnFailureListener(callback::onFailure);
        });
    }
    public void replaceWinner(String userId, String eventId, Callback<Void> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WaitlistDB waitlistDB = WaitlistDB.getInstance();

        waitlistDB.getWaitlistEntry(userId, eventId, task -> {
            if (!task.isSuccessful() || task.getResult() == null || !task.getResult().exists()) {
                callback.onFailure(new Exception("Current winner entry not found"));
                return;
            }

            DocumentSnapshot currentEntry = task.getResult();
            String currentStatus = currentEntry.getString("status");

            waitlistDB.getEntrantsByStatus(eventId, Waitlist.STATUS_WAITING, waitingTask -> {
                if (!waitingTask.isSuccessful()) {
                    callback.onFailure(waitingTask.getException() != null ? waitingTask.getException() : new Exception("Unknown error finding replacement"));
                    return;
                }

                QuerySnapshot waitingSnap = waitingTask.getResult();
                if (waitingSnap == null || waitingSnap.isEmpty()) {
                    callback.onFailure(new Exception("No waiting entrants available"));
                    return;
                }

                List<DocumentSnapshot> waitingList = waitingSnap.getDocuments();
                Collections.shuffle(waitingList);
                DocumentSnapshot replacementEntry = waitingList.get(0);

                WriteBatch batch = db.batch();

                if (Waitlist.STATUS_SELECTED.equals(currentStatus)) {
                    batch.update(currentEntry.getReference(),
                            "status", Waitlist.STATUS_CANCELLED,
                            "replaced", true
                    );
                } else if (Waitlist.STATUS_DECLINED.equals(currentStatus)) {
                    batch.update(currentEntry.getReference(),
                            "replaced", true
                    );
                } else {
                    callback.onFailure(new Exception("Cannot replace a user with status: " + currentStatus));
                    return;
                }

                batch.update(replacementEntry.getReference(),
                        "status", Waitlist.STATUS_SELECTED
                );

                batch.commit().addOnSuccessListener(aVoid -> {
                    EventDB.getInstance().getEvent(eventId, eventTask -> {
                        String eventName = "the event"; // fallback
                        if (eventTask.isSuccessful() && eventTask.getResult() != null && eventTask.getResult().exists()) {
                            DocumentSnapshot doc = eventTask.getResult();
                            String name = doc.getString("name");
                            if (name != null) eventName = name;
                        }

                        sendNotificationIfEnabled(
                                userId,
                                "You were replaced",
                                "Your spot in " + eventName + " has been cancelled and a new winner was selected.",
                                eventId
                        );

                        String newWinnerId = replacementEntry.getString("userId");
                        sendNotificationIfEnabled(
                                newWinnerId,
                                "You're a winner!",
                                "You have been selected for " + eventName + ".",
                                eventId
                        );

                        callback.onSuccess(null);
                    });
                }).addOnFailureListener(callback::onFailure);
            });
        });
    }

    public void revokeWinner(String userId, String eventId, Callback<Void> callback) {
        WaitlistDB waitlistDB = WaitlistDB.getInstance();

        waitlistDB.getWaitlistEntry(userId, eventId, task -> {
            if (!task.isSuccessful() || task.getResult() == null || !task.getResult().exists()) {
                callback.onFailure(new Exception("Winner entry not found"));
                return;
            }

            DocumentSnapshot entry = task.getResult();
            String currentStatus = entry.getString("status");

            if (!Waitlist.STATUS_SELECTED.equals(currentStatus)) {
                callback.onFailure(new Exception("Cannot revoke user with status: " + currentStatus));
                return;
            }

            waitlistDB.updateWaitlistStatus(userId, eventId, Waitlist.STATUS_CANCELLED, statusTask -> {
                if (statusTask.isSuccessful()) {
                    EventDB.getInstance().getEvent(eventId, eventTask -> {
                        String eventName = "the event";
                        if (eventTask.isSuccessful() && eventTask.getResult() != null && eventTask.getResult().exists()) {
                            DocumentSnapshot doc = eventTask.getResult();
                            String name = doc.getString("name");
                            if (name != null) eventName = name;
                        }

                        sendNotificationIfEnabled(
                                userId,
                                "Your spot was revoked",
                                "Your winning spot in " + eventName + " has been cancelled.",
                                eventId
                        );

                        callback.onSuccess(null);
                    });
                } else {
                    callback.onFailure(statusTask.getException() != null ? statusTask.getException() : new Exception("Failed to update status"));
                }
            });
        });
    }

    public void sendNotificationIfEnabled(String userId, String title, String message, String eventId) {
        UserDB userDB = UserDB.getInstance();
        userDB.getUser(userId, task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                Boolean notificationsEnabled = task.getResult().getBoolean("notifications");
                if (notificationsEnabled != null && notificationsEnabled) {
                    sendNotification(userId, title, message, eventId);
                } else {
                    System.out.println("User " + userId + " has notifications disabled.");
                }
            } else {
                System.err.println("Failed to fetch user " + userId + ": " + task.getException());
            }
        });
    }

    private void sendNotification(String userId, String title, String message, String eventId) {
        NotificationDB notificationDB = NotificationDB.getInstance();
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setEventId(eventId);
        notification.setTimestamp(Timestamp.now());

        notificationDB.addNotification(notification, task -> {
            if (!task.isSuccessful()) {
                System.err.println("Failed to send notification: " + task.getException());
            }
        });
    }

    public void updateEventPoster(String eventId, String posterBase64, Callback<Void> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        HashMap<String, Object> updates = new HashMap<>();
        updates.put("posterBase64", posterBase64);

        db.collection("events")
                .document(eventId)
                .update(updates)
                .addOnSuccessListener(unused -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }
}
