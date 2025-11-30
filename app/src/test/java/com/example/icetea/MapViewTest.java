package com.example.icetea;

import static org.junit.Assert.*;

import org.junit.Test;

import com.example.icetea.models.Waitlist;

/**
 * US 02.02.02
 * As an organizer I want to see on a map where entrants joined
 * my event waiting list from.
 *
 * This mocked test verifies that when we acquire a user's location
 * (latitude/longitude) to join the waitlist, those coordinates are
 * correctly stored in the Waitlist model. Later, EntrantsMapFragment
 * reads the same fields ("latitude", "longitude") to display markers
 * on the map.
 */
public class MapViewTest {

    @Test
    public void testWaitlistStoresAcquiredLocation() {
        double acquiredLat = 53.5461;
        double acquiredLng = -113.4938;

        Waitlist waitlist = new Waitlist();
        waitlist.setUserId("user123");
        waitlist.setEventId("event123");
        waitlist.setLatitude(acquiredLat);
        waitlist.setLongitude(acquiredLng);

        assertNotNull("Latitude should not be null", waitlist.getLatitude());
        assertNotNull("Longitude should not be null", waitlist.getLongitude());

        assertEquals("Latitude should match acquired value",
                acquiredLat, waitlist.getLatitude(), 0.000001);
        assertEquals("Longitude should match acquired value",
                acquiredLng, waitlist.getLongitude(), 0.000001);
    }

    @Test
    public void testWaitlistWithoutLocation() {
        Waitlist waitlist = new Waitlist();
        waitlist.setUserId("user456");
        waitlist.setEventId("event123");
        waitlist.setLatitude(null);
        waitlist.setLongitude(null);

        assertNull("Latitude should be null when not set", waitlist.getLatitude());
        assertNull("Longitude should be null when not set", waitlist.getLongitude());
    }
}
