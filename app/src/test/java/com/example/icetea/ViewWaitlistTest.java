package com.example.icetea;

import static org.junit.Assert.*;

import org.junit.Test;

import com.example.icetea.models.Waitlist;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * US 02.02.01
 * As an organizer, I want to view the list of entrants
 * who joined my event waiting list.
 *
 */
public class ViewWaitlistTest {

    @Test
    public void testViewEntrantsForSpecificEvent() {
        String targetEventId = "event123";

        // Simulated full waitlist
        List<Waitlist> allEntries = new ArrayList<>();

        // Entrant in waiting state for event123
        Waitlist e1 = new Waitlist();
        e1.setUserId("userA");
        e1.setEventId("event123");
        e1.setStatus(Waitlist.STATUS_WAITING);
        allEntries.add(e1);

        // Selected entrant for event123
        Waitlist e2 = new Waitlist();
        e2.setUserId("userB");
        e2.setEventId("event123");
        e2.setStatus(Waitlist.STATUS_SELECTED);
        allEntries.add(e2);

        // Entrant for a completely different event
        Waitlist e3 = new Waitlist();
        e3.setUserId("userC");
        e3.setEventId("otherEvent");
        e3.setStatus(Waitlist.STATUS_WAITING);
        allEntries.add(e3);

        // entries belonging to targetEventId
        List<Waitlist> entrantsForEvent = new ArrayList<>();
        for (Waitlist entry : allEntries) {
            if (targetEventId.equals(entry.getEventId())) {
                entrantsForEvent.add(entry);
            }
        }

        // Expect exactly two entries from event123
        assertEquals("There should be exactly 2 entrants for event123", 2, entrantsForEvent.size());
        assertEquals("userA", entrantsForEvent.get(0).getUserId());
        assertEquals("userB", entrantsForEvent.get(1).getUserId());

        // Ensure all returned entries belong to the correct event
        for (Waitlist entry : entrantsForEvent) {
            assertEquals(targetEventId, entry.getEventId());
        }
    }
}
