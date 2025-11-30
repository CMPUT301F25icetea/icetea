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
 * As an organizer I want to create a new event and generate a unique promotional QR code that links to the event description and event poster in the app.
 */
public class CreateEventTest {

    private CreateEventController controller;
    private EventDB mockEventDB;
    private MockedStatic<EventDB> staticMockEventDB;
    private MockedStatic<CurrentUser> staticMockCurrentUser;

    @Before
    public void setUp() {
        Locale.setDefault(Locale.US);

        mockEventDB = mock(EventDB.class);
        staticMockEventDB = mockStatic(EventDB.class);
        staticMockEventDB.when(EventDB::getInstance).thenReturn(mockEventDB);

        CurrentUser mockUser = mock(CurrentUser.class);
        when(mockUser.getFid()).thenReturn("organizer_id");

        staticMockCurrentUser = mockStatic(CurrentUser.class);
        staticMockCurrentUser.when(CurrentUser::getInstance).thenReturn(mockUser);

        controller = new CreateEventController();
    }

    @Test
    public void testCreateEvent_MockedFirebase() {

        // Strings passed exactly like real UI
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

        @SuppressWarnings("unchecked")
        Task<Void> mockTask = mock(Task.class);
        when(mockTask.isSuccessful()).thenReturn(true);

        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            OnCompleteListener<Void> listener =
                    (OnCompleteListener<Void>) invocation.getArgument(1);
            listener.onComplete(mockTask);
            return null;
        }).when(mockEventDB).createEvent(any(Event.class), any(OnCompleteListener.class));

        final boolean[] successCalled = { false };

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
                        successCalled[0] = true;
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
