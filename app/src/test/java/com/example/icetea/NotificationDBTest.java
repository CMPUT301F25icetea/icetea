//package com.example.icetea;
//
//import static org.mockito.Mockito.*;
//import static org.junit.Assert.*;
//
//import com.example.icetea.models.Notification;
//import com.example.icetea.models.NotificationDB;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.Timestamp;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.Query;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.MockedStatic;
//import org.mockito.Mockito;
//
//*
// * Tests NotificationDB behaviour for entrant notifications.
// * Covers:
// *  - US 01.04.01: Entrant receives win notification
// *  - US 01.04.02: Entrant receives loss notification
// *  - US 01.05.02: Entrant accepts invitation
// *  - US 01.05.03: Entrant declines invitation
//
//
//public class NotificationDBTest {
//
//    private NotificationDB notificationDB;
//    private FirebaseFirestore mockFirestore;
//    private CollectionReference mockCollection;
//
//    @Before
//    public void setUp() {
//        mockFirestore = mock(FirebaseFirestore.class);
//        mockCollection = mock(CollectionReference.class);
//
//        try (MockedStatic<FirebaseFirestore> firestoreStatic = Mockito.mockStatic(FirebaseFirestore.class)) {
//            firestoreStatic.when(FirebaseFirestore::getInstance).thenReturn(mockFirestore);
//            when(mockFirestore.collection("Notification")).thenReturn(mockCollection);
//            notificationDB = new NotificationDB();
//        }
//    }
//
//    @Test
//    public void testAddWinNotification() {
//        Notification winNotification = new Notification(
//                "N1", "U1", "E1", "Marathon", "won", "Congratulations! You’ve been selected!",
//                Timestamp.now()
//        );
//
//        assertEquals("won", winNotification.getType());
//        assertTrue(winNotification.isWonNotification());
//        assertTrue(winNotification.isPending());
//    }
//
//    @Test
//    public void testAddLossNotification() {
//        Notification loseNotification = new Notification(
//                "N2", "U1", "E1", "Marathon", "lost", "Sorry, you were not selected.",
//                Timestamp.now()
//        );
//
//        assertEquals("lost", loseNotification.getType());
//        assertFalse(loseNotification.isWonNotification());
//        assertEquals("pending", loseNotification.getStatus());
//    }
//
//    @Test
//    public void testAcceptInvitationUpdatesStatus() {
//        Notification notification = new Notification(
//                "N3", "U1", "E1", "Hackathon", "won", "You’re invited to join!", Timestamp.now()
//        );
//        notification.setStatus("accepted");
//        assertTrue(notification.isAccepted());
//        assertFalse(notification.isDeclined());
//    }
//
//    @Test
//    public void testDeclineInvitationUpdatesStatus() {
//        Notification notification = new Notification(
//                "N4", "U1", "E1", "Hackathon", "won", "You’re invited to join!", Timestamp.now()
//        );
//        notification.setStatus("declined");
//        assertTrue(notification.isDeclined());
//        assertFalse(notification.isAccepted());
//    }
//
//    @Test
//    public void testGetNotificationsForUserCallsFirestoreQuery() {
//        Query mockQuery = mock(Query.class);
//        when(mockCollection.whereEqualTo(eq("userId"), anyString())).thenReturn(mockQuery);
//        when(mockQuery.orderBy(eq("timestamp"), eq(Query.Direction.DESCENDING))).thenReturn(mockQuery);
//        when(mockQuery.get()).thenReturn(mock(Task.class));
//
//        notificationDB.getNotificationsForUser("U1", task -> {});
//        verify(mockCollection).whereEqualTo("userId", "U1");
//    }
//}
