package com.example.icetea.home;

import com.example.icetea.models.*;
import com.example.icetea.util.Callback;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.*;

import java.util.Collections;
import java.util.List;

/**
 * Controller class responsible for handling all event-related logic for ManageEventFragment.
 * Includes fetching events, drawing winners, replacing winners, revoking winners,
 * and sending notifications.
 */
public class ManageEventController {

    /**
     * Retrieves an {@link Event} object from the database given an event ID.
     *
     * @param eventId  ID of the event to fetch
     * @param callback Callback invoked with the fetched Event or an Exception on failure
     */
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

    /**
     * Draws winners from the waiting list for a given event and notifies users accordingly.
     *
     * @param event    The event for which winners are being drawn
     * @param count    Number of winners to draw
     * @param callback Callback invoked on success or failure
     */
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
                        // Send notifications to winners and non-winners
                        for (DocumentSnapshot doc : selectedList) {
                            String userId = doc.getString("userId");
                            sendNotificationIfEnabled(
                                    userId,
                                    "You're a winner!",
                                    "You have been selected for the event: " + event.getName(),
                                    event.getEventId()
                            );
                        }

                        for (DocumentSnapshot doc : waitingList) {
                            if (!selectedList.contains(doc)) {
                                String userId = doc.getString("userId");
                                sendNotificationIfEnabled(
                                        userId,
                                        "Event Results",
                                        "You were not selected for the event: " + event.getName() + ". However you can still be selected if someone else declines their offer.",
                                        event.getEventId()
                                );
                            }
                        }

                        callback.onSuccess(null);
                    })
                    .addOnFailureListener(callback::onFailure);
        });
    }

    /**
     * Replaces a winner with a new entrant from the waiting list.
     *
     * @param userId   ID of the current winner to be replaced
     * @param eventId  ID of the event
     * @param callback Callback invoked on success or failure
     */
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
                        String eventName = "the event";
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

    /**
     * Revokes a winner's spot for an event and notifies the user.
     *
     * @param userId   ID of the winner to revoke
     * @param eventId  ID of the event
     * @param callback Callback invoked on success or failure
     */
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

    /**
     * Sends a notification to a user if their notifications are enabled.
     *
     * @param userId  ID of the user
     * @param title   Notification title
     * @param message Notification message
     * @param eventId ID of the event related to the notification
     */
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

    /**
     * Sends a notification to a user without checking settings.
     *
     * @param userId  ID of the user
     * @param title   Notification title
     * @param message Notification message
     * @param eventId ID of the related event
     */
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

    /**
     * Adds a log entry for notifications sent regarding an event.
     *
     * @param eventId    ID of the event
     * @param title      Notification title
     * @param message    Notification message
     * @param recipients List of user IDs who received the notification
     * @param statuses   List of status strings corresponding to recipients
     */
    public void addNotificationLogForEvent(String eventId,
                                           String title,
                                           String message,
                                           List<String> recipients,
                                           List<String> statuses) {
        NotificationLog log = new NotificationLog();
        log.setEventId(eventId);
        log.setTitle(title);
        log.setMessage(message);
        log.setRecipients(recipients);
        log.setStatuses(statuses);
        log.setTimestamp(Timestamp.now());

        FirebaseFirestore.getInstance()
                .collection("notificationsLog")
                .add(log);
    }
}