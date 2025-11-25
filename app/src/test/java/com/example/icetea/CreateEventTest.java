package com.example.icetea;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.example.icetea.models.Event;
import com.example.icetea.event.EventController;
import com.example.icetea.models.EventDB;
import com.example.icetea.util.Callback;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.util.ArrayList;

/**
 * Mocked test for EventController.createEvent().
 * US 02.01.01
 * As an organizer I want to create a new event and generate a unique promotional QR code that links to the event description and event poster in the app.
 */
public class CreateEventTest {

    private EventController controller;
    private EventDB mockEventDB;

    @Before
    public void setUp() {
        // mock EventDB singleton
        mockEventDB = mock(EventDB.class);

        MockedStatic<EventDB> staticMock = mockStatic(EventDB.class);
        staticMock.when(EventDB::getInstance).thenReturn(mockEventDB);

        controller = new EventController();
    }

    @Test
    public void testCreateEvent_MockedFirebase() {
        long now = System.currentTimeMillis() / 1000;

        Timestamp rStart = new Timestamp(now, 0);
        Timestamp rEnd = new Timestamp(now + 3600, 0);
        Timestamp start = new Timestamp(now, 0);
        Timestamp end = new Timestamp(now + 7200, 0);

        Event newEvent = new Event(
                "test_id",
                "organizer_id",
                "event_name",
                "event_description",
                "event_location",
                40,
                start,
                end,
                rStart,
                rEnd,
                "test_poster_url",
                new ArrayList<>(),
                new ArrayList<>()
        );

        @SuppressWarnings("unchecked")
        Task<Void> mockTask = mock(Task.class);
        when(mockTask.isSuccessful()).thenReturn(true);

        // when EventDB.saveEvent() is called, immediately call listener.onComplete()

        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            OnCompleteListener<Void> listener = (OnCompleteListener<Void>) invocation.getArgument(1);
            listener.onComplete(mockTask);
            return null;
        }).when(mockEventDB).createEvent(eq(newEvent), any(OnCompleteListener.class));


        final boolean[] successCalled = {false};

        controller.createEvent(newEvent, new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                successCalled[0] = true;
            }

            @Override
            public void onFailure(Exception e) {
                fail("createEvent should not fail: " + e.getMessage());
            }
        });

        assertTrue("onSuccess() should have been called", successCalled[0]);
        verify(mockEventDB, times(1)).createEvent(eq(newEvent), any());
    }
}
