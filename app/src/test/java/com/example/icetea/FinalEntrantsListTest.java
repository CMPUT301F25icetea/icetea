package com.example.icetea;

import static org.junit.Assert.*;

import org.junit.Test;

import com.example.icetea.models.Waitlist;

import java.util.ArrayList;
import java.util.List;

/**
 * Mocked test for final entrants
 * US 02.06.03 – As an organizer, I want to see a final list
 * of entrants who enrolled for the event.
 *
 * In this app, final entrants are the waitlist entries
 * whose status is "accepted".
 *
 * This test checks that from a mixed list of waitlist entries,
 * we can correctly pick out only the accepted ones.
 */
public class FinalEntrantsListTest {

    @Test
    public void testFinalEntrantsAreAllAccepted() {
        List<Waitlist> allEntries = new ArrayList<>();

        Waitlist waiting = new Waitlist();
        waiting.setUserId("user_waiting");
        waiting.setEventId("event123");
        waiting.setStatus(Waitlist.STATUS_WAITING);
        allEntries.add(waiting);

        Waitlist selected = new Waitlist();
        selected.setUserId("user_selected");
        selected.setEventId("event123");
        selected.setStatus(Waitlist.STATUS_SELECTED);
        allEntries.add(selected);

        Waitlist accepted1 = new Waitlist();
        accepted1.setUserId("user_accepted_1");
        accepted1.setEventId("event123");
        accepted1.setStatus(Waitlist.STATUS_ACCEPTED);
        allEntries.add(accepted1);

        Waitlist cancelled = new Waitlist();
        cancelled.setUserId("user_cancelled");
        cancelled.setEventId("event123");
        cancelled.setStatus(Waitlist.STATUS_CANCELLED);
        allEntries.add(cancelled);

        Waitlist accepted2 = new Waitlist();
        accepted2.setUserId("user_accepted_2");
        accepted2.setEventId("event123");
        accepted2.setStatus(Waitlist.STATUS_ACCEPTED);
        allEntries.add(accepted2);

        List<Waitlist> finalEntrants = new ArrayList<>();
        for (Waitlist entry : allEntries) {
            if (Waitlist.STATUS_ACCEPTED.equals(entry.getStatus())) {
                finalEntrants.add(entry);
            }
        }

        assertEquals("There should be exactly 2 accepted entrants", 2, finalEntrants.size());
        assertEquals("user_accepted_1", finalEntrants.get(0).getUserId());
        assertEquals("user_accepted_2", finalEntrants.get(1).getUserId());

        for (Waitlist entry : finalEntrants) {
            assertEquals(Waitlist.STATUS_ACCEPTED, entry.getStatus());
        }
    }
}
