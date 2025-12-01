package com.example.icetea;

import static org.junit.Assert.*;

import org.junit.Test;

import com.example.icetea.models.Waitlist;

/**
 * US 02.02.02
 * As an organizer I want to see on a map where entrants joined
 * my event waiting list from.
 *
 * This test verifies that:
 *  - When a user joins the waitlist with a location,
 *    the latitude/longitude are stored correctly.
 *  - When a user joins WITHOUT location (allowed when geo is off),
 *    the fields are null.
 */
public class MapViewTest {

    @Test
    public void testWaitlistStoresAcquiredLocation() {
        // Simulated location values obtained from device GPS
        double acquiredLat = 53.5461;
        double acquiredLng = -113.4938;

        // Create waitlist entry and assign location
        Waitlist waitlist = new Waitlist();
        waitlist.setUserId("user123");
        waitlist.setEventId("event123");
        waitlist.setLatitude(acquiredLat);
        waitlist.setLongitude(acquiredLng);

        // make sure fields are not null
        assertNotNull("Latitude should not be null", waitlist.getLatitude());
        assertNotNull("Longitude should not be null", waitlist.getLongitude());

        // Confirm exact coordinate values match what was stored
        assertEquals("Latitude should match acquired value",
                acquiredLat, waitlist.getLatitude(), 0.000001);
        assertEquals("Longitude should match acquired value",
                acquiredLng, waitlist.getLongitude(), 0.000001);
    }

    @Test
    public void testWaitlistWithoutLocation() {
        // A user who joined without providing geolocation
        Waitlist waitlist = new Waitlist();
        waitlist.setUserId("user456");
        waitlist.setEventId("event123");

        // Explicitly set location to null
        waitlist.setLatitude(null);
        waitlist.setLongitude(null);

        // Verify the location fields are indeed null
        assertNull("Latitude should be null when not set", waitlist.getLatitude());
        assertNull("Longitude should be null when not set", waitlist.getLongitude());
    }
}
