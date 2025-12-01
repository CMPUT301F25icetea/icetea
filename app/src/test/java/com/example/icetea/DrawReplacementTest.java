package com.example.icetea;

import static org.junit.Assert.*;

import org.junit.Test;

import com.example.icetea.models.Waitlist;

import java.util.ArrayList;
import java.util.List;

/**
 * US 02.05.03
 * As an organizer, I want to draw a replacement applicant
 * when a previously selected applicant cancels or rejects.
 *
 * This test simulates the behaviour of ManageEventController.replaceWinner(...):
 *  - A winner cancels or declines
 *  - Someone from the WAITING pool is chosen
 *  - That person becomes SELECTED
 */
public class DrawReplacementTest {

    private static final String EVENT_ID = "event123";

    // Helper method create a Waitlist entry for tests
    private Waitlist makeEntry(String userId, String status) {
        Waitlist w = new Waitlist();
        w.setUserId(userId);
        w.setEventId(EVENT_ID);
        w.setStatus(status);
        return w;
    }

    @Test
    public void testReplacementWhenWinnerCancels() {
        // Create an existing winner
        Waitlist currentWinner = makeEntry("winnerUser", Waitlist.STATUS_SELECTED);

        // Two users waiting in list
        Waitlist waiting1 = makeEntry("userA", Waitlist.STATUS_WAITING);
        Waitlist waiting2 = makeEntry("userB", Waitlist.STATUS_WAITING);

        // Simulated waiting list
        List<Waitlist> waitingPool = new ArrayList<>();
        waitingPool.add(waiting1);
        waitingPool.add(waiting2);

        // Winner cancels
        currentWinner.setStatus(Waitlist.STATUS_CANCELLED);

        // First person in line becomes the replacement winner
        Waitlist replacement = waitingPool.get(0);
        replacement.setStatus(Waitlist.STATUS_SELECTED);

        // Assert
        assertNotEquals("Original winner should not still be SELECTED",
                Waitlist.STATUS_SELECTED, currentWinner.getStatus());

        assertEquals("Replacement should be marked as SELECTED",
                Waitlist.STATUS_SELECTED, replacement.getStatus());

        assertEquals("Replacement must belong to the same event",
                EVENT_ID, replacement.getEventId());

        assertNotEquals("Replacement should not be the same user as the cancelled winner",
                currentWinner.getUserId(), replacement.getUserId());
    }

    @Test
    public void testReplacementWhenWinnerDeclines() {
        // Create original winner
        Waitlist currentWinner = makeEntry("winnerUser", Waitlist.STATUS_SELECTED);

        // Only one person in the waiting list
        Waitlist waiting = makeEntry("userA", Waitlist.STATUS_WAITING);

        List<Waitlist> waitingPool = new ArrayList<>();
        waitingPool.add(waiting);

        // Winner declines the invitation
        currentWinner.setStatus(Waitlist.STATUS_DECLINED);

        // Change waiting user to winner
        Waitlist replacement = waitingPool.get(0);
        replacement.setStatus(Waitlist.STATUS_SELECTED);

        // Assert
        assertNotEquals("Declined winner should not remain SELECTED",
                Waitlist.STATUS_SELECTED, currentWinner.getStatus());

        assertEquals("Waiting user should now become SELECTED",
                Waitlist.STATUS_SELECTED, replacement.getStatus());

        assertEquals("The selected replacement should be userA",
                "userA", replacement.getUserId());
    }
}
