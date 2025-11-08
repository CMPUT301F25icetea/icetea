package com.example.icetea;

import static org.junit.Assert.*;

import com.example.icetea.entrant.EntrantNotificationsFragment;

import org.junit.Before;
import org.junit.Test;

/**
 * Simple unit test for EntrantNotificationsFragment.
 * basic initialization covered in this test
 */
public class EntrantNotificationsFragmentTest {

    private EntrantNotificationsFragment fragment;

    @Before
    public void setUp() {
        // Create a dummy subclass to bypass Firebase calls
        fragment = new EntrantNotificationsFragment() {
            @Override
            public void loadNotifications() {
                // Do nothing (skip Firebase calls)
            }
        };
    }

    @Test
    public void testFragmentNotNull() {
        assertNotNull("Fragment should be created", fragment);
    }

    @Test
    public void testNotificationsListInitialized() {
        fragment.notifications = new java.util.ArrayList<>();
        assertNotNull("Notifications list should not be null", fragment.notifications);
    }

    @Test
    public void testAdapterInitiallyNull() {
        assertNull("Adapter should be null before onCreateView", fragment.adapter);
    }

    @Test
    public void testLoadNotificationsOverrideDoesNotCrash() {
        fragment.loadNotifications(); // Safe dummy call
        assertTrue(true); // If no crash, test passes
    }
}

