package com.example.icetea;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.example.icetea.models.UserDB;
import com.example.icetea.models.WaitlistDB;
import com.example.icetea.models.WaitingListController;
import com.example.icetea.models.WaitingListEntry;
import com.example.icetea.util.Callback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Mocked test for seeing applicants that have cancelled
 * US 02.06.02
 * As an organizer I want to see a list of all the cancelled entrants.
 */
public class WaitingListControllerCancelledTest {

    private WaitingListController controller;
    private WaitlistDB mockWaitlistDB;
    private UserDB mockUserDB;

    @Before
    public void setUp() {
        mockWaitlistDB = mock(WaitlistDB.class);
        mockUserDB = mock(UserDB.class);

        MockedStatic<WaitlistDB> waitlistStatic = mockStatic(WaitlistDB.class);
        waitlistStatic.when(WaitlistDB::getInstance).thenReturn(mockWaitlistDB);

        MockedStatic<UserDB> userStatic = mockStatic(UserDB.class);
        userStatic.when(UserDB::getInstance).thenReturn(mockUserDB);

        controller = new WaitingListController();
    }

    @Test
    public void testGetCancelledEntrants() throws InterruptedException {
        String eventId = "event_456";

        // Mock two waitlist documents, one cancelled
        QueryDocumentSnapshot doc1 = mock(QueryDocumentSnapshot.class);
        when(doc1.getString("userId")).thenReturn("user1");
        when(doc1.getString("status")).thenReturn("pending");
        when(doc1.get("joinedAt")).thenReturn(System.currentTimeMillis());

        QueryDocumentSnapshot doc2 = mock(QueryDocumentSnapshot.class);
        when(doc2.getString("userId")).thenReturn("user2");
        when(doc2.getString("status")).thenReturn("cancelled");
        when(doc2.get("joinedAt")).thenReturn(System.currentTimeMillis());

        List<QueryDocumentSnapshot> mockDocs = Arrays.asList(doc1, doc2);

        // Mock WaitlistDB to return these documents
        doAnswer(invocation -> {
            OnCompleteListener mockListener = invocation.getArgument(1);

            QuerySnapshot mockSnapshot = mock(QuerySnapshot.class);
            when(mockSnapshot.isEmpty()).thenReturn(false);
            when(mockSnapshot.size()).thenReturn(mockDocs.size());
            when(mockSnapshot.iterator()).thenReturn(mockDocs.iterator());

            Task<QuerySnapshot> mockTask = mock(Task.class);
            when(mockTask.isSuccessful()).thenReturn(true);
            when(mockTask.getResult()).thenReturn(mockSnapshot);

            mockListener.onComplete(mockTask);
            return null;
        }).when(mockWaitlistDB).getWaitlistForEvent(eq(eventId), any(OnCompleteListener.class));

        // Mock UserDB to return emails
        doAnswer(invocation -> {
            String userId = invocation.getArgument(0);
            @SuppressWarnings("unchecked")
            Callback<String> cb = (Callback<String>) invocation.getArgument(1);
            cb.onSuccess(userId + "@example.com");
            return null;
        }).when(mockUserDB).getUserEmail(anyString(), any());

        CountDownLatch latch = new CountDownLatch(1);

        // Call controller
        controller.getWaitingList(eventId, new Callback<List<WaitingListEntry>>() {
            @Override
            public void onSuccess(List<WaitingListEntry> result) {
                assertEquals(2, result.size());

                WaitingListEntry cancelledEntry = result.stream()
                        .filter(e -> "cancelled".equals(e.getStatus()))
                        .findFirst()
                        .orElse(null);

                assertNotNull("Cancelled entry should be present", cancelledEntry);
                assertEquals("user2", cancelledEntry.getUserId());
                assertEquals(eventId, cancelledEntry.getEventId());
                assertEquals("user2@example.com", cancelledEntry.getEmail());

                latch.countDown();
            }

            @Override
            public void onFailure(Exception e) {
                fail("Should not fail: " + e.getMessage());
                latch.countDown();
            }
        });

        assertTrue("Callback not invoked in time", latch.await(2, TimeUnit.SECONDS));
    }
}
