package com.example.icetea;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.example.icetea.models.WaitlistDB;
import com.example.icetea.models.WaitingListEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;


/**
 * Mocked test to update an entrants status from invited to cancelled
 * US 02.06.04
 * As an organizer I want to cancel entrants that did not sign up for the event.
 */
public class WaitlistStatusUpdateTest {

    private WaitlistDB mockWaitlistDB;

    @Before
    public void setUp() {
        mockWaitlistDB = mock(WaitlistDB.class);

        MockedStatic<WaitlistDB> waitlistStatic = mockStatic(WaitlistDB.class);
        waitlistStatic.when(WaitlistDB::getInstance).thenReturn(mockWaitlistDB);
    }

    @Test
    public void testUpdateWaitlistStatusToCancelled() {
        // Arrange: a waitlist entry currently "invited"
        WaitingListEntry entry = new WaitingListEntry();
        entry.setEventId("event_789");
        entry.setUserId("user123");
        entry.setStatus("invited");

        // Mock updateWaitlistStatus() to immediately succeed
        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            OnCompleteListener<Void> listener = invocation.getArgument(3);

            // Make sure Task<Void> matches exactly
            Task<Void> mockTask = mock(Task.class);
            when(mockTask.isSuccessful()).thenReturn(true);

            listener.onComplete(mockTask);
            return null;
        }).when(mockWaitlistDB)
                .updateWaitlistStatus(eq(entry.getEventId()), eq(entry.getUserId()), eq("cancelled"), any(OnCompleteListener.class));

        // Act: call updateWaitlistStatus
        WaitlistDB.getInstance().updateWaitlistStatus(
                entry.getEventId(),
                entry.getUserId(),
                "cancelled",
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            // update local object
                            entry.setStatus("cancelled");
                        }
                    }
                });

        // Assert
        assertEquals("cancelled", entry.getStatus());
        verify(mockWaitlistDB, times(1))
                .updateWaitlistStatus(eq(entry.getEventId()), eq(entry.getUserId()), eq("cancelled"), any(OnCompleteListener.class));
    }
}
