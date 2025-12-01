package com.example.icetea.home;

import com.example.icetea.models.Event;
import com.example.icetea.models.EventDB;
import com.example.icetea.util.Callback;
import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Controller responsible for validating and updating event data.
 * Provides date parsing helpers, field validation, and Firestore update logic
 * for editing an existing {@link Event}.
 */
public class EditEventController {

    /** Date formatter used for parsing user-entered date/time strings. */
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());

    /**
     * Converts a user-entered date/time string into a Firestore {@link Timestamp}.
     *
     * @param text The input string (e.g., "2025-01-20 05:00 PM").
     * @return Parsed {@link Timestamp}, or {@code null} if invalid or empty.
     */
    public Timestamp textToTimestamp(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        try {
            Date date = sdf.parse(text);
            return (date != null) ? new Timestamp(date) : null;
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Updates an existing event in Firestore. All fields provided are validated beforehand
     * via separate validation methods. Only non-null or non-empty values are written.
     *
     * @param event                The event object being updated.
     * @param name                 Event name.
     * @param description          Event description.
     * @param criteria             Entry criteria text.
     * @param posterBase64         Optional Base64-encoded poster image.
     * @param regStart             Registration start (string).
     * @param regEnd               Registration end (string).
     * @param eventStart           Event start date (string).
     * @param eventEnd             Event end date (string).
     * @param location             Event location.
     * @param maxEntrants          Max entrants allowed (string form).
     * @param geolocationRequired  Whether geolocation check-in is required.
     * @param callback             Callback invoked on success or failure.
     */
    public void updateEvent(
            Event event,
            String name,
            String description,
            String criteria,
            String posterBase64,
            String regStart,
            String regEnd,
            String eventStart,
            String eventEnd,
            String location,
            String maxEntrants,
            boolean geolocationRequired,
            Callback<Void> callback
    ) {
        Timestamp regStartTs = textToTimestamp(regStart);
        Timestamp regEndTs = textToTimestamp(regEnd);
        Timestamp eventStartTs = textToTimestamp(eventStart);
        Timestamp eventEndTs = textToTimestamp(eventEnd);

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("description", description);
        updates.put("criteria", criteria);
        if (posterBase64 != null) updates.put("posterBase64", posterBase64);
        updates.put("registrationStartDate", regStartTs);
        updates.put("registrationEndDate", regEndTs);
        updates.put("eventStartDate", eventStartTs);
        updates.put("eventEndDate", eventEndTs);
        updates.put("location", location);

        if (maxEntrants != null && !maxEntrants.trim().isEmpty()) {
            try {
                updates.put("maxEntrants", Integer.parseInt(maxEntrants.trim()));
            } catch (NumberFormatException ignored) {}
        }

        updates.put("geolocationRequirement", geolocationRequired);

        EventDB.getInstance().updateEvent(event.getEventId(), updates, task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(null);
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    /**
     * Validates that the event name is not empty.
     *
     * @param name The name to validate.
     * @return Error message, or {@code null} if valid.
     */
    public String validateName(String name) {
        return (name == null || name.isEmpty()) ? "Name cannot be empty" : null;
    }

    /**
     * Validates that the description is not empty.
     *
     * @param description Event description.
     * @return Error message, or {@code null} if valid.
     */
    public String validateDescription(String description) {
        return (description == null || description.isEmpty()) ? "Description cannot be empty" : null;
    }

    /**
     * Validates that the registration start date is valid and precedes other dates.
     *
     * @param regOpen     Registration start.
     * @param regClose    Registration end.
     * @param eventStart  Event start.
     * @param eventEnd    Event end.
     * @return Error message, or {@code null} if valid.
     */
    public String validateRegOpen(String regOpen, String regClose, String eventStart, String eventEnd) {
        if (regOpen == null || regOpen.isEmpty()) return null;

        try {
            Date regOpenDate = sdf.parse(regOpen);
            if (regOpenDate == null) return "Invalid date format for registration start";

            if (regClose != null && !regClose.isEmpty()) {
                Date regCloseDate = sdf.parse(regClose);
                if (regCloseDate == null) return "Invalid date format for registration close";
                if (!regOpenDate.before(regCloseDate)) {
                    return "Registration start must be before registration close";
                }
            }

            if (eventStart != null && !eventStart.isEmpty()) {
                Date eventStartDate = sdf.parse(eventStart);
                if (eventStartDate == null) return "Invalid date format for event start";
                if (!regOpenDate.before(eventStartDate)) {
                    return "Registration start must be before event start";
                }
            }

            if (eventEnd != null && !eventEnd.isEmpty()) {
                Date eventEndDate = sdf.parse(eventEnd);
                if (eventEndDate == null) return "Invalid date format for event end";
                if (!regOpenDate.before(eventEndDate)) {
                    return "Registration start must be before event end";
                }
            }

        } catch (ParseException e) {
            return "Invalid date format for registration start";
        }

        return null;
    }

    /**
     * Validates that the registration close date is valid and occurs after registration start
     * but before the event begins or ends.
     *
     * @param regOpen    Registration start.
     * @param regClose   Registration end.
     * @param eventStart Event start.
     * @param eventEnd   Event end.
     * @return Error message, or {@code null} if valid.
     */
    public String validateRegClose(String regOpen, String regClose, String eventStart, String eventEnd) {
        if (regClose == null || regClose.isEmpty()) {
            return "Registration close date must be set";
        }

        try {
            Date regCloseDate = sdf.parse(regClose);
            if (regCloseDate == null) return "Invalid date format for registration close";

            if (regOpen != null && !regOpen.isEmpty()) {
                Date regOpenDate = sdf.parse(regOpen);
                if (regOpenDate == null) return "Invalid date format for registration start";
                if (!regCloseDate.after(regOpenDate)) {
                    return "Registration close must be after registration start";
                }
            }

            if (eventStart != null && !eventStart.isEmpty()) {
                Date eventStartDate = sdf.parse(eventStart);
                if (eventStartDate == null) return "Invalid date format for event start";
                if (regCloseDate.after(eventStartDate)) {
                    return "Registration close must be before event start";
                }
            }

            if (eventEnd != null && !eventEnd.isEmpty()) {
                Date eventEndDate = sdf.parse(eventEnd);
                if (eventEndDate == null) return "Invalid date format for event end";
                if (regCloseDate.after(eventEndDate)) {
                    return "Registration close must be before event end";
                }
            }

        } catch (ParseException e) {
            return "Invalid date format for registration close";
        }

        return null;
    }

    /**
     * Validates that the event start date is set and falls after registration dates
     * but before the event end date.
     *
     * @param regOpen    Registration start.
     * @param regClose   Registration close.
     * @param eventStart Event start.
     * @param eventEnd   Event end.
     * @return Error message, or {@code null} if valid.
     */
    public String validateEventStart(String regOpen, String regClose, String eventStart, String eventEnd) {
        if (eventStart == null || eventStart.isEmpty()) {
            return "Event start date must be set";
        }

        try {
            Date eventStartDate = sdf.parse(eventStart);
            if (eventStartDate == null) return "Invalid date format for event start";

            if (regOpen != null && !regOpen.isEmpty()) {
                Date regOpenDate = sdf.parse(regOpen);
                if (regOpenDate != null && !eventStartDate.after(regOpenDate)) {
                    return "Event start must be after registration start";
                }
            }

            if (regClose != null && !regClose.isEmpty()) {
                Date regCloseDate = sdf.parse(regClose);
                if (regCloseDate != null && !eventStartDate.after(regCloseDate)) {
                    return "Event start must be after registration close";
                }
            }

            if (eventEnd != null && !eventEnd.isEmpty()) {
                Date eventEndDate = sdf.parse(eventEnd);
                if (eventEndDate != null && !eventStartDate.before(eventEndDate)) {
                    return "Event start must be before event end";
                }
            }

        } catch (ParseException e) {
            return "Invalid date format for event start";
        }

        return null;
    }

    /**
     * Validates that the event end date is valid and occurs after all other event/registration dates.
     *
     * @param regOpen    Registration start.
     * @param regClose   Registration close.
     * @param eventStart Event start.
     * @param eventEnd   Event end.
     * @return Error message, or {@code null} if valid.
     */
    public String validateEventEnd(String regOpen, String regClose, String eventStart, String eventEnd) {
        if (eventEnd == null || eventEnd.isEmpty()) return null;

        try {
            Date eventEndDate = sdf.parse(eventEnd);
            if (eventEndDate == null) return "Invalid date format for event end";

            if (regOpen != null && !regOpen.isEmpty()) {
                Date regOpenDate = sdf.parse(regOpen);
                if (regOpenDate != null && !eventEndDate.after(regOpenDate)) {
                    return "Event end must be after registration start";
                }
            }

            if (regClose != null && !regClose.isEmpty()) {
                Date regCloseDate = sdf.parse(regClose);
                if (regCloseDate != null && !eventEndDate.after(regCloseDate)) {
                    return "Event end must be after registration close";
                }
            }

            if (eventStart != null && !eventStart.isEmpty()) {
                Date eventStartDate = sdf.parse(eventStart);
                if (eventStartDate != null && !eventEndDate.after(eventStartDate)) {
                    return "Event end must be after event start";
                }
            }

        } catch (ParseException e) {
            return "Invalid date format for event end";
        }

        return null;
    }

    /**
     * Validates the max entrants field, ensuring the value is a positive integer.
     *
     * @param maxEntrants Input string representing the maximum number of entrants.
     * @return Error message, or {@code null} if valid.
     */
    public String validateMaxEntrants(String maxEntrants) {
        if (maxEntrants == null || maxEntrants.isEmpty()) return null;

        try {
            int value = Integer.parseInt(maxEntrants.trim());
            if (value <= 0) {
                return "Max entrants must be a positive number";
            }
        } catch (NumberFormatException e) {
            return "Max entrants must be a valid number";
        }

        return null;
    }
}