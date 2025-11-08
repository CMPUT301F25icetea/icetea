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

/**
 * Controller for fetching and managing the waiting list for events.
 * Handles fetching user emails and formatting join times for display.
 */
public class WaitingListController {

    /**
     * Retrieves the waiting list for a given event.
     * Each entry includes user ID, email, status, and formatted join time.
     *
     * @param eventId  ID of the event whose waiting list is being fetched
     * @param callback Callback to return the list or an error
     */
    public void getWaitingList(String eventId, Callback<List<WaitingListEntry>> callback) {
        WaitlistDB.getInstance().getWaitlistForEvent(eventId, task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                // Return failure if Firestore task fails or result is null
                callback.onFailure(task.getException() != null ? task.getException() : new Exception("Failed to get waitlist"));
                return;
            }

            List<WaitingListEntry> waitingList = new ArrayList<>();
            AtomicInteger processedCount = new AtomicInteger(0);

            if (task.getResult().isEmpty()) {
                // No entries, return empty list
                callback.onSuccess(new ArrayList<WaitingListEntry>());
                return;
            }

            for (QueryDocumentSnapshot doc : task.getResult()) {
                final String userId = doc.getString("userId");
                if (userId == null) {
                    processedCount.incrementAndGet();
                    continue;
                }

                // Fetch user email for each waiting list entry
                UserDB.getInstance().getUserEmail(userId, new Callback<String>() {
                    @Override
                    public void onSuccess(String email) {
                        WaitingListEntry entry = new WaitingListEntry();
                        entry.setUserId(userId);
                        entry.setEventId(eventId);
                        entry.setEmail(email != null ? email : "error fetching email");
                        entry.setStatus(doc.getString("status"));
                        entry.setSelected(false);

                        // Format join time
                        Object joinedAt = doc.get("joinedAt");
                        String joinTimeStr = "error";
                        if (joinedAt instanceof Long) {
                            joinTimeStr = formatDate((Long) joinedAt);
                        } else if (joinedAt instanceof Timestamp) {
                            joinTimeStr = formatDate(((Timestamp) joinedAt).toDate().getTime());
                        }
                        entry.setJoinTime(joinTimeStr);

                        waitingList.add(entry);

                        // If all entries processed, return the list
                        if (processedCount.incrementAndGet() == task.getResult().size()) {
                            callback.onSuccess(waitingList);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Even on failure, count as processed
                        if (processedCount.incrementAndGet() == task.getResult().size()) {
                            callback.onSuccess(waitingList);
                        }
                    }
                });
            }
        });
    }

    /**
     * Formats a timestamp (milliseconds since epoch) into a human-readable string.
     *
     * @param millis Milliseconds since epoch
     * @return Formatted date string in "yyyy-MM-dd HH:mm" format
     */
    private String formatDate(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date(millis));
    }
}
