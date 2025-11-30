package com.example.icetea;

import static org.junit.Assert.*;

import org.junit.Test;

import com.example.icetea.models.Waitlist;

import java.util.ArrayList;
import java.util.List;

/**
 * US 02.05.03
 * As an organizer I want to be able to draw a replacement applicant
 * from the pooling system when a previously selected applicant cancels
 * or rejects the invitation.
 *
 * the test simulate ManageEventController.replaceWinner(...) is supposed to perform:
 *  - take a no-longer-valid winner
 *  - pick someone from the waiting pool
 *  - mark them as SELECTED
 */
public class DrawReplacementTest {

    private static final String EVENT_ID = "event123";

    private Waitlist makeEntry(String userId, String status) {
        Waitlist w = new Waitlist();
        w.setUserId(userId);
        w.setEventId(EVENT_ID);
        w.setStatus(status);
        return w;
    }

    @Test
    public void testReplacementWhenWinnerCancels() {
        Waitlist currentWinner = makeEntry("winnerUser", Waitlist.STATUS_SELECTED);

        Waitlist waiting1 = makeEntry("userA", Waitlist.STATUS_WAITING);
        Waitlist waiting2 = makeEntry("userB", Waitlist.STATUS_WAITING);

        List<Waitlist> waitingPool = new ArrayList<>();
        waitingPool.add(waiting1);
        waitingPool.add(waiting2);

        currentWinner.setStatus(Waitlist.STATUS_CANCELLED);

        Waitlist replacement = waitingPool.get(0);
        replacement.setStatus(Waitlist.STATUS_SELECTED);

        assertNotEquals("Original winner should not stay selected",
                Waitlist.STATUS_SELECTED, currentWinner.getStatus());

        assertEquals("Replacement should be marked as SELECTED",
                Waitlist.STATUS_SELECTED, replacement.getStatus());

        assertEquals(EVENT_ID, replacement.getEventId());

        assertNotEquals(currentWinner.getUserId(), replacement.getUserId());
    }

    @Test
    public void testReplacementWhenWinnerDeclines() {
        Waitlist currentWinner = makeEntry("winnerUser", Waitlist.STATUS_SELECTED);

        Waitlist waiting = makeEntry("userA", Waitlist.STATUS_WAITING);

        List<Waitlist> waitingPool = new ArrayList<>();
        waitingPool.add(waiting);

        currentWinner.setStatus(Waitlist.STATUS_DECLINED);

        Waitlist replacement = waitingPool.get(0);
        replacement.setStatus(Waitlist.STATUS_SELECTED);


        assertNotEquals("Declined winner should not be SELECTED",
                Waitlist.STATUS_SELECTED, currentWinner.getStatus());

        assertEquals("Waiting user should become SELECTED",
                Waitlist.STATUS_SELECTED, replacement.getStatus());
        assertEquals("userA", replacement.getUserId());
    }
}
