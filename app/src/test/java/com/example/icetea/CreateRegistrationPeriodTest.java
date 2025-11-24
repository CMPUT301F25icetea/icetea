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
 * US 02.01.04
 * As an organizer, I want to set a registration period.
 */
public class CreateRegistrationPeriodTest {

    private EventController controller;
    private EventDB mockEventDB;

    @Before
    public void setUp() {
        mockEventDB = mock(EventDB.class);
        MockedStatic<EventDB> staticMock = mockStatic(EventDB.class);
        staticMock.when(EventDB::getInstance).thenReturn(mockEventDB);
        controller = new EventController();
    }

    @Test
    public void testRegistrationPeriodStoredCorrectly_Mocked() {
        long now = System.currentTimeMillis() / 1000;
        Timestamp rStart = new Timestamp(now, 0);
        Timestamp rEnd = new Timestamp(now + 3600, 0);
        Timestamp start = new Timestamp(now + 7200, 0);
        Timestamp end = new Timestamp(now + 10800, 0);

        Event newEvent = new Event(
                "test_event_reg_period",
                "organizer_id",
                "RegistrationPeriodEvent",
                "Testing registration date fields",
                "Sample location",
                40,
                start,
                end,
                rStart,
                rEnd,
                "test_poster_url",
                new ArrayList<>(),
                new ArrayList<>()
        );

        // Mock saveEvent() to call listener with success
        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            OnCompleteListener<Void> listener = (OnCompleteListener<Void>) invocation.getArgument(1);
            Task<Void> mockTask = mock(Task.class);
            when(mockTask.isSuccessful()).thenReturn(true);
            listener.onComplete(mockTask);
            return null;
        }).when(mockEventDB).createEvent(eq(newEvent), any(OnCompleteListener.class));

        // Mock getEvent() to call listener with a fake DocumentSnapshot
        doAnswer(invocation -> {
            String eventId = invocation.getArgument(0);
            @SuppressWarnings("unchecked")
            OnCompleteListener<DocumentSnapshot> listener = (OnCompleteListener<DocumentSnapshot>) invocation.getArgument(1);

            DocumentSnapshot mockDoc = mock(DocumentSnapshot.class);
            when(mockDoc.exists()).thenReturn(true);
            when(mockDoc.toObject(Event.class)).thenReturn(newEvent);
            when(mockDoc.getId()).thenReturn(eventId);

            Task<DocumentSnapshot> fakeTask = mock(Task.class);
            when(fakeTask.isSuccessful()).thenReturn(true);
            when(fakeTask.getResult()).thenReturn(mockDoc);

            listener.onComplete(fakeTask);
            return null;
        }).when(mockEventDB).getEvent(eq(newEvent.getId()), any(OnCompleteListener.class));

        final boolean[] successCalled = {false};

        controller.createEvent(newEvent, new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                EventDB.getInstance().getEvent(newEvent.getId(), task -> {
                    DocumentSnapshot doc = task.getResult();
                    Event fetchedEvent = doc.toObject(Event.class);

                    assertNotNull(fetchedEvent);
                    assertEquals(rStart.getSeconds(), fetchedEvent.getRegistrationStartDate().getSeconds());
                    assertEquals(rEnd.getSeconds(), fetchedEvent.getRegistrationEndDate().getSeconds());

                    successCalled[0] = true;
                });
            }

            @Override
            public void onFailure(Exception e) {
                fail("Event creation failed: " + e.getMessage());
            }
        });

        assertTrue("onSuccess should have been called", successCalled[0]);
    }
}
