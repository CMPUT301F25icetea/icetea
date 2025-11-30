package com.example.icetea;

import static org.junit.Assert.*;

import org.junit.Test;

import com.example.icetea.models.Event;

/**
 * US 02.02.03
 * As an organizer, I want to enable or disable the geolocation
 * requirement for my event.
 *
 * These tests verify that the Event model correctly stores and
 * returns the geolocationRequirement flag.
 */
public class EnableDisableGeoTest {

    @Test
    public void testDefaultGeolocationRequirementIsDisabled() {
        Event event = new Event();

        assertFalse(event.getGeolocationRequirement());
    }

    @Test
    public void testEnableGeolocationRequirement() {
        Event event = new Event();

        event.setGeolocationRequirement(true);

        assertTrue(event.getGeolocationRequirement());
    }

    @Test
    public void testDisableGeolocationRequirement() {
        Event event = new Event();

        event.setGeolocationRequirement(true);
        assertTrue(event.getGeolocationRequirement());

        event.setGeolocationRequirement(false);
        assertFalse(event.getGeolocationRequirement());
    }
}
