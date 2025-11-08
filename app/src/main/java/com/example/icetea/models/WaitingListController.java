package com.example.icetea.models;

import com.example.icetea.util.Callback;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class WaitingListController {

    public void getWaitingList(String eventId, Callback<List<WaitingListEntry>> callback) {
        WaitlistDB.getInstance().getWaitlistForEvent(eventId, task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                callback.onFailure(task.getException() != null ? task.getException() : new Exception("Failed to get waitlist"));
                return;
            }
            List<WaitingListEntry> waitingList = new ArrayList<>();
            AtomicInteger processedCount = new AtomicInteger(0);

            if (task.getResult().isEmpty()) {
                callback.onSuccess(new ArrayList<WaitingListEntry>());
                return;
            }

            for (QueryDocumentSnapshot doc : task.getResult()) {
                final String userId = doc.getString("userId");
                if (userId == null) {
                    processedCount.incrementAndGet();
                    continue;
                }
                UserDB.getInstance().getUserEmail(userId, new Callback<String>() {
                    @Override
                    public void onSuccess(String email) {
                        WaitingListEntry entry = new WaitingListEntry();
                        entry.setUserId(userId);
                        entry.setEventId(eventId);
                        entry.setEmail(email != null ? email : "error fetching email");
                        entry.setStatus(doc.getString("status"));
                        entry.setSelected(false);

                        Object joinedAt = doc.get("joinedAt");
                        String joinTimeStr = "error";
                        if (joinedAt instanceof Long) {
                            joinTimeStr = formatDate((Long) joinedAt);
                        } else if (joinedAt instanceof Timestamp) {
                            joinTimeStr = formatDate(((Timestamp) joinedAt).toDate().getTime());
                        }
                        entry.setJoinTime(joinTimeStr);

                        waitingList.add(entry);

                        if (processedCount.incrementAndGet() == task.getResult().size()) {
                            callback.onSuccess(waitingList);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        if (processedCount.incrementAndGet() == task.getResult().size()) {
                            callback.onSuccess(waitingList);
                        }
                    }
                });
            }
        });
    }
    private String formatDate(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date(millis));
    }
}
