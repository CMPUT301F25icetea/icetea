package com.example.icetea;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.example.icetea.home.ManageEventController;
import com.example.icetea.models.Notification;
import com.example.icetea.models.NotificationDB;
import com.example.icetea.models.UserDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

/**
 * Mocked test for sending notifications to entrants
 * US 02.07.01 US 02.07.02 US 02.07.03
 * As an organizer I want to send notifications to:
 * - all entrants on the waiting list,
 * - all selected entrants,
 * - all cancelled entrants.
 *
 * All of these user stories use the same notification method underneath,
 * so here we verify that sending a notification builds the correct Notification
 * object and passes it to NotificationDB.
 */
public class OrganizerNotificationManagerTest {

    private ManageEventController controller;
    private NotificationDB mockNotificationDB;
    private UserDB mockUserDB;

    @Before
    public void setUp() {
        controller = new ManageEventController();

        mockNotificationDB = mock(NotificationDB.class);
        mockUserDB = mock(UserDB.class);

        MockedStatic<NotificationDB> notificationStatic = mockStatic(NotificationDB.class);
        notificationStatic.when(NotificationDB::getInstance).thenReturn(mockNotificationDB);

        MockedStatic<UserDB> userStatic = mockStatic(UserDB.class);
        userStatic.when(UserDB::getInstance).thenReturn(mockUserDB);

        doAnswer(invocation -> {
            String userId = invocation.getArgument(0);
            @SuppressWarnings("unchecked")
            OnCompleteListener<DocumentSnapshot> listener =
                    (OnCompleteListener<DocumentSnapshot>) invocation.getArgument(1);

            DocumentSnapshot mockDoc = mock(DocumentSnapshot.class);
            when(mockDoc.exists()).thenReturn(true);
            when(mockDoc.getBoolean("notifications")).thenReturn(true);

            @SuppressWarnings("unchecked")
            Task<DocumentSnapshot> mockTask = mock(Task.class);
            when(mockTask.isSuccessful()).thenReturn(true);
            when(mockTask.getResult()).thenReturn(mockDoc);

            listener.onComplete(mockTask);
            return null;
        }).when(mockUserDB).getUser(eq("user123"), any());

        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            OnCompleteListener<Void> listener =
                    (OnCompleteListener<Void>) invocation.getArgument(1);

            @SuppressWarnings("unchecked")
            Task<Void> mockTask = mock(Task.class);
            when(mockTask.isSuccessful()).thenReturn(true);

            listener.onComplete(mockTask);
            return null;
        }).when(mockNotificationDB).addNotification(any(Notification.class), any());
    }

    @Test
    public void testSendNotification_BuildsCorrectNotificationObject() {
        String userId = "user123";
        String eventId = "event456";
        String title = "Status update";
        String message = "This is a test notification.";

        controller.sendNotificationIfEnabled(userId, title, message, eventId);

        ArgumentCaptor<Notification> notificationCaptor =
                ArgumentCaptor.forClass(Notification.class);

        verify(mockNotificationDB, times(1))
                .addNotification(notificationCaptor.capture(), any());

        Notification sent = notificationCaptor.getValue();
        assertNotNull(sent);
        assertEquals(userId, sent.getUserId());
        assertEquals(eventId, sent.getEventId());
        assertEquals(title, sent.getTitle());
        assertEquals(message, sent.getMessage());
        assertNotNull("Timestamp should be set on notification", sent.getTimestamp());
    }
}
