package com.example.icetea;

import com.example.icetea.models.Event;
import com.example.icetea.models.Waitlist;
import com.example.icetea.history.HistoryEventItem;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test cases for:
 *
 * US 01.02.03 – As an entrant, I want to have a history of events I have
 * registered for, whether I was selected or not.
 *
 */
public class EventHistoryTests {

    @Test
    public void testHistoryEventItemCreation() {

        // Mock event user registered for
        Event event = new Event();
        event.setEventId("E123");
        event.setName("Music Festival");

        // Mock waitlist record representing user's participation
        Waitlist waitlist = new Waitlist();
        waitlist.setUserId("U1");
        waitlist.setEventId("E123");
        waitlist.setStatus("selected");

        // Create a history item (how the app displays event history)
        HistoryEventItem historyItem = new HistoryEventItem(event, waitlist.getStatus());

        // Validate event info is correctly stored
        assertEquals("E123", historyItem.getEvent().getEventId());
        assertEquals("Music Festival", historyItem.getEvent().getName());

        // Validate user's participation status is stored
        assertEquals("selected", historyItem.getWaitlistStatus());
    }

    @Test
    public void testHistoryItemWithDifferentStatuses() {

        Event event = new Event();
        event.setEventId("E999");
        event.setName("Cooking Workshop");

        Waitlist waitlist = new Waitlist();
        waitlist.setUserId("U2");
        waitlist.setEventId("E999");

        // Check all possible status mappings
        String[] statuses = {"waiting", "selected", "declined", "accepted", "cancelled"};

        for (String status : statuses) {
            waitlist.setStatus(status);
            HistoryEventItem item = new HistoryEventItem(event, status);

            assertEquals(status, item.getWaitlistStatus());
        }
    }
}
