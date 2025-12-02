package com.example.icetea;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.icetea.models.Event;
import com.google.firebase.Timestamp;

import java.util.*;

/**
 * Combined test cases for:
 *
 * US 01.01.03 – As an entrant, I want to be able to see a list of events
 *               that I can join the waiting list for.
 *
 * US 01.01.04 – As an entrant, I want to filter events based on
 *               my interests and availability.
 *
 * US 03.04.01 As an administrator, I want to be able to browse events.
 */
public class EventUserStoryTests {

    // --------------------------------------------------------------
    // Helper Methods
    // --------------------------------------------------------------

    /**
     * Determines if an event is joinable for the waitlist.
     * Used for US 01.01.03.
     */
    private boolean isJoinable(Event e, Timestamp now) {
        boolean registrationOpen =
                e.getRegistrationStartDate().compareTo(now) <= 0 &&
                        e.getRegistrationEndDate().compareTo(now) >= 0;

        boolean isFull = e.getCurrentEntrants() >= e.getMaxEntrants();
        boolean notDrew = !e.getAlreadyDrew();

        return registrationOpen && isFull && notDrew;
    }

    /**
     * Checks if event matches the user's interest keyword.
     * Used for US 01.01.04.
     */
    private boolean matchesInterest(Event e, String interest) {
        if (e.getDescription() == null) return false;
        return e.getDescription().toLowerCase().contains(interest.toLowerCase());
    }

    /**
     * Checks if event is within user availability.
     * Used for US 01.01.04.
     */
    private boolean matchesAvailability(Event e, Timestamp start, Timestamp end) {
        return e.getEventStartDate().compareTo(start) >= 0 &&
                e.getEventEndDate().compareTo(end) <= 0;
    }

    // --------------------------------------------------------------
    // US 01.01.03 TEST
    // --------------------------------------------------------------

    /**
     * US 01.01.03:
     * As an entrant, I want to see a list of events that I can join the waiting list for.
     *
     * Validates that only:
     *  - events with open registration
     *  - events that are full
     *  - events that have not already held a draw
     * appear in the joinable list.
     */
    @Test
    public void testJoinableEventsAreCorrectlyReturned() {

        Timestamp now = Timestamp.now();
        Timestamp past = new Timestamp(now.getSeconds() - 50000, 0);
        Timestamp future = new Timestamp(now.getSeconds() + 50000, 0);

        // Event: open registration, full, not drawn → joinable
        Event openFull = new Event();
        openFull.setName("Open Full Event");
        openFull.setRegistrationStartDate(past);
        openFull.setRegistrationEndDate(future);
        openFull.setMaxEntrants(10);
        openFull.setCurrentEntrants(10);
        openFull.setAlreadyDrew(false);

        // Event: open registration but NOT full → cannot join waitlist
        Event openNotFull = new Event();
        openNotFull.setName("Open Not Full");
        openNotFull.setRegistrationStartDate(past);
        openNotFull.setRegistrationEndDate(future);
        openNotFull.setMaxEntrants(10);
        openNotFull.setCurrentEntrants(5);
        openNotFull.setAlreadyDrew(false);

        // Event: registration closed → cannot join
        Event closed = new Event();
        closed.setName("Closed");
        closed.setRegistrationStartDate(past);
        closed.setRegistrationEndDate(past);
        closed.setMaxEntrants(10);
        closed.setCurrentEntrants(10);
        closed.setAlreadyDrew(false);

        List<Event> events = Arrays.asList(openFull, openNotFull, closed);
        List<Event> joinable = new ArrayList<>();

        for (Event e : events) {
            if (isJoinable(e, now)) joinable.add(e);
        }

        assertEquals(1, joinable.size());
        assertEquals("Open Full Event", joinable.get(0).getName());
    }

    // --------------------------------------------------------------
    // US 01.01.04 TEST
    // --------------------------------------------------------------

    /**
     * US 01.01.04:
     * As an entrant, I want to filter events based on my interests and availability.
     *
     * Validates that:
     *  - description contains user's interest keyword
     *  - event occurs entirely within user's free time window
     */
    @Test
    public void testFilterEventsByInterestAndAvailability() {

        String interest = "music";

        Timestamp start = Timestamp.now();
        Timestamp end = new Timestamp(start.getSeconds() + 100000, 0);

        // Event that matches interest & availability
        Event e1 = new Event();
        e1.setName("Jazz Festival");
        e1.setDescription("A fun outdoor music event");
        e1.setEventStartDate(start);
        e1.setEventEndDate(end);

        // Event that fails interest filter
        Event e2 = new Event();
        e2.setName("Tech Expo");
        e2.setDescription("A showcase of innovation");
        e2.setEventStartDate(start);
        e2.setEventEndDate(end);

        // Event that fails availability filter
        Event e3 = new Event();
        e3.setName("Late Night Concert");
        e3.setDescription("Live music late show");
        e3.setEventStartDate(new Timestamp(end.getSeconds() + 50000, 0));
        e3.setEventEndDate(new Timestamp(end.getSeconds() + 80000, 0));

        List<Event> allEvents = Arrays.asList(e1, e2, e3);
        List<Event> filtered = new ArrayList<>();

        for (Event e : allEvents) {
            if (matchesInterest(e, interest) &&
                    matchesAvailability(e, start, end)) {

                filtered.add(e);
            }
        }

        assertEquals(1, filtered.size());
        assertEquals("Jazz Festival", filtered.get(0).getName());
    }
}
