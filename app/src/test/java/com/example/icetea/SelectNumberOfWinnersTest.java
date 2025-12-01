package com.example.icetea;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.example.icetea.home.ManageEventController;
import com.example.icetea.models.Event;
import com.example.icetea.models.Waitlist;
import com.example.icetea.models.WaitlistDB;
import com.example.icetea.util.Callback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Test;
import org.mockito.MockedStatic;

import java.util.List;

/**
 * US 02.05.02
 * As an organizer, I want to select a specific number of winners.
 */
public class SelectNumberOfWinnersTest {

    @Test
    public void testDrawWinnersFailsWhenCountIsNonPositive() {
        ManageEventController controller = new ManageEventController();

        Event event = new Event();
        event.setEventId("event123");
        event.setName("Sample Event");

        final boolean[] failureCalled = {false};
        final Exception[] captured = {null};

        // Try to draw 0 winners
        controller.drawWinners(event, 0, new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                fail("drawWinners should not succeed when count <= 0");
            }

            @Override
            public void onFailure(Exception e) {
                failureCalled[0] = true;
                captured[0] = e;
            }
        });

        // Verify expected failure
        assertTrue("onFailure should have been called for count <= 0", failureCalled[0]);
        assertTrue("Exception should be IllegalArgumentException",
                captured[0] instanceof IllegalArgumentException);
        assertEquals("Count must be greater than 0", captured[0].getMessage());
    }

    @Test
    public void testDrawWinnersFailsWhenNotEnoughEntrants() {
        ManageEventController controller = new ManageEventController();

        Event event = new Event();
        event.setEventId("event456");
        event.setName("Not Enough Entrants Event");

        int requestedCount = 5;  // Asking for 5 but only 2 entries exist

        WaitlistDB mockWaitlistDB = mock(WaitlistDB.class);

        // Mock static getInstance() for WaitlistDB and FirebaseFirestore
        try (MockedStatic<WaitlistDB> waitlistStatic = mockStatic(WaitlistDB.class);
             MockedStatic<FirebaseFirestore> firestoreStatic = mockStatic(FirebaseFirestore.class)) {

            // Return mocked DB instances
            waitlistStatic.when(WaitlistDB::getInstance).thenReturn(mockWaitlistDB);

            FirebaseFirestore mockDb = mock(FirebaseFirestore.class);
            firestoreStatic.when(FirebaseFirestore::getInstance).thenReturn(mockDb);

            // only TWO waiting entrants returned
            doAnswer(invocation -> {
                String eventId = invocation.getArgument(0);
                String status = invocation.getArgument(1);

                @SuppressWarnings("unchecked")
                OnCompleteListener<QuerySnapshot> listener =
                        (OnCompleteListener<QuerySnapshot>) invocation.getArgument(2);

                // Verify correct params passed to DB
                assertEquals("event456", eventId);
                assertEquals(Waitlist.STATUS_WAITING, status);

                // Fake Firestore response
                QuerySnapshot mockSnapshot = mock(QuerySnapshot.class);
                DocumentSnapshot doc1 = mock(DocumentSnapshot.class);
                DocumentSnapshot doc2 = mock(DocumentSnapshot.class);
                List<DocumentSnapshot> docs = List.of(doc1, doc2);

                when(mockSnapshot.getDocuments()).thenReturn(docs);
                when(mockSnapshot.isEmpty()).thenReturn(false);

                // Fake successful Firestore task
                @SuppressWarnings("unchecked")
                Task<QuerySnapshot> mockTask = mock(Task.class);
                when(mockTask.isSuccessful()).thenReturn(true);
                when(mockTask.getResult()).thenReturn(mockSnapshot);

                // Trigger callback
                listener.onComplete(mockTask);
                return null;
            }).when(mockWaitlistDB)
                    .getEntrantsByStatus(eq(event.getEventId()), eq(Waitlist.STATUS_WAITING), any());

            final boolean[] failureCalled = {false};
            final Exception[] captured = {null};

            // should failed
            controller.drawWinners(event, requestedCount, new Callback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    fail("drawWinners should not succeed when there are not enough entrants");
                }

                @Override
                public void onFailure(Exception e) {
                    failureCalled[0] = true;
                    captured[0] = e;
                }
            });

            // failure
            assertTrue("onFailure should have been called when entrants < count", failureCalled[0]);
            assertNotNull(captured[0]);
            assertTrue(
                    "Error message should mention 'Not enough entrants'",
                    captured[0].getMessage().contains("Not enough entrants")
            );
        }
    }
}
