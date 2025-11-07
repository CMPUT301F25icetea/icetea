package com.example.icetea;

import static org.junit.Assert.*;
import org.junit.Test;

import com.example.icetea.organizer.OrganizerDrawManager;

/**
 * US 02.05.01 – As an organizer, I want to send notifications
 * to chosen entrants to sign up for events. check if we are sending out the correct type
 */
public class OrganizerDrawManagerTest {

    @Test
    public void testCanGetWonNotification() {
        assertEquals("won", OrganizerDrawManager.getNotificationType(0, 3));
        assertEquals("won", OrganizerDrawManager.getNotificationType(2, 5));
    }

    @Test
    public void testCanGetLostNotification() {
        assertEquals("lost", OrganizerDrawManager.getNotificationType(5, 3));
        assertEquals("lost", OrganizerDrawManager.getNotificationType(10, 10));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidDrawSizeThrowsException() {
        OrganizerDrawManager.getNotificationType(0, 0);
    }
}

