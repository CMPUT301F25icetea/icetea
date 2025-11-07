package com.example.icetea;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.icetea.models.WaitlistDB;

/**
 * to test us.02.03.01, see if we successfully add to the waitlist with limit
 */

public class WaitlistDBTest {

    @Test
    public void testCanAddToWaitlist_UnderLimit() {
        assertTrue(WaitlistDB.canAddToWaitlist(3L, 2L));
    }

    @Test
    public void testCanAddToWaitlist_AtLimit() {
        assertFalse(WaitlistDB.canAddToWaitlist(3L, 3L));
    }

    @Test
    public void testCanAddToWaitlist_UnlimitedWhenCapacityZero() {
        assertTrue(WaitlistDB.canAddToWaitlist(0L, 100L));
    }

    @Test
    public void testCanAddToWaitlist_NullCapacityOrCount() {
        assertTrue(WaitlistDB.canAddToWaitlist(null, 10L));

        assertTrue(WaitlistDB.canAddToWaitlist(5L, null));
    }
}


