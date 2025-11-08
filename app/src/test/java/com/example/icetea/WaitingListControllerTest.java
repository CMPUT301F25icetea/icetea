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
 * Mocked test for viewing waitlist
 * US 02.02.01
 * As an organizer I want to view the list of entrants who joined my event waiting list.
 */
public class WaitingListControllerTest {

    private WaitingListController controller;
    private WaitlistDB mockWaitlistDB;
    private UserDB mockUserDB;

    @Before
    public void setUp() {
        // Mock the singleton instances
        mockWaitlistDB = mock(WaitlistDB.class);
        mockUserDB = mock(UserDB.class);

        MockedStatic<WaitlistDB> waitlistStatic = mockStatic(WaitlistDB.class);
        waitlistStatic.when(WaitlistDB::getInstance).thenReturn(mockWaitlistDB);

        MockedStatic<UserDB> userStatic = mockStatic(UserDB.class);
        userStatic.when(UserDB::getInstance).thenReturn(mockUserDB);

        controller = new WaitingListController();
    }

    @Test
    public void testGetWaitingList_ReturnsAllEntries() throws InterruptedException {
        String eventId = "event_123";

        // Mock two documents in WaitlistDB
        QueryDocumentSnapshot doc1 = mock(QueryDocumentSnapshot.class);
        when(doc1.getString("userId")).thenReturn("user1");
        when(doc1.getString("status")).thenReturn("pending");
        when(doc1.get("joinedAt")).thenReturn(System.currentTimeMillis());

        QueryDocumentSnapshot doc2 = mock(QueryDocumentSnapshot.class);
        when(doc2.getString("userId")).thenReturn("user2");
        when(doc2.getString("status")).thenReturn("invited");
        when(doc2.get("joinedAt")).thenReturn(System.currentTimeMillis());

        List<QueryDocumentSnapshot> mockDocs = Arrays.asList(doc1, doc2);

        // Mock WaitlistDB to return these documents for the event
        doAnswer(invocation -> {
            OnCompleteListener mockListener = invocation.getArgument(1);

            // Create a fake QuerySnapshot
            QuerySnapshot mockSnapshot = mock(QuerySnapshot.class);
            when(mockSnapshot.isEmpty()).thenReturn(false);
            when(mockSnapshot.size()).thenReturn(mockDocs.size());
            when(mockSnapshot.iterator()).thenReturn(mockDocs.iterator());

            // Wrap in a Task<QuerySnapshot> mock
            Task<QuerySnapshot> mockTask = mock(Task.class);
            when(mockTask.isSuccessful()).thenReturn(true);
            when(mockTask.getResult()).thenReturn(mockSnapshot);

            // Call the listener
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

        // Call the controller
        controller.getWaitingList(eventId, new Callback<List<WaitingListEntry>>() {
            @Override
            public void onSuccess(List<WaitingListEntry> result) {
                // Verify all entries were returned
                assertEquals(2, result.size());

                WaitingListEntry e1 = result.get(0);
                assertEquals("user1", e1.getUserId());
                assertEquals(eventId, e1.getEventId());
                assertEquals("user1@example.com", e1.getEmail());
                assertEquals("pending", e1.getStatus());
                assertNotNull(e1.getJoinTime());

                WaitingListEntry e2 = result.get(1);
                assertEquals("user2", e2.getUserId());
                assertEquals(eventId, e2.getEventId());
                assertEquals("user2@example.com", e2.getEmail());
                assertEquals("invited", e2.getStatus());
                assertNotNull(e2.getJoinTime());

                latch.countDown();
            }

            @Override
            public void onFailure(Exception e) {
                fail("Should not fail: " + e.getMessage());
                latch.countDown();
            }
        });

        // Wait for async callback
        assertTrue("Callback not invoked in time", latch.await(2, TimeUnit.SECONDS));
    }
}
