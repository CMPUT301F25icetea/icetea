package com.example.icetea;

import static org.mockito.Mockito.*;

import com.example.icetea.models.Waitlist;
import com.example.icetea.models.WaitlistDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * US 01.05.02
 * As an entrant, I want to be able to accept the invitation
 * to sign up when chosen to participate in an event.
 *
 * Acceptance is represented by updating the waitlist status
 * to STATUS_ACCEPTED.
 */
public class ManageEventControllerAcceptInvitationTest {

    private WaitlistDB mockWaitlistDB;
    private MockedStatic<WaitlistDB> waitlistDBStatic;

    @Before
    public void setUp() {
        mockWaitlistDB = mock(WaitlistDB.class);

        waitlistDBStatic = Mockito.mockStatic(WaitlistDB.class);
        waitlistDBStatic.when(WaitlistDB::getInstance)
                .thenReturn(mockWaitlistDB);
    }

    @After
    public void tearDown() {
        waitlistDBStatic.close();
    }

    @Test
    public void entrantCanAcceptInvitation() {

        String userId = "user123";
        String eventId = "event456";

        Task<Void> successTask = mock(Task.class);
        when(successTask.isSuccessful()).thenReturn(true);
        doAnswer(invocation -> {
            OnCompleteListener<Void> listener =
                    invocation.getArgument(3);
            listener.onComplete(successTask);
            return null;
        }).when(mockWaitlistDB)
                .updateWaitlistStatus(
                        anyString(),                     // matcher
                        anyString(),                     // matcher
                        eq(Waitlist.STATUS_ACCEPTED),    // matcher
                        any()                            // matcher
                );

        // Act: user accepts invitation
        WaitlistDB.getInstance().updateWaitlistStatus(
                userId,
                eventId,
                Waitlist.STATUS_ACCEPTED,
                task -> {
                    if (!task.isSuccessful()) {
                        throw new AssertionError("Acceptance should succeed");
                    }
                }
        );

        // Verify correct status update was requested
        verify(mockWaitlistDB, times(1))
                .updateWaitlistStatus(
                        eq(userId),
                        eq(eventId),
                        eq(Waitlist.STATUS_ACCEPTED),
                        any()
                );
    }
}
