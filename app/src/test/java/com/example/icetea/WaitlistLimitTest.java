package com.example.icetea;

import static org.junit.Assert.*;

import org.junit.Test;

import com.example.icetea.models.Event;

/**
 * US 02.03.01 – As an entrant, I want to join
 * the event waitlist only if the limit is not exceeded.
 *
 */
public class WaitlistLimitTest {

    /**
     * Helper that represents the rule:
     * "can this entrant join this event's waitlist?"
     */
    private boolean canJoin(Event event) {
        Integer max = event.getMaxEntrants();
        Integer current = event.getCurrentEntrants();
        if (current == null) current = 0;

        if (max == null) {
            return true;
        }
        return current < max;
    }

    @Test
    public void testCanJoinWhenUnderLimit() {
        Event event = new Event();
        event.setMaxEntrants(10);
        event.setCurrentEntrants(5);

        assertTrue("Should be allowed to join when under the limit", canJoin(event));
    }

    @Test
    public void testCannotJoinWhenAtLimit() {
        Event event = new Event();
        event.setMaxEntrants(10);
        event.setCurrentEntrants(10);

        assertFalse("Should NOT be allowed to join when at the limit", canJoin(event));
    }

    @Test
    public void testCannotJoinWhenOverLimit() {
        Event event = new Event();
        event.setMaxEntrants(10);
        event.setCurrentEntrants(12);

        assertFalse("Should NOT be allowed to join when over the limit", canJoin(event));
    }

    @Test
    public void testCanJoinWhenNoLimitSet() {
        Event event = new Event();
        event.setMaxEntrants(null);
        event.setCurrentEntrants(50);

        assertTrue("Should be allowed to join when there is no limit", canJoin(event));
    }
}
