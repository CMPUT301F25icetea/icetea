package com.example.icetea;

import static org.junit.Assert.*;

import org.junit.Test;

import com.example.icetea.models.Event;

/**
 * US 02.04.01
 * US 02.04.02
 * As an organizer, I want to upload an event poster to the event
 * details page to provide visual information to entrants.
 *
 * These tests verify that the Event model correctly stores and
 * returns the Base64-encoded poster image string.
 */
public class EventPosterTest {

    @Test
    public void testDefaultPosterIsNull() {
        Event event = new Event();

        assertNull(event.getPosterBase64());
    }

    @Test
    public void testSetPosterBase64() {
        Event event = new Event();

        String posterBase64 = "iVBORw0KGgoAAAANSUhEUgAAA...";

        event.setPosterBase64(posterBase64);

        assertEquals(posterBase64, event.getPosterBase64());
    }

    @Test
    public void testUpdatePosterBase64() {
        Event event = new Event();

        String oldPoster = "OLD_BASE64";
        String newPoster = "NEW_BASE64";

        event.setPosterBase64(oldPoster);
        assertEquals(oldPoster, event.getPosterBase64());

        event.setPosterBase64(newPoster);
        assertEquals(newPoster, event.getPosterBase64());
    }
}

