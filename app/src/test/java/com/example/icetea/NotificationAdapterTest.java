package com.example.icetea;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Context;

import com.example.icetea.models.Notification;
import com.example.icetea.models.NotificationAdapter;
import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

/**
 * Unit tests for NotificationAdapter.
 * These tests avoid real Android/Firebase calls.
 */
public class NotificationAdapterTest {

    private Context mockContext;
    private ArrayList<Notification> notifications;
    private NotificationAdapter adapter;

    @Before
    public void setUp() {
        mockContext = mock(Context.class);
        notifications = new ArrayList<>();

        // Add a "won" notification (pending)
        notifications.add(new Notification(
                "1", "U1", "E1", "Concert Night",
                "won", "You won!", new Timestamp(new Date())
        ));

        // Add a "lost" notification
        notifications.add(new Notification(
                "2", "U2", "E2", "Art Show",
                "lost", "Better luck next time.", new Timestamp(new Date())
        ));

        adapter = new NotificationAdapter(mockContext, notifications);
    }

    @Test
    public void testAdapterItemCountMatchesListSize() {
        assertEquals("Adapter should report same size as list", 2, adapter.getItemCount());}


    @Test
    public void testFirstNotificationIsWonAndPending() {
        Notification n = notifications.get(0);
        assertTrue(n.isWonNotification());
        assertTrue(n.canRespond());
    }

    @Test
    public void testSecondNotificationIsLostAndNotRespondable() {
        Notification n = notifications.get(1);
        assertEquals("lost", n.getType());
        assertFalse(n.canRespond());
    }

    @Test
    public void testAdapterReturnsZeroWhenListIsNull() {
        NotificationAdapter emptyAdapter = new NotificationAdapter(mockContext, null);
        assertEquals(0, emptyAdapter.getItemCount());
    }

    @Test
    public void testAdapterHandlesEmptyListGracefully() {
        NotificationAdapter emptyAdapter = new NotificationAdapter(mockContext, new ArrayList<>());
        assertEquals(0, emptyAdapter.getItemCount());
    }
}
