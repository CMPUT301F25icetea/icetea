package com.example.icetea;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.example.icetea.models.Waitlist;
import com.example.icetea.models.WaitlistDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

/**
 * Mocked test to update an entrant's status from invited/selected to cancelled
 * US 02.06.04
 * As an organizer I want to cancel entrants that did not sign up for the event.
 */

public class WaitlistStatusUpdateTest {

    private WaitlistDB mockWaitlistDB;
    private MockedStatic<WaitlistDB> waitlistStatic;

    @Before
    public void setUp() {
        mockWaitlistDB = mock(WaitlistDB.class);
        waitlistStatic = mockStatic(WaitlistDB.class);
        waitlistStatic.when(WaitlistDB::getInstance).thenReturn(mockWaitlistDB);
    }

    @Test
    public void testUpdateWaitlistStatusToCancelled() {
        Waitlist entry = new Waitlist();
        entry.setEventId("event_789");
        entry.setUserId("user123");
        entry.setStatus(Waitlist.STATUS_SELECTED);

        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            OnCompleteListener<Void> listener = invocation.getArgument(3);

            Task<Void> mockTask = mock(Task.class);
            when(mockTask.isSuccessful()).thenReturn(true);

            listener.onComplete(mockTask);
            return null;
        }).when(mockWaitlistDB)
                .updateWaitlistStatus(
                        eq(entry.getUserId()),
                        eq(entry.getEventId()),
                        eq(Waitlist.STATUS_CANCELLED),
                        any(OnCompleteListener.class)
                );

        WaitlistDB.getInstance().updateWaitlistStatus(
                entry.getUserId(),
                entry.getEventId(),
                Waitlist.STATUS_CANCELLED,
                task -> {
                    if (task.isSuccessful()) {
                        // update local object
                        entry.setStatus(Waitlist.STATUS_CANCELLED);
                    }
                });

        assertEquals(Waitlist.STATUS_CANCELLED, entry.getStatus());
        verify(mockWaitlistDB, times(1))
                .updateWaitlistStatus(
                        eq(entry.getUserId()),
                        eq(entry.getEventId()),
                        eq(Waitlist.STATUS_CANCELLED),
                        any(OnCompleteListener.class)
                );

        waitlistStatic.close();
    }
}
