package com.example.icetea;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.example.icetea.auth.CurrentUser;
import com.example.icetea.home.CreateEventController;
import com.example.icetea.models.Event;
import com.example.icetea.models.EventDB;
import com.example.icetea.util.Callback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.util.Locale;

/**
 * Mocked test for CreateEventController.createEvent().
 * US 02.01.01
 * Tests that an organizer can create an event and the controller
 * properly calls EventDB.createEvent() and triggers success callback.
 */
public class CreateEventTest {

    private CreateEventController controller;
    private EventDB mockEventDB;
    private MockedStatic<EventDB> staticMockEventDB;
    private MockedStatic<CurrentUser> staticMockCurrentUser;

    @Before
    public void setUp() {
        // Ensures date parsing uses US format
        Locale.setDefault(Locale.US);

        // Mock EventDB.getInstance()
        mockEventDB = mock(EventDB.class);
        staticMockEventDB = mockStatic(EventDB.class);
        staticMockEventDB.when(EventDB::getInstance).thenReturn(mockEventDB);

        // Mock CurrentUser.getInstance()
        CurrentUser mockUser = mock(CurrentUser.class);
        when(mockUser.getFid()).thenReturn("organizer_id");

        staticMockCurrentUser = mockStatic(CurrentUser.class);
        staticMockCurrentUser.when(CurrentUser::getInstance).thenReturn(mockUser);

        // Controller under test
        controller = new CreateEventController();
    }

    @Test
    public void testCreateEvent_MockedFirebase() {

        // Fake inputs like from UI
        String eventName = "event_name";
        String eventDescription = "event_description";
        String eventCriteria = "criteria";
        String posterBase64 = "test_poster_base64";
        String regStart = "2025-01-01 10:00 AM";
        String regEnd = "2025-01-01 11:00 AM";
        String eventStart = "2025-01-02 10:00 AM";
        String eventEnd = "2025-01-02 12:00 PM";
        String eventLocation = "event_location";
        String maxEntrants = "40";
        boolean geolocationRequired = false;

        // Mock Firebase Task that returns "success"
        @SuppressWarnings("unchecked")
        Task<Void> mockTask = mock(Task.class);
        when(mockTask.isSuccessful()).thenReturn(true);

        // Simulate EventDB.createEvent() calling the OnCompleteListener immediately
        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            OnCompleteListener<Void> listener =
                    (OnCompleteListener<Void>) invocation.getArgument(1);
            listener.onComplete(mockTask);   // pretend Firebase finished successfully
            return null;
        }).when(mockEventDB).createEvent(any(Event.class), any(OnCompleteListener.class));

        // Track if onSuccess() was called
        final boolean[] successCalled = { false };

        // Call the controller method
        controller.createEvent(
                eventName,
                eventDescription,
                eventCriteria,
                posterBase64,
                regStart,
                regEnd,
                eventStart,
                eventEnd,
                eventLocation,
                maxEntrants,
                geolocationRequired,
                new Callback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        successCalled[0] = true; // mark success
                    }

                    @Override
                    public void onFailure(Exception e) {
                        fail("createEvent should not fail: " + e.getMessage());
                    }
                }
        );

        assertTrue("onSuccess() should have been called", successCalled[0]);

        verify(mockEventDB, times(1)).createEvent(any(Event.class), any());
    }
}
