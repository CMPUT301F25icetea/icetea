package com.example.icetea;

import static org.junit.Assert.*;

import org.junit.Test;

import com.example.icetea.models.Event;

/**
 * US 02.02.03
 * As an organizer, I want to enable or disable the geolocation
 * requirement for my event.
 *
 * This test checks that:
 *  - the geolocation flag starts as FALSE
 *  - it can be enabled (set to TRUE)
 *  - it can be disabled again
 */
public class EnableDisableGeoTest {

    @Test
    public void testDefaultGeolocationRequirementIsDisabled() {
        // Create a new Event with default values
        Event event = new Event();

        // New created events should not require geolocation
        assertFalse(event.getGeolocationRequirement());
    }

    @Test
    public void testEnableGeolocationRequirement() {
        Event event = new Event();

        // Turn geolocation requirement ON
        event.setGeolocationRequirement(true);

        // Confirm it's now enabled
        assertTrue(event.getGeolocationRequirement());
    }

    @Test
    public void testDisableGeolocationRequirement() {
        Event event = new Event();

        // First enable it
        event.setGeolocationRequirement(true);
        assertTrue(event.getGeolocationRequirement());

        // Then disable it again
        event.setGeolocationRequirement(false);

        // Confirm it is disabled
        assertFalse(event.getGeolocationRequirement());
    }
}
