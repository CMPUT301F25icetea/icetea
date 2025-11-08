package com.example.icetea;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.*;

import com.example.icetea.organizer.OrganizerDrawManager;

/**
 * US 02.05.02 – As an organizer, I want to set the system
 * to sample a specified number of attendees to register for the event.
 */
public class SelectNumberOfWinnersTest {
    @Test
    public void testSelectsCorrectNumberOfWinners() {
        List<String> waitlist = Arrays.asList("user1", "user2", "user3", "user4", "U5");
        List<String> selected = OrganizerDrawManager.selectWinners(waitlist, 3);
        assertEquals(3, selected.size());

        selected = OrganizerDrawManager.selectWinners(waitlist, 5);
        assertEquals(5, selected.size());

        selected = OrganizerDrawManager.selectWinners(waitlist, 10);
        assertEquals(waitlist.size(), selected.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidDrawSizeThrowsException() {
        List<String> waitlist = Arrays.asList("user1", "user2");
        OrganizerDrawManager.selectWinners(waitlist, 0);
    }

    @Test
    public void testEmptyWaitlistReturnsEmptyList() {
        List<String> waitlist = new ArrayList<>();
        List<String> selected = OrganizerDrawManager.selectWinners(waitlist, 3);
        assertTrue(selected.isEmpty());
    }

    @Test
    public void testSelectedWinnersHaveNoDuplicates() {
        List<String> waitlist = Arrays.asList("user1", "user2", "user3", "user4", "user5");
        List<String> selected = OrganizerDrawManager.selectWinners(waitlist, 4);

        Set<String> unique = new HashSet<>(selected);
        assertEquals("Selected winners should be unique", unique.size(), selected.size());
    }
}

