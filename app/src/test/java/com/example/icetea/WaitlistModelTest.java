package com.example.icetea;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.icetea.models.Waitlist;

/**
 * Test cases for:
 * US 01.01.01 - Entrant joins the waitlist for an event
 * US 01.01.02 - Entrant leaves the waitlist for an event
 */
public class WaitlistModelTest {

    /**
     * US 01.01.01:
     * As an entrant, I want to join the waiting list for a specific event.
     *
     * This test verifies:
     * - A Waitlist object is correctly created
     * - Correct userId and eventId are assigned
     * - Status defaults to "waiting"
     * - Document ID format matches Firestore expectations (userId_eventId)
     */
    @Test
    public void testJoinWaitlistCreatesCorrectEntry() {
        String userId = "user123";
        String eventId = "event123";

        // Simulate joining the waitlist
        Waitlist entry = new Waitlist();
        entry.setUserId(userId);
        entry.setEventId(eventId);
        entry.setStatus(Waitlist.STATUS_WAITING);

        // Assertions
        assertEquals("User ID should match", userId, entry.getUserId());
        assertEquals("Event ID should match", eventId, entry.getEventId());
        assertEquals("Status should be waiting", "waiting", entry.getStatus());

        // Document ID format test
        assertEquals("user123_event123", entry.getId());
    }

    /**
     * US 01.01.02:
     * As an entrant, I want to leave the waiting list for a specific event.
     *
     * This test verifies:
     * - The Waitlist object's ID matches the one that Firestore will delete.
     * - This ensures removeFromWaitlist(userId, eventId) targets the right document.
     */
    @Test
    public void testLeaveWaitlistCorrectDocumentId() {
        String userId = "userABC";
        String eventId = "eventXYZ";

        // Existing waitlist entry
        Waitlist entry = new Waitlist();
        entry.setUserId(userId);
        entry.setEventId(eventId);
        entry.setStatus(Waitlist.STATUS_WAITING);

        // The ID used for deletion
        String expectedId = "userABC_eventXYZ";

        assertEquals("Document ID should match for deletion", expectedId, entry.getId());
    }
}
