package com.example.icetea;

import static org.junit.Assert.*;

import org.junit.Test;

import com.example.icetea.models.Waitlist;

import java.util.ArrayList;
import java.util.List;

/**
 * US 02.06.03 – As an organizer, I want to see a final list
 * of entrants who enrolled for the event.
 *
 * This test ensures that from a mixed set of waitlist entries,
 * we correctly filter out only those who have accepted.
 */
public class FinalEntrantsListTest {

    @Test
    public void testFinalEntrantsAreAllAccepted() {
        // A simulated mixed waitlist for an event
        List<Waitlist> allEntries = new ArrayList<>();

        // (WAITING)
        Waitlist waiting = new Waitlist();
        waiting.setUserId("user_waiting");
        waiting.setEventId("event123");
        waiting.setStatus(Waitlist.STATUS_WAITING);
        allEntries.add(waiting);

        // (SELECTED)
        Waitlist selected = new Waitlist();
        selected.setUserId("user_selected");
        selected.setEventId("event123");
        selected.setStatus(Waitlist.STATUS_SELECTED);
        allEntries.add(selected);

        // Accepted
        Waitlist accepted1 = new Waitlist();
        accepted1.setUserId("user_accepted_1");
        accepted1.setEventId("event123");
        accepted1.setStatus(Waitlist.STATUS_ACCEPTED);
        allEntries.add(accepted1);

        // Cancelled
        Waitlist cancelled = new Waitlist();
        cancelled.setUserId("user_cancelled");
        cancelled.setEventId("event123");
        cancelled.setStatus(Waitlist.STATUS_CANCELLED);
        allEntries.add(cancelled);

        // Accepted
        Waitlist accepted2 = new Waitlist();
        accepted2.setUserId("user_accepted_2");
        accepted2.setEventId("event123");
        accepted2.setStatus(Waitlist.STATUS_ACCEPTED);
        allEntries.add(accepted2);

        // Filter only ACCEPTED entries
        List<Waitlist> finalEntrants = new ArrayList<>();
        for (Waitlist entry : allEntries) {
            if (Waitlist.STATUS_ACCEPTED.equals(entry.getStatus())) {
                finalEntrants.add(entry);
            }
        }

        // We expect exactly 2 accepted entrants
        assertEquals("There should be exactly 2 accepted entrants", 2, finalEntrants.size());

        // Check that the correct users were selected
        assertEquals("user_accepted_1", finalEntrants.get(0).getUserId());
        assertEquals("user_accepted_2", finalEntrants.get(1).getUserId());

        // all final entrants must have ACCEPTED status
        for (Waitlist entry : finalEntrants) {
            assertEquals(Waitlist.STATUS_ACCEPTED, entry.getStatus());
        }
    }
}
