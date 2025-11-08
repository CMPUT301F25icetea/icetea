package com.example.icetea;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.testing.FragmentScenario;

import com.example.icetea.entrant.UserEventDetailsFragment;
import com.example.icetea.models.Event;
import com.example.icetea.models.EventDB;
import com.example.icetea.auth.FBAuthenticator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
//Part of this code was implemented by using GenAI mainly for understanding some implementation process and doing it
/**
 * Test suite for UserEventDetailsFragment functionality.
 * Tests event data display, waitlist status management, and user interactions.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class UserEventDetailsFragmentTest {

    private UserEventDetailsFragment fragment;

    @Mock
    private EventDB mockEventDB;

    @Mock
    private Event mockEvent;

    @Mock
    private Task<DocumentSnapshot> mockTask;

    @Mock
    private DocumentSnapshot mockDocumentSnapshot;

    @Mock
    private FirebaseUser mockFirebaseUser;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        fragment = new UserEventDetailsFragment();
        fragment.eventDB = mockEventDB;

        // Initialize views with Android context
        fragment.tvTotalParticipants = new TextView(RuntimeEnvironment.getApplication());
        fragment.tvWaitlistCount = new TextView(RuntimeEnvironment.getApplication());
        fragment.tvLotteryProcess = new TextView(RuntimeEnvironment.getApplication());
        fragment.btnJoinWaitlist = new Button(RuntimeEnvironment.getApplication());
        fragment.progressBar = new ProgressBar(RuntimeEnvironment.getApplication());
    }

    // ==================== Display Event Data Tests ====================

    @Test
    public void testDisplayEventData_WithValidData() {
        // Arrange
        Event event = new Event();
        event.setCapacity(100);
        event.setWaitlistCount(25);
        event.setLotteryProcess("Random selection from waitlist");
        fragment.currentEvent = event;

        // Act
        fragment.displayEventData();

        // Assert
        assertEquals("100", fragment.tvTotalParticipants.getText().toString());
        assertEquals("25", fragment.tvWaitlistCount.getText().toString());
        assertEquals("Random selection from waitlist", fragment.tvLotteryProcess.getText().toString());
    }

    @Test
    public void testDisplayEventData_WithZeroCapacity() {
        // Arrange
        Event event = new Event();
        event.setCapacity(0);
        event.setWaitlistCount(0);
        event.setLotteryProcess("No lottery yet");
        fragment.currentEvent = event;

        // Act
        fragment.displayEventData();

        // Assert
        assertEquals("0", fragment.tvTotalParticipants.getText().toString());
        assertEquals("0", fragment.tvWaitlistCount.getText().toString());
    }

    @Test
    public void testDisplayEventData_WithNullLotteryProcess() {
        // Arrange
        Event event = new Event();
        event.setCapacity(50);
        event.setWaitlistCount(10);
        event.setLotteryProcess(null);
        fragment.currentEvent = event;

        // Act
        fragment.displayEventData();

        // Assert
        assertEquals("No lottery process information available",
                fragment.tvLotteryProcess.getText().toString());
    }

    @Test
    public void testDisplayEventData_WithEmptyLotteryProcess() {
        // Arrange
        Event event = new Event();
        event.setCapacity(50);
        event.setWaitlistCount(10);
        event.setLotteryProcess("");
        fragment.currentEvent = event;

        // Act
        fragment.displayEventData();

        // Assert
        assertEquals("No lottery process information available",
                fragment.tvLotteryProcess.getText().toString());
    }

    @Test
    public void testDisplayEventData_WithNullEvent() {
        // Arrange
        fragment.currentEvent = null;
        String initialText = "Initial";
        fragment.tvTotalParticipants.setText(initialText);

        // Act
        fragment.displayEventData();

        // Assert - text should remain unchanged
        assertEquals(initialText, fragment.tvTotalParticipants.getText().toString());
    }

    // ==================== Loading State Tests ====================

    @Test
    public void testShowLoading_True() {
        // Act
        fragment.showLoading(true);

        // Assert
        assertEquals(View.VISIBLE, fragment.progressBar.getVisibility());
    }

    @Test
    public void testShowLoading_False() {
        // Arrange
        fragment.progressBar.setVisibility(View.VISIBLE);

        // Act
        fragment.showLoading(false);

        // Assert
        assertEquals(View.GONE, fragment.progressBar.getVisibility());
    }

    @Test
    public void testShowLoading_WithNullProgressBar() {
        // Arrange
        fragment.progressBar = null;

        // Act & Assert - should not throw exception
        fragment.showLoading(true);
        fragment.showLoading(false);
    }

    // ==================== Button State Tests ====================

    @Test
    public void testUpdateButtonState_UserInWaitlist() {
        // Arrange
        fragment.isInWaitlist = true;

        // Use reflection to call private method
        try {
            java.lang.reflect.Method method = UserEventDetailsFragment.class
                    .getDeclaredMethod("updateButtonState");
            method.setAccessible(true);
            method.invoke(fragment);
        } catch (Exception e) {
            fail("Failed to invoke updateButtonState: " + e.getMessage());
        }

        // Assert
        assertEquals("LEAVE WAITLIST", fragment.btnJoinWaitlist.getText().toString());
        assertTrue(fragment.btnJoinWaitlist.isEnabled());
    }

    @Test
    public void testUpdateButtonState_UserNotInWaitlist() {
        // Arrange
        fragment.isInWaitlist = false;

        // Use reflection to call private method
        try {
            java.lang.reflect.Method method = UserEventDetailsFragment.class
                    .getDeclaredMethod("updateButtonState");
            method.setAccessible(true);
            method.invoke(fragment);
        } catch (Exception e) {
            fail("Failed to invoke updateButtonState: " + e.getMessage());
        }

        // Assert
        assertEquals("JOIN WAITLIST", fragment.btnJoinWaitlist.getText().toString());
        assertTrue(fragment.btnJoinWaitlist.isEnabled());
    }

    // ==================== Waitlist Status Tests ====================

    @Test
    public void testIsInWaitlist_InitiallyFalse() {
        // Assert
        assertFalse(fragment.isInWaitlist);
    }

    @Test
    public void testIsInWaitlist_CanBeSetToTrue() {
        // Act
        fragment.isInWaitlist = true;

        // Assert
        assertTrue(fragment.isInWaitlist);
    }

    // ==================== Factory Method Tests ====================

    @Test
    public void testNewInstance_CreatesFragmentWithEventId() {
        // Arrange
        String testEventId = "event123";

        // Act
        UserEventDetailsFragment fragment = UserEventDetailsFragment.newInstance(testEventId);

        // Assert
        assertNotNull(fragment);
        assertNotNull(fragment.getArguments());
        assertEquals(testEventId, fragment.getArguments().getString("event_id"));
    }

    @Test
    public void testNewInstance_WithNullEventId() {
        // Act
        UserEventDetailsFragment fragment = UserEventDetailsFragment.newInstance(null);

        // Assert
        assertNotNull(fragment);
        assertNotNull(fragment.getArguments());
        assertNull(fragment.getArguments().getString("event_id"));
    }

    @Test
    public void testNewInstance_WithEmptyEventId() {
        // Act
        UserEventDetailsFragment fragment = UserEventDetailsFragment.newInstance("");

        // Assert
        assertNotNull(fragment);
        assertEquals("", fragment.getArguments().getString("event_id"));
    }

    // ==================== Event Data Validation Tests ====================

    @Test
    public void testDisplayEventData_WithLargeCapacity() {
        // Arrange
        Event event = new Event();
        event.setCapacity(999999);
        event.setWaitlistCount(50000);
        event.setLotteryProcess("Large event lottery");
        fragment.currentEvent = event;

        // Act
        fragment.displayEventData();

        // Assert
        assertEquals("999999", fragment.tvTotalParticipants.getText().toString());
        assertEquals("50000", fragment.tvWaitlistCount.getText().toString());
    }

    @Test
    public void testDisplayEventData_WithNegativeValues() {
        // Arrange
        Event event = new Event();
        event.setCapacity(-1);
        event.setWaitlistCount(-5);
        event.setLotteryProcess("Test");
        fragment.currentEvent = event;

        // Act
        fragment.displayEventData();

        // Assert - should display the values as-is
        assertEquals("-1", fragment.tvTotalParticipants.getText().toString());
        assertEquals("-5", fragment.tvWaitlistCount.getText().toString());
    }

    @Test
    public void testDisplayEventData_WithLongLotteryText() {
        // Arrange
        String longText = "This is a very long lottery process description that " +
                "contains multiple sentences and a lot of information about " +
                "how the lottery works, including all the rules and regulations " +
                "that participants need to know before entering the waitlist.";
        Event event = new Event();
        event.setCapacity(50);
        event.setWaitlistCount(10);
        event.setLotteryProcess(longText);
        fragment.currentEvent = event;

        // Act
        fragment.displayEventData();

        // Assert
        assertEquals(longText, fragment.tvLotteryProcess.getText().toString());
    }

    // ==================== Fragment Lifecycle Tests ====================

    @Test
    public void testFragmentConstructor_CreatesInstance() {
        // Act
        UserEventDetailsFragment newFragment = new UserEventDetailsFragment();

        // Assert
        assertNotNull(newFragment);
        assertFalse(newFragment.isInWaitlist);
    }

    @Test
    public void testFragmentInitialization_EventDBIsNull() {
        // Arrange
        UserEventDetailsFragment newFragment = new UserEventDetailsFragment();

        // Assert - eventDB should be null until onCreate is called
        assertNull(newFragment.eventDB);
    }

    // ==================== Edge Case Tests ====================

    @Test
    public void testDisplayEventData_CalledMultipleTimes() {
        // Arrange
        Event event1 = new Event();
        event1.setCapacity(50);
        event1.setWaitlistCount(10);
        event1.setLotteryProcess("First lottery");

        Event event2 = new Event();
        event2.setCapacity(100);
        event2.setWaitlistCount(25);
        event2.setLotteryProcess("Second lottery");

        // Act - display first event
        fragment.currentEvent = event1;
        fragment.displayEventData();

        // Assert first event
        assertEquals("50", fragment.tvTotalParticipants.getText().toString());

        // Act - display second event
        fragment.currentEvent = event2;
        fragment.displayEventData();

        // Assert second event overwrites first
        assertEquals("100", fragment.tvTotalParticipants.getText().toString());
        assertEquals("25", fragment.tvWaitlistCount.getText().toString());
        assertEquals("Second lottery", fragment.tvLotteryProcess.getText().toString());
    }

    @Test
    public void testShowLoading_ToggleMultipleTimes() {
        // Act & Assert
        fragment.showLoading(true);
        assertEquals(View.VISIBLE, fragment.progressBar.getVisibility());

        fragment.showLoading(false);
        assertEquals(View.GONE, fragment.progressBar.getVisibility());

        fragment.showLoading(true);
        assertEquals(View.VISIBLE, fragment.progressBar.getVisibility());

        fragment.showLoading(false);
        assertEquals(View.GONE, fragment.progressBar.getVisibility());
    }

    @Test
    public void testWaitlistStatus_ToggleMultipleTimes() {
        // Initial state
        assertFalse(fragment.isInWaitlist);

        // Toggle to true
        fragment.isInWaitlist = true;
        assertTrue(fragment.isInWaitlist);

        // Toggle back to false
        fragment.isInWaitlist = false;
        assertFalse(fragment.isInWaitlist);

        // Toggle to true again
        fragment.isInWaitlist = true;
        assertTrue(fragment.isInWaitlist);
    }



}
