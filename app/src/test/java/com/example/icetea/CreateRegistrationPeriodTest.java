package com.example.icetea;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.icetea.auth.CurrentUser;
import com.example.icetea.home.CreateEventController;
import com.example.icetea.models.Event;
import com.example.icetea.models.EventDB;
import com.example.icetea.util.Callback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.util.Locale;

/**
 * Tests that the registration period (start/end dates) is stored correctly
 * when creating an event.
 *
 * US 02.01.04:
 * As an organizer, I want to set a registration period.
 */
public class CreateRegistrationPeriodTest {

    private CreateEventController controller;
    private EventDB mockEventDB;

    @Before
    public void setUp() {
        // Make sure date parsing uses US format
        Locale.setDefault(Locale.US);

        // Mock EventDB.getInstance()
        mockEventDB = mock(EventDB.class);
        MockedStatic<EventDB> staticMockEventDB = mockStatic(EventDB.class);
        staticMockEventDB.when(EventDB::getInstance).thenReturn(mockEventDB);

        // Mock CurrentUser.getInstance()
        CurrentUser mockUser = mock(CurrentUser.class);
        when(mockUser.getFid()).thenReturn("organizer_id");

        MockedStatic<CurrentUser> staticMockUser = mockStatic(CurrentUser.class);
        staticMockUser.when(CurrentUser::getInstance).thenReturn(mockUser);

        // Controller under test
        controller = new CreateEventController();
    }

    @Test
    public void testRegistrationPeriodStoredCorrectly_Mocked() {
        // Registration and event time strings exactly like the UI would send
        String regStart = "2025-01-01 10:00 AM";
        String regEnd   = "2025-01-01 11:00 AM";
        String eventStart = "2025-01-02 10:00 AM";
        String eventEnd   = "2025-01-02 12:00 PM";

        // Convert to Firebase Timestamps (
        Timestamp expectedRStart = controller.textToTimestamp(regStart);
        Timestamp expectedREnd   = controller.textToTimestamp(regEnd);

        // Mock Firebase Task
        @SuppressWarnings("unchecked")
        Task<Void> mockTask = mock(Task.class);
        when(mockTask.isSuccessful()).thenReturn(true);

        // Capture the Event object and verify its registration timestamps
        doAnswer(invocation -> {
            Event savedEvent = invocation.getArgument(0);

            @SuppressWarnings("unchecked")
            OnCompleteListener<Void> listener =
                    (OnCompleteListener<Void>) invocation.getArgument(1);

            // Check that registration timestamps exist
            assertNotNull(savedEvent.getRegistrationStartDate());
            assertNotNull(savedEvent.getRegistrationEndDate());

            // Check correct seconds values
            assertEquals(expectedRStart.getSeconds(),
                    savedEvent.getRegistrationStartDate().getSeconds());

            assertEquals(expectedREnd.getSeconds(),
                    savedEvent.getRegistrationEndDate().getSeconds());

            // Simulate Firebase calling onComplete()
            listener.onComplete(mockTask);
            return null;
        }).when(mockEventDB).createEvent(any(Event.class), any(OnCompleteListener.class));

        // whether success callback is triggered
        final boolean[] successCalled = {false};

        // Call controller to create the event
        controller.createEvent(
                "RegistrationPeriodEvent",
                "Testing registration date fields",
                "criteria",
                "test_poster_base64",
                regStart,
                regEnd,
                eventStart,
                eventEnd,
                "Sample location",
                "40",
                false,
                new Callback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        successCalled[0] = true;
                    }

                    @Override
                    public void onFailure(Exception e) {
                        fail("Event creation failed: " + e.getMessage());
                    }
                }
        );

        // Confirm success callback happened
        assertTrue("onSuccess should have been called", successCalled[0]);

        // Confirm EventDB.createEvent() was called exactly once
        verify(mockEventDB, times(1)).createEvent(any(Event.class), any());
    }
}
