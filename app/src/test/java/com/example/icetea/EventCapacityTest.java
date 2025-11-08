package com.example.icetea;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.example.icetea.event.Event;
import com.example.icetea.event.EventController;
import com.example.icetea.event.EventDB;
import com.example.icetea.util.Callback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.util.ArrayList;

/**
 * Mocked test for registration period
 * US 02.03.01
 * As an organizer I want to OPTIONALLY limit the number of entrants who can join my waiting list.
 */
public class EventCapacityTest {

    private EventController controller;
    private EventDB mockEventDB;

    @Before
    public void setUp() {
        // Mock EventDB singleton
        mockEventDB = mock(EventDB.class);
        MockedStatic<EventDB> staticMock = mockStatic(EventDB.class);
        staticMock.when(EventDB::getInstance).thenReturn(mockEventDB);

        controller = new EventController();
    }

    @Test
    public void testEventCapacitySavedAndFetched() {
        // Create the event
        Event event = new Event();
        event.setId("event_capacity_123");
        event.setName("Capacity Test Event");
        event.setCapacity(10);
        event.setWaitingList(new ArrayList<>());
        long now = System.currentTimeMillis() / 1000;
        event.setStartDate(new Timestamp(now, 0));
        event.setEndDate(new Timestamp(now + 3600, 0));

        // Mock saveEvent() to immediately succeed
        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            OnCompleteListener<Void> listener = (OnCompleteListener<Void>) invocation.getArgument(1);
            Task<Void> mockTask = mock(Task.class);
            when(mockTask.isSuccessful()).thenReturn(true);
            listener.onComplete(mockTask);
            return null;
        }).when(mockEventDB).saveEvent(eq(event), any(OnCompleteListener.class));

        // Mock getEvent() to return the event as if fetched from Firestore
        doAnswer(invocation -> {
            String eventId = invocation.getArgument(0);
            @SuppressWarnings("unchecked")
            OnCompleteListener<DocumentSnapshot> listener = (OnCompleteListener<DocumentSnapshot>) invocation.getArgument(1);

            DocumentSnapshot mockDoc = mock(DocumentSnapshot.class);
            when(mockDoc.exists()).thenReturn(true);
            when(mockDoc.toObject(Event.class)).thenReturn(event);
            when(mockDoc.getId()).thenReturn(eventId);

            Task<DocumentSnapshot> mockTask = mock(Task.class);
            when(mockTask.isSuccessful()).thenReturn(true);
            when(mockTask.getResult()).thenReturn(mockDoc);

            listener.onComplete(mockTask);
            return null;
        }).when(mockEventDB).getEvent(eq(event.getId()), any(OnCompleteListener.class));

        // Use EventController to create the event
        final boolean[] created = {false};
        controller.createEvent(event, new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                created[0] = true;
            }

            @Override
            public void onFailure(Exception e) {
                fail("Event creation failed: " + e.getMessage());
            }
        });
        assertTrue("Event should have been created", created[0]);

        // Fetch the event and verify capacity
        EventDB.getInstance().getEvent(event.getId(), task -> {
            DocumentSnapshot fetchedDoc = task.getResult();
            assertNotNull(fetchedDoc);
            Event fetchedEvent = fetchedDoc.toObject(Event.class);
            assertNotNull(fetchedEvent);
            assertEquals("Capacity should match", 10, fetchedEvent.getCapacity().intValue());
        });
    }
}
