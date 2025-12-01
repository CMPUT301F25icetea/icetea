package com.example.icetea;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.example.icetea.home.ManageEventController;
import com.example.icetea.models.Notification;
import com.example.icetea.models.NotificationDB;
import com.example.icetea.models.UserDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * US 01.04.01 & US 01.04.02
 * Ensures notifications are sent when a user wins OR loses the lottery.
 */
public class ManageEventControllerNotificationTest {

    private ManageEventController controller;
    private UserDB mockUserDB;
    private NotificationDB mockNotificationDB;

    private MockedStatic<UserDB> userDBStatic;
    private MockedStatic<NotificationDB> notifDBStatic;

    @Before
    public void setup() {
        controller = new ManageEventController();

        mockUserDB = mock(UserDB.class);
        mockNotificationDB = mock(NotificationDB.class);

        userDBStatic = Mockito.mockStatic(UserDB.class);
        userDBStatic.when(UserDB::getInstance).thenReturn(mockUserDB);

        notifDBStatic = Mockito.mockStatic(NotificationDB.class);
        notifDBStatic.when(NotificationDB::getInstance).thenReturn(mockNotificationDB);
    }

    @After
    public void tearDown() {
        userDBStatic.close();
        notifDBStatic.close();
    }

    /**
     * ✅ US 01.04.01
     * Entrant receives notification when selected (WIN)
     */
    @Test
    public void testWinnerNotificationSent() {

        String userId = "winnerUser";
        String eventId = "event123";
        String eventName = "Cool Event";

        mockUserWithNotificationsEnabled(userId);
        mockNotificationAddSuccess();

        controller.sendNotificationIfEnabled(
                userId,
                "You're a winner!",
                "You have been selected for the event: " + eventName,
                eventId
        );

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(mockNotificationDB, times(1))
                .addNotification(captor.capture(), any());

        Notification sent = captor.getValue();

        assertEquals(userId, sent.getUserId());
        assertEquals(eventId, sent.getEventId());
        assertEquals("You're a winner!", sent.getTitle());
        assertEquals("You have been selected for the event: " + eventName, sent.getMessage());
        assertNotNull(sent.getTimestamp());
    }

    /**
     * ✅ US 01.04.02
     * Entrant receives notification when NOT selected (LOSE)
     */
    @Test
    public void testLoserNotificationSent() {

        String userId = "loserUser";
        String eventId = "event123";
        String eventName = "Cool Event";

        mockUserWithNotificationsEnabled(userId);
        mockNotificationAddSuccess();

        controller.sendNotificationIfEnabled(
                userId,
                "Event Results",
                "You were not selected for the event: " + eventName +
                        ". However you can still be selected if someone else declines their offer.",
                eventId
        );

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(mockNotificationDB, times(1))
                .addNotification(captor.capture(), any());

        Notification sent = captor.getValue();

        assertEquals(userId, sent.getUserId());
        assertEquals(eventId, sent.getEventId());
        assertEquals("Event Results", sent.getTitle());
        assertTrue(sent.getMessage().contains("not selected"));
        assertNotNull(sent.getTimestamp());
    }


    private void mockUserWithNotificationsEnabled(String userId) {
        Task<DocumentSnapshot> mockTask = mock(Task.class);
        DocumentSnapshot mockDoc = mock(DocumentSnapshot.class);

        doAnswer(invocation -> {
            OnCompleteListener<DocumentSnapshot> listener =
                    invocation.getArgument(1);

            when(mockTask.isSuccessful()).thenReturn(true);
            when(mockTask.getResult()).thenReturn(mockDoc);
            when(mockDoc.exists()).thenReturn(true);
            when(mockDoc.getBoolean("notifications")).thenReturn(true);

            listener.onComplete(mockTask);
            return null;
        }).when(mockUserDB).getUser(eq(userId), any());
    }

    private void mockNotificationAddSuccess() {
        Task<Void> mockTask = mock(Task.class);
        when(mockTask.isSuccessful()).thenReturn(true);

        doAnswer(invocation -> {
            OnCompleteListener<Void> listener =
                    invocation.getArgument(1);
            listener.onComplete(mockTask);
            return null;
        }).when(mockNotificationDB).addNotification(any(Notification.class), any());
    }
}
