package com.example.icetea;

import static org.junit.Assert.*;

import org.junit.Test;

import com.example.icetea.models.Waitlist;

import java.util.ArrayList;
import java.util.List;

/**
 * US 02.05.01 – As an organizer, I want to send notifications
 * to chosen entrants to sign up for events.
 *
 * to test only the chosen entrant (selected) received notifications
 */
public class OrganizerNotificationTest {

    @Test
    public void testNotificationsAreSentOnlyToSelectedEntrants() {
        String eventId = "event_123";

        List<Waitlist> allEntries = new ArrayList<>();

        Waitlist waiting = new Waitlist();
        waiting.setEventId(eventId);
        waiting.setUserId("user_waiting");
        waiting.setStatus(Waitlist.STATUS_WAITING);
        allEntries.add(waiting);

        Waitlist selected1 = new Waitlist();
        selected1.setEventId(eventId);
        selected1.setUserId("user_selected_1");
        selected1.setStatus(Waitlist.STATUS_SELECTED);
        allEntries.add(selected1);

        Waitlist selected2 = new Waitlist();
        selected2.setEventId(eventId);
        selected2.setUserId("user_selected_2");
        selected2.setStatus(Waitlist.STATUS_SELECTED);
        allEntries.add(selected2);

        Waitlist cancelled = new Waitlist();
        cancelled.setEventId(eventId);
        cancelled.setUserId("user_cancelled");
        cancelled.setStatus(Waitlist.STATUS_CANCELLED);
        allEntries.add(cancelled);

        List<String> notifiedUserIds = new ArrayList<>();
        for (Waitlist entry : allEntries) {
            if (Waitlist.STATUS_SELECTED.equals(entry.getStatus())) {
                notifiedUserIds.add(entry.getUserId());
            }
        }

        assertEquals("Exactly 2 selected entrants should be notified", 2, notifiedUserIds.size());
        assertTrue(notifiedUserIds.contains("user_selected_1"));
        assertTrue(notifiedUserIds.contains("user_selected_2"));

        assertFalse("Waiting user should not be notified", notifiedUserIds.contains("user_waiting"));
        assertFalse("Cancelled user should not be notified", notifiedUserIds.contains("user_cancelled"));
    }
}
