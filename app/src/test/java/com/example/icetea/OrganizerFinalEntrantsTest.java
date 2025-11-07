package com.example.icetea;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.*;

import com.example.icetea.organizer.OrganizerDrawManager;

/**
 * US 02.06.03 – As an organizer, I want to see a final list
 * of entrants who enrolled for the event.
 */
public class OrganizerFinalEntrantsTest {
    @Test
    public void testFinalEntrantsCountMatchesDrawSize() {
        List<String> waitlist = Arrays.asList("U1", "U2", "U3", "U4", "U5");
        List<String> finalEntrants = OrganizerDrawManager.getFinalEntrants(waitlist, 3);
        assertEquals(3, finalEntrants.size());
    }

    @Test
    public void testFinalEntrantsSubsetOfWaitlist() {
        List<String> waitlist = Arrays.asList("U1", "U2", "U3", "U4", "U5");
        List<String> finalEntrants = OrganizerDrawManager.getFinalEntrants(waitlist, 3);
        assertTrue(waitlist.containsAll(finalEntrants));
    }

    @Test
    public void testNoDuplicateEntrantsInFinalList() {
        List<String> waitlist = Arrays.asList("U1", "U2", "U3", "U4", "U5");
        List<String> finalEntrants = OrganizerDrawManager.getFinalEntrants(waitlist, 4);
        Set<String> unique = new HashSet<>(finalEntrants);
        assertEquals(unique.size(), finalEntrants.size());
    }

    @Test
    public void testEmptyWaitlistReturnsEmptyFinalList() {
        List<String> waitlist = new ArrayList<>();
        List<String> finalEntrants = OrganizerDrawManager.getFinalEntrants(waitlist, 3);
        assertTrue(finalEntrants.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidDrawSizeThrowsException() {
        List<String> waitlist = Arrays.asList("U1", "U2", "U3");
        OrganizerDrawManager.getFinalEntrants(waitlist, 0);
    }
}
