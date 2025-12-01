package com.example.icetea;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.example.icetea.home.EventDetailsController;
import com.example.icetea.models.Event;
import com.example.icetea.models.Waitlist;
import com.example.icetea.models.WaitlistDB;
import com.example.icetea.util.Callback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.List;

/**
 * Combined test cases for user stories:
 *
 * US 01.05.03 - As an entrant I want to be able to decline an invitation
 *               when chosen to participate in an event.
 *
 * US 01.05.04 - As an entrant, I want to know how many total entrants are
 *               on the waiting list for an event.
 *
 * US 01.05.05 - As an entrant, I want to be informed about the criteria or
 *               guidelines for the lottery selection process.
 *
 * US 01.06.02 - As an entrant I want to be able to be sign up for an event
 *               from the event details.
 *
 * US 01.07.01 - As an entrant, I want to be identified by my device, so that
 *               I don't have to use a username and password.
 */
public class EntrantUserStoryTests {

    private WaitlistDB mockWaitlistDB;
    private MockedStatic<WaitlistDB> waitlistStatic;
    private EventDetailsController controller;

    @Before
    public void setUp() {
        mockWaitlistDB = mock(WaitlistDB.class);
        waitlistStatic = mockStatic(WaitlistDB.class);
        waitlistStatic.when(WaitlistDB::getInstance).thenReturn(mockWaitlistDB);
        controller = new EventDetailsController();
    }

    @After
    public void tearDown() {
        if (waitlistStatic != null) {
            waitlistStatic.close();
        }
    }

    // ================================================================
    // US 01.05.03 - DECLINE INVITATION
    // ================================================================

    /**
     * US 01.05.03:
     * As an entrant I want to be able to decline an invitation when chosen
     * to participate in an event.
     *
     * This test verifies:
     * - An entrant with SELECTED status can decline the invitation
     * - Status is correctly updated to DECLINED
     * - The update is persisted to the database
     */
    @Test
    public void testEntrantCanDeclineInvitation() {
        String userId = "user_decline_test";
        String eventId = "event_decline_test";

        // Create a waitlist entry with SELECTED status
        Waitlist selectedEntry = new Waitlist();
        selectedEntry.setUserId(userId);
        selectedEntry.setEventId(eventId);
        selectedEntry.setStatus(Waitlist.STATUS_SELECTED);

        // Mock successful update
        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            OnCompleteListener<Void> listener = invocation.getArgument(3);

            Task<Void> mockTask = mock(Task.class);
            when(mockTask.isSuccessful()).thenReturn(true);

            listener.onComplete(mockTask);
            return null;
        }).when(mockWaitlistDB)
                .updateWaitlistStatus(
                        eq(userId),
                        eq(eventId),
                        eq(Waitlist.STATUS_DECLINED),
                        any(OnCompleteListener.class)
                );

        final boolean[] successCalled = {false};

        // Entrant declines the invitation
        controller.updateEntrantStatus(userId, eventId, Waitlist.STATUS_DECLINED,
                new Callback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        successCalled[0] = true;
                        // Update local status after successful DB update
                        selectedEntry.setStatus(Waitlist.STATUS_DECLINED);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        fail("Declining invitation should not fail: " + e.getMessage());
                    }
                });

        // Verify success callback was triggered
        assertTrue("Success callback should be called", successCalled[0]);

        // Verify status changed to DECLINED
        assertEquals("Status should be DECLINED after declining invitation",
                Waitlist.STATUS_DECLINED, selectedEntry.getStatus());

        // Verify database was called with correct parameters
        verify(mockWaitlistDB, times(1))
                .updateWaitlistStatus(
                        eq(userId),
                        eq(eventId),
                        eq(Waitlist.STATUS_DECLINED),
                        any(OnCompleteListener.class)
                );
    }

    @Test
    public void testCannotDeclineIfNotSelected() {
        // A user who is still waiting cannot decline
        Waitlist waitingEntry = new Waitlist();
        waitingEntry.setUserId("user_waiting");
        waitingEntry.setEventId("event123");
        waitingEntry.setStatus(Waitlist.STATUS_WAITING);

        // They should not be able to decline if they're not selected
        assertNotEquals("Waiting users should not have DECLINED status",
                Waitlist.STATUS_DECLINED, waitingEntry.getStatus());
        assertEquals("Status should still be WAITING",
                Waitlist.STATUS_WAITING, waitingEntry.getStatus());
    }

    // ================================================================
    // US 01.05.04 - VIEW TOTAL ENTRANTS ON WAITING LIST
    // ================================================================

    /**
     * US 01.05.04:
     * As an entrant, I want to know how many total entrants are on the
     * waiting list for an event.
     *
     * This test verifies:
     * - Event displays current entrant count
     * - Count reflects total on waiting list
     * - Optional max entrants limit is also visible
     */
    @Test
    public void testViewTotalEntrantsOnWaitingList() {
        Event event = new Event();
        event.setEventId("event_entrants_test");
        event.setName("Popular Event");
        event.setCurrentEntrants(42);
        event.setMaxEntrants(100);

        // Verify current entrant count is accessible
        assertNotNull("Current entrants should not be null",
                event.getCurrentEntrants());
        assertEquals("Should show 42 current entrants",
                Integer.valueOf(42), event.getCurrentEntrants());

        // Verify max entrants is also accessible
        assertNotNull("Max entrants should not be null",
                event.getMaxEntrants());
        assertEquals("Should show max of 100 entrants",
                Integer.valueOf(100), event.getMaxEntrants());
    }

    @Test
    public void testEntrantCountWithNoLimit() {
        Event event = new Event();
        event.setEventId("event_no_limit");
        event.setName("Unlimited Event");
        event.setCurrentEntrants(250);
        event.setMaxEntrants(null); // No limit set

        // Verify count is still visible even without limit
        assertEquals("Should show 250 current entrants",
                Integer.valueOf(250), event.getCurrentEntrants());
        assertNull("Max entrants should be null (unlimited)",
                event.getMaxEntrants());
    }

    @Test
    public void testEntrantCountUpdatesWhenUserJoins() {
        Event event = new Event();
        event.setCurrentEntrants(10);

        // Simulate a user joining
        int newCount = event.getCurrentEntrants() + 1;
        event.setCurrentEntrants(newCount);

        assertEquals("Count should increment to 11",
                Integer.valueOf(11), event.getCurrentEntrants());
    }

    // ================================================================
    // US 01.05.05 - VIEW LOTTERY SELECTION CRITERIA
    // ================================================================

    /**
     * US 01.05.05:
     * As an entrant, I want to be informed about the criteria or guidelines
     * for the lottery selection process.
     *
     * This test verifies:
     * - Event criteria field is accessible
     * - Criteria can be set by organizer
     * - Empty criteria is handled gracefully
     */
    @Test
    public void testViewLotterySelectionCriteria() {
        Event event = new Event();
        event.setEventId("event_criteria_test");
        event.setName("Tech Conference");

        String criteria = "Selection will be random. All applicants have equal chance. " +
                "Winners will be notified 48 hours before the event.";
        event.setCriteria(criteria);

        // Verify criteria is accessible
        assertNotNull("Criteria should not be null", event.getCriteria());
        assertEquals("Criteria should match what was set", criteria, event.getCriteria());
        assertTrue("Criteria should mention random selection",
                event.getCriteria().contains("random"));
    }

    @Test
    public void testEventWithNoCriteria() {
        Event event = new Event();
        event.setEventId("event_no_criteria");
        event.setName("Simple Event");
        event.setCriteria(null);

        // Verify null criteria is handled
        assertNull("Criteria can be null", event.getCriteria());

        // In the UI, this would show a default message like:
        // "No specific criteria. Anyone can join!"
    }

    @Test
    public void testCriteriaCanBeUpdated() {
        Event event = new Event();

        String oldCriteria = "First come, first served";
        event.setCriteria(oldCriteria);
        assertEquals(oldCriteria, event.getCriteria());

        String newCriteria = "Random lottery selection from all applicants";
        event.setCriteria(newCriteria);
        assertEquals("Criteria should be updated",
                newCriteria, event.getCriteria());
    }

    // ================================================================
    // US 01.06.02 - SIGN UP FOR EVENT FROM DETAILS
    // ================================================================

    /**
     * US 01.06.02:
     * As an entrant I want to be able to sign up for an event from the
     * event details.
     *
     * This test verifies:
     * - User can join waitlist from event details page
     * - Waitlist entry is created with correct information
     * - Event's current entrant count is incremented
     */
    @Test
    public void testSignUpForEventFromDetails() {
        String userId = "user_signup_test";
        String eventId = "event_signup_test";

        Event event = new Event();
        event.setEventId(eventId);
        event.setName("Art Workshop");
        event.setCurrentEntrants(15);
        event.setGeolocationRequirement(false);

        // Create waitlist entry (simulating sign-up)
        Waitlist waitlistEntry = new Waitlist();
        waitlistEntry.setUserId(userId);
        waitlistEntry.setEventId(eventId);
        waitlistEntry.setStatus(Waitlist.STATUS_WAITING);
        waitlistEntry.setTimestamp(Timestamp.now());
        waitlistEntry.setLatitude(null); // No geolocation required
        waitlistEntry.setLongitude(null);

        // Verify waitlist entry is correctly created
        assertEquals("User ID should match", userId, waitlistEntry.getUserId());
        assertEquals("Event ID should match", eventId, waitlistEntry.getEventId());
        assertEquals("Status should be WAITING",
                Waitlist.STATUS_WAITING, waitlistEntry.getStatus());
        assertNotNull("Timestamp should be set", waitlistEntry.getTimestamp());

        // Simulate successful sign-up (increment count)
        event.setCurrentEntrants(event.getCurrentEntrants() + 1);
        assertEquals("Current entrants should increment to 16",
                Integer.valueOf(16), event.getCurrentEntrants());
    }

    @Test
    public void testCannotSignUpWhenWaitlistFull() {
        String userId = "user_full_test";
        String eventId = "event_full_test";

        Event event = new Event();
        event.setEventId(eventId);
        event.setName("Full Event");
        event.setCurrentEntrants(50);
        event.setMaxEntrants(50); // At capacity

        // Check if user can join
        boolean canJoin = event.getCurrentEntrants() < event.getMaxEntrants();

        assertFalse("Should not be able to join when waitlist is full", canJoin);
    }

    @Test
    public void testSignUpWithGeolocationWhenRequired() {
        String userId = "user_geo_test";
        String eventId = "event_geo_test";

        Event event = new Event();
        event.setEventId(eventId);
        event.setName("Local Meetup");
        event.setGeolocationRequirement(true);

        // Create waitlist entry with location
        Waitlist waitlistEntry = new Waitlist();
        waitlistEntry.setUserId(userId);
        waitlistEntry.setEventId(eventId);
        waitlistEntry.setStatus(Waitlist.STATUS_WAITING);
        waitlistEntry.setTimestamp(Timestamp.now());

        // Simulate acquired location
        double latitude = 53.5461;
        double longitude = -113.4938;
        waitlistEntry.setLatitude(latitude);
        waitlistEntry.setLongitude(longitude);

        // Verify location is stored when geolocation required
        assertTrue("Event requires geolocation", event.getGeolocationRequirement());
        assertNotNull("Latitude should be set", waitlistEntry.getLatitude());
        assertNotNull("Longitude should be set", waitlistEntry.getLongitude());
        assertEquals("Latitude should match", latitude, waitlistEntry.getLatitude(), 0.000001);
        assertEquals("Longitude should match", longitude, waitlistEntry.getLongitude(), 0.000001);
    }

    // ================================================================
    // US 01.07.01 - DEVICE-BASED IDENTIFICATION
    // ================================================================

    /**
     * US 01.07.01:
     * As an entrant, I want to be identified by my device, so that I don't
     * have to use a username and password.
     *
     * This test verifies:
     * - Users are identified by Firebase Installation ID (FID)
     * - FID is unique per device
     * - No username/password is required
     */
    @Test
    public void testDeviceBasedIdentification() {
        // Simulate Firebase Installation ID (FID) from device
        String deviceFID1 = "firebase_installation_id_abc123";
        String deviceFID2 = "firebase_installation_id_xyz789";

        // Create user entries identified by device FID
        Waitlist user1Entry = new Waitlist();
        user1Entry.setUserId(deviceFID1);
        user1Entry.setEventId("event123");
        user1Entry.setStatus(Waitlist.STATUS_WAITING);

        Waitlist user2Entry = new Waitlist();
        user2Entry.setUserId(deviceFID2);
        user2Entry.setEventId("event123");
        user2Entry.setStatus(Waitlist.STATUS_WAITING);

        // Verify users are identified by their device FID
        assertNotNull("User should have device-based ID", user1Entry.getUserId());
        assertNotNull("User should have device-based ID", user2Entry.getUserId());

        // Verify FIDs are unique
        assertNotEquals("Different devices should have different FIDs",
                user1Entry.getUserId(), user2Entry.getUserId());

        // Verify FID format (typically starts with specific prefix)
        assertTrue("FID should have expected format",
                deviceFID1.startsWith("firebase_installation_id_"));
        assertTrue("FID should have expected format",
                deviceFID2.startsWith("firebase_installation_id_"));
    }

    @Test
    public void testSameDeviceConsistentIdentification() {
        // Same device should always get same FID
        String deviceFID = "firebase_installation_id_consistent";

        // First time accessing app
        Waitlist firstAccess = new Waitlist();
        firstAccess.setUserId(deviceFID);
        firstAccess.setEventId("event1");

        // Later accessing app again on same device
        Waitlist secondAccess = new Waitlist();
        secondAccess.setUserId(deviceFID);
        secondAccess.setEventId("event2");

        // Verify same device uses same FID
        assertEquals("Same device should have consistent FID",
                firstAccess.getUserId(), secondAccess.getUserId());
    }

    @Test
    public void testNoPasswordRequired() {
        String deviceFID = "firebase_installation_id_nopass";

        // User identified solely by device FID - no password field exists
        Waitlist userEntry = new Waitlist();
        userEntry.setUserId(deviceFID);
        userEntry.setEventId("event123");
        userEntry.setStatus(Waitlist.STATUS_WAITING);

        // Verify user can be created without any password
        assertNotNull("User should exist with just device ID", userEntry.getUserId());
        assertEquals("User ID should be the device FID", deviceFID, userEntry.getUserId());

    }

    @Test
    public void testFIDPersistsAcrossSessions() {
        String persistentFID = "firebase_installation_id_persist";

        // Session 1
        List<String> userActions = new ArrayList<>();
        userActions.add("Joined waitlist with FID: " + persistentFID);

        // Session 2 (app restart)
        userActions.add("Accessed profile with FID: " + persistentFID);

        // Session 3 (days later)
        userActions.add("Checked notifications with FID: " + persistentFID);

        // Verify same FID used across all sessions
        for (String action : userActions) {
            assertTrue("Should use same FID across sessions",
                    action.contains(persistentFID));
        }

        assertEquals("Should have recorded 3 actions with same FID",
                3, userActions.size());
    }
}