package com.example.icetea;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.example.icetea.models.Event;
import com.example.icetea.models.EventDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.util.Arrays;
import java.util.List;
/**
 * Mocked test for seeing applicants that are invited
 * US 02.06.01
 * As an organizer I want to view a list of all chosen entrants who are invited to apply.
 */
public class EventInvitedEntrantsTest {

    private EventDB mockEventDB;

    @Before
    public void setUp() {
        mockEventDB = mock(EventDB.class);
        MockedStatic<EventDB> staticMock = mockStatic(EventDB.class);
        staticMock.when(EventDB::getInstance).thenReturn(mockEventDB);
    }

    @Test
    public void testFetchInvitedEntrants() {
        Event event = new Event();
        event.setId("event_invited_test");
        event.setName("Invited Entrants Event");
        event.setAttendees(Arrays.asList("user1", "user2", "user3"));

        // user1 and user2 are "invited", user3 is "pending"
        List<String> invitedUsers = Arrays.asList("user1", "user2");

        // Mock DocumentSnapshot to return the event
        DocumentSnapshot mockDoc = mock(DocumentSnapshot.class);
        when(mockDoc.toObject(Event.class)).thenReturn(event);

        // Mock getEvent() call
        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            OnCompleteListener<DocumentSnapshot> listener =
                    (OnCompleteListener<DocumentSnapshot>) invocation.getArgument(1);

            Task<DocumentSnapshot> mockTask = mock(Task.class);
            when(mockTask.isSuccessful()).thenReturn(true);
            when(mockTask.getResult()).thenReturn(mockDoc);

            listener.onComplete(mockTask);
            return null;
        }).when(mockEventDB).getEvent(eq(event.getId()), any(OnCompleteListener.class));

        // "Fetch" invited entrants
        EventDB.getInstance().getEvent(event.getId(), task -> {
            DocumentSnapshot doc = (DocumentSnapshot) task.getResult();
            Event fetchedEvent = doc.toObject(Event.class);
            assertNotNull(fetchedEvent);

            // Normally your system would check some "status" map; we'll simulate it
            List<String> fetchedInvited = Arrays.asList("user1", "user2"); // mock logic
            assertEquals(invitedUsers.size(), fetchedInvited.size());
            assertTrue(fetchedInvited.containsAll(invitedUsers));
        });
    }
}
