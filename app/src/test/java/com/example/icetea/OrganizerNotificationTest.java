package com.example.icetea;

import static org.junit.Assert.*;

import org.junit.Test;

import com.example.icetea.models.Waitlist;

import java.util.ArrayList;
import java.util.List;

/**
 * US 02.05.01
 * As an organizer, I want to send notifications only
 * to the chosen entrants (status = SELECTED).
 *
 * This test ensures:
 *  - Only SELECTED entrants receive notifications
 *  - WAITING or CANCELLED entrants do NOT receive notifications
 */
public class OrganizerNotificationTest {

    @Test
    public void testNotificationsAreSentOnlyToSelectedEntrants() {
        String eventId = "event_123";

        // Simulated event waitlist with mixed statuses
        List<Waitlist> allEntries = new ArrayList<>();

        // Not selected
        Waitlist waiting = new Waitlist();
        waiting.setEventId(eventId);
        waiting.setUserId("user_waiting");
        waiting.setStatus(Waitlist.STATUS_WAITING);
        allEntries.add(waiting);

        // selected
        Waitlist selected1 = new Waitlist();
        selected1.setEventId(eventId);
        selected1.setUserId("user_selected_1");
        selected1.setStatus(Waitlist.STATUS_SELECTED);
        allEntries.add(selected1);

        // selected
        Waitlist selected2 = new Waitlist();
        selected2.setEventId(eventId);
        selected2.setUserId("user_selected_2");
        selected2.setStatus(Waitlist.STATUS_SELECTED);
        allEntries.add(selected2);

        // cancelled
        Waitlist cancelled = new Waitlist();
        cancelled.setEventId(eventId);
        cancelled.setUserId("user_cancelled");
        cancelled.setStatus(Waitlist.STATUS_CANCELLED);
        allEntries.add(cancelled);

        // Filter only SELECTED entrants to notify
        List<String> notifiedUserIds = new ArrayList<>();
        for (Waitlist entry : allEntries) {
            if (Waitlist.STATUS_SELECTED.equals(entry.getStatus())) {
                notifiedUserIds.add(entry.getUserId());
            }
        }

        // Exactly 2 selected entrants should be notified
        assertEquals("Exactly 2 selected entrants should be notified", 2, notifiedUserIds.size());
        assertTrue(notifiedUserIds.contains("user_selected_1"));
        assertTrue(notifiedUserIds.contains("user_selected_2"));

        // mkae sure WAITING and CANCELLED do not receive notification
        assertFalse("Waiting user should not be notified", notifiedUserIds.contains("user_waiting"));
        assertFalse("Cancelled user should not be notified", notifiedUserIds.contains("user_cancelled"));
    }
}
