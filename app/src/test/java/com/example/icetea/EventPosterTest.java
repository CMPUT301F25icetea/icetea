package com.example.icetea;

import static org.junit.Assert.*;

import org.junit.Test;

import com.example.icetea.models.Event;

/**
 * US 02.04.01
 * US 02.04.02
 * As an organizer, I want to upload an event poster so entrants can see
 * visual information about the event.
 *
 * These tests ensure that the Event model correctly stores and retrieves
 * the Base64 string that represents the poster image.
 */
public class EventPosterTest {

    @Test
    public void testDefaultPosterIsNull() {
        // A new Event should not have a poster by default
        Event event = new Event();

        assertNull(event.getPosterBase64());
    }

    @Test
    public void testSetPosterBase64() {
        Event event = new Event();

        // Example Base64 image string
        String posterBase64 = "iVBORw0KGgoAAAANSUhEUgAAA...";

        // Set the poster on the event
        event.setPosterBase64(posterBase64);

        // Confirm it was stored correctly
        assertEquals(posterBase64, event.getPosterBase64());
    }

    @Test
    public void testUpdatePosterBase64() {
        Event event = new Event();

        String oldPoster = "OLD_BASE64";
        String newPoster = "NEW_BASE64";

        // First set an initial poster
        event.setPosterBase64(oldPoster);
        assertEquals(oldPoster, event.getPosterBase64());

        // Replace it with a new one
        event.setPosterBase64(newPoster);

        // Confirm the value was overwritten correctly
        assertEquals(newPoster, event.getPosterBase64());
    }
}
