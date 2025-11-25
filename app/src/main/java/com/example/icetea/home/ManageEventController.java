package com.example.icetea.home;

import com.example.icetea.models.Event;
import com.example.icetea.models.EventDB;
import com.example.icetea.models.Waitlist;
import com.example.icetea.models.WaitlistDB;
import com.example.icetea.util.Callback;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.Collections;
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

    //todo: send out notifications
    public void drawWinners(String eventId, int count, Callback<Void> callback) {
        if (count <= 0) {
            callback.onFailure(new IllegalArgumentException("Count must be greater than 0"));
            return;
        }

        WaitlistDB waitlistDB = WaitlistDB.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

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


}
