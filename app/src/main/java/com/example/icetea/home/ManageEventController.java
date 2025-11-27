package com.example.icetea.home;

import com.example.icetea.models.Event;
import com.example.icetea.models.EventDB;
import com.example.icetea.models.Waitlist;
import com.example.icetea.models.WaitlistDB;
import com.example.icetea.util.Callback;
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

    // todo: send out notifications
    public void drawWinners(String eventId, int count, Callback<Void> callback) {
        if (count <= 0) {
            callback.onFailure(new IllegalArgumentException("Count must be greater than 0"));
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WaitlistDB waitlistDB = WaitlistDB.getInstance();

        waitlistDB.getEntrantsByStatus(eventId, Waitlist.STATUS_WAITING, task -> {
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

            DocumentReference eventRef = db.collection("events").document(eventId);
            batch.update(eventRef, "alreadyDrew", true);

            batch.commit()
                    .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                    .addOnFailureListener(callback::onFailure);
        });
    }
    //todo:send notifications too
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

                batch.commit()
                        .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                        .addOnFailureListener(callback::onFailure);
            });
        });
    }

    //todo: send notification too
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
                    callback.onSuccess(null);
                } else {
                    callback.onFailure(statusTask.getException() != null ? statusTask.getException() : new Exception("Failed to update status"));
                }
            });
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
