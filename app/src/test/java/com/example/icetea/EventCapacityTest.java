package com.example.icetea;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
 * US 02.03.01
 * As an organizer I want to OPTIONALLY limit the number of entrants
 * who can join my waiting list.
 */
public class EventCapacityTest {

    private CreateEventController controller;
    private EventDB mockEventDB;
    private MockedStatic<EventDB> staticMockEventDB;
    private MockedStatic<CurrentUser> staticMockCurrentUser;

    @Before
    public void setUp() {
        // Use US locale for consistent date parsing/formatting
        Locale.setDefault(Locale.US);

        // Mock EventDB.getInstance() so we don't hit real Firestore
        mockEventDB = mock(EventDB.class);
        staticMockEventDB = mockStatic(EventDB.class);
        staticMockEventDB.when(EventDB::getInstance).thenReturn(mockEventDB);

        // Mock CurrentUser.getInstance() so the controller has an organizer ID
        CurrentUser mockUser = mock(CurrentUser.class);
        when(mockUser.getFid()).thenReturn("organizer_id");

        staticMockCurrentUser = mockStatic(CurrentUser.class);
        staticMockCurrentUser.when(CurrentUser::getInstance).thenReturn(mockUser);

        // Create the controller we are testing
        controller = new CreateEventController();
    }

    @Test
    public void testEventCapacityIsSavedInEventObject() {
        // Sample event data as if coming from the UI
        String eventName = "Capacity Test Event";
        String eventDescription = "Testing optional capacity";
        String eventCriteria = "criteria";
        String posterBase64 = "poster_data";
        String regStart = "2025-01-01 10:00 AM";
        String regEnd   = "2025-01-02 10:00 AM";
        String eventStart = "2025-01-03 10:00 AM";
        String eventEnd   = "2025-01-03 12:00 PM";
        String eventLocation = "Sample location";

        // Capacity entered as a string in the UI
        String maxEntrants = "10";
        boolean geolocationRequired = false;

        // Mock Task<Void> to simulate a successful Firestore write
        @SuppressWarnings("unchecked")
        Task<Void> mockTask = mock(Task.class);
        when(mockTask.isSuccessful()).thenReturn(true);

        // Intercept EventDB.createEvent() and check the Event passed in
        doAnswer(invocation -> {
            Event savedEvent = invocation.getArgument(0);

            @SuppressWarnings("unchecked")
            OnCompleteListener<Void> listener =
                    (OnCompleteListener<Void>) invocation.getArgument(1);

            assertNotNull(savedEvent);

            // Check that maxEntrants was parsed and stored as 10
            assertEquals("Capacity (maxEntrants) should be 10",
                    Integer.valueOf(10), savedEvent.getMaxEntrants());

            // Simulate Firebase calling onComplete() after success
            listener.onComplete(mockTask);
            return null;
        }).when(mockEventDB).createEvent(any(Event.class), any(OnCompleteListener.class));

        // Track that the success callback was invoked
        final boolean[] successCalled = {false};

        // Call the controller method under test
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
                        // Mark that we got a success callback
                        successCalled[0] = true;
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Test should fail if creation fails
                        fail("Event creation should not fail: " + e.getMessage());
                    }
                }
        );

        // make sure the success callback really happened
        assertTrue("onSuccess should have been called", successCalled[0]);

        // make sure EventDB.createEvent was called  one time
        verify(mockEventDB, times(1)).createEvent(any(Event.class), any());
    }
}
