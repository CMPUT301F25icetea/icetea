package com.example.icetea;

import static org.junit.Assert.*;

import org.junit.Test;

import com.example.icetea.models.Waitlist;

import java.util.ArrayList;
import java.util.List;

/**
 * Mocked test for seeing applicants that are invited
 * US 02.06.01
 * As an organizer I want to view a list of all chosen entrants
 * who are invited to apply.
 *
 * In the app, "invited" entrants are waitlist entries whose status is "selected".
 */
public class EventInvitedEntrantsTest {

    @Test
    public void testFilterInvitedEntrantsBySelectedStatus() {
        String eventId = "event_invited_test";

        List<Waitlist> allEntries = new ArrayList<>();

        Waitlist w1 = new Waitlist();
        w1.setEventId(eventId);
        w1.setUserId("user1");
        w1.setStatus(Waitlist.STATUS_SELECTED);
        allEntries.add(w1);

        Waitlist w2 = new Waitlist();
        w2.setEventId(eventId);
        w2.setUserId("user2");
        w2.setStatus(Waitlist.STATUS_SELECTED);
        allEntries.add(w2);

        Waitlist w3 = new Waitlist();
        w3.setEventId(eventId);
        w3.setUserId("user3");
        w3.setStatus(Waitlist.STATUS_WAITING);
        allEntries.add(w3);

        Waitlist w4 = new Waitlist();
        w4.setEventId(eventId);
        w4.setUserId("user4");
        w4.setStatus(Waitlist.STATUS_CANCELLED);
        allEntries.add(w4);

        List<Waitlist> invitedEntrants = new ArrayList<>();
        for (Waitlist entry : allEntries) {
            if (Waitlist.STATUS_SELECTED.equals(entry.getStatus())) {
                invitedEntrants.add(entry);
            }
        }

        assertEquals("There should be exactly 2 invited entrants", 2, invitedEntrants.size());
        assertEquals("user1", invitedEntrants.get(0).getUserId());
        assertEquals("user2", invitedEntrants.get(1).getUserId());

        for (Waitlist entry : invitedEntrants) {
            assertEquals(Waitlist.STATUS_SELECTED, entry.getStatus());
        }
    }
}
