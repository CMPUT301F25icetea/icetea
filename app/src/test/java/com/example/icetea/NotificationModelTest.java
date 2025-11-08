package com.example.icetea;

import static org.junit.Assert.*;

import com.example.icetea.entrant.Notification;
import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Test;

public class NotificationModelTest {

    private Notification notif;

    @Before
    public void setUp() {
        notif = new Notification("id1", "user1", "event1", "Event Name",
                "won", "You won!", Timestamp.now());
    }

    @Test
    public void testDefaultStatusIsPending() {
        assertEquals("pending", notif.getStatus());
        assertTrue(notif.isPending());
    }

    @Test
    public void testSetAcceptedAndHelpers() {
        notif.setStatus("accepted");
        assertTrue(notif.isAccepted());
        assertFalse(notif.isDeclined());
        assertFalse(notif.isPending());
    }

    @Test
    public void testSetDeclinedAndHelpers() {
        notif.setStatus("declined");
        assertTrue(notif.isDeclined());
        assertFalse(notif.isAccepted());
        assertFalse(notif.isPending());
    }

    @Test
    public void testCanRespond_whenWonAndPending() {
        notif.setType("won");
        notif.setStatus("pending");
        assertTrue(notif.canRespond());
    }

    @Test
    public void testCanRespond_falseWhenNotWonOrNotPending() {
        notif.setType("lost");
        notif.setStatus("pending");
        assertFalse(notif.canRespond());

        notif.setType("won");
        notif.setStatus("accepted");
        assertFalse(notif.canRespond());
    }
}
