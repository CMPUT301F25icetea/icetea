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
 * US 01.04.01
 * Ensures a notification is sent when a user is selected as a winner.
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

        // Replace static getInstance() for both DB singletons
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


    @Test
    public void testNotificationSentWhenUserIsSelected() {

        String userId = "user123";
        String eventId = "eventABC";
        String eventName = "Cool Event";

        Task<DocumentSnapshot> mockUserTask = mock(Task.class);
        DocumentSnapshot mockUserDoc = mock(DocumentSnapshot.class);

        doAnswer(invocation -> {
            OnCompleteListener<DocumentSnapshot> listener =
                    (OnCompleteListener<DocumentSnapshot>) invocation.getArgument(1);

            when(mockUserTask.isSuccessful()).thenReturn(true);
            when(mockUserTask.getResult()).thenReturn(mockUserDoc);

            when(mockUserDoc.exists()).thenReturn(true);
            when(mockUserDoc.getBoolean("notifications")).thenReturn(true); // Notifications ENABLED ✔

            listener.onComplete(mockUserTask);
            return null;
        }).when(mockUserDB).getUser(eq(userId), any());

        // --- Mock NotificationDB.addNotification() ---
        Task<Void> mockAddNotifTask = mock(Task.class);
        when(mockAddNotifTask.isSuccessful()).thenReturn(true);

        doAnswer(invocation -> {
            OnCompleteListener<Void> listener =
                    (OnCompleteListener<Void>) invocation.getArgument(1);

            listener.onComplete(mockAddNotifTask);
            return null;
        }).when(mockNotificationDB).addNotification(any(Notification.class), any());

        // 🔥 Trigger logic directly
        controller.sendNotificationIfEnabled(
                userId,
                "You're a winner!",
                "You have been selected for the event: " + eventName,
                eventId
        );

        // --- Capture created Notification object ---
        ArgumentCaptor<Notification> notificationCaptor =
                ArgumentCaptor.forClass(Notification.class);

        verify(mockNotificationDB, times(1))
                .addNotification(notificationCaptor.capture(), any());

        Notification sentNotif = notificationCaptor.getValue();

        // --- Validate content ---
        assertEquals(userId, sentNotif.getUserId());
        assertEquals(eventId, sentNotif.getEventId());
        assertEquals("You're a winner!", sentNotif.getTitle());
        assertEquals("You have been selected for the event: " + eventName, sentNotif.getMessage());

        assertNotNull("Timestamp should be auto-generated", sentNotif.getTimestamp());
    }
}
