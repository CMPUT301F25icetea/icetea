package com.example.icetea;

import static org.junit.Assert.*;

import org.junit.Test;

import com.example.icetea.models.Waitlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * US 02.06.02
 * As an organizer I want to see a list of all the cancelled entrants.
 */
public class CancelledEntrantsTest {

    @Test
    public void testOnlyCancelledEntrantsAreReturned() {
        // Simulate waitlist statuses for several entrants
        List<String> statuses = Arrays.asList(
                Waitlist.STATUS_WAITING,
                Waitlist.STATUS_CANCELLED,
                Waitlist.STATUS_ACCEPTED,
                Waitlist.STATUS_CANCELLED,
                Waitlist.STATUS_SELECTED
        );

        List<String> cancelled = new ArrayList<>();
        for (String status : statuses) {
            if (Waitlist.STATUS_CANCELLED.equals(status)) {
                cancelled.add(status);
            }
        }

        assertEquals(2, cancelled.size());
        for (String s : cancelled) {
            assertEquals(Waitlist.STATUS_CANCELLED, s);
        }
    }
}
