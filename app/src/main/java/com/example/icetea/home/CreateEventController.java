package com.example.icetea.home;

import android.util.Log;

import com.example.icetea.auth.CurrentUser;
import com.example.icetea.models.Event;
import com.example.icetea.models.EventDB;
import com.example.icetea.util.Callback;
import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;

/**
 * Controller responsible for creating new events and validating
 * event input fields such as dates, names, descriptions, and limits.
 * <p>
 * This class performs input validation, converts text dates into
 * {@link Timestamp} objects, and sends the finalized {@link Event}
 * object to Firestore via {@link EventDB}.
 */
public class CreateEventController {

    /** Formatter for parsing date/time strings provided by the user. */
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());

    /**
     * Creates an event using the provided fields, validates conversions,
     * and sends the event to Firestore.
     *
     * @param eventName           The name of the event.
     * @param eventDescription    The description of the event.
     * @param eventCriteria       The criteria for selecting entrants.
     * @param posterBase64        The Base64-encoded event poster image.
     * @param regStart            Registration open date (text).
     * @param regEnd              Registration close date (text).
     * @param eventStart          Event start date (text).
     * @param eventEnd            Event end date (text).
     * @param eventLocation       The location of the event.
     * @param maxEntrants         Maximum number of entrants allowed.
     * @param geolocationRequired Whether entrants must provide location.
     * @param callback            Callback for success or failure.
     */
    public void createEvent(String eventName, String eventDescription, String eventCriteria,
                            String posterBase64, String regStart, String regEnd,
                            String eventStart, String eventEnd, String eventLocation,
                            String maxEntrants, boolean geolocationRequired, Callback<Void> callback) {

        Timestamp regStartTs = textToTimestamp(regStart);
        Timestamp regEndTs = textToTimestamp(regEnd);
        Timestamp eventStartTs = textToTimestamp(eventStart);
        Timestamp eventEndTs = textToTimestamp(eventEnd);

        if (regEndTs == null) {
            callback.onFailure(new Exception("Invalid registration close date format"));
            return;
        }

        if (eventStartTs == null) {
            callback.onFailure(new Exception("Invalid event start date format"));
            return;
        }

        Integer maxEntrantsInt = null;
        if (maxEntrants != null && !maxEntrants.trim().isEmpty()) {
            try {
                int value = Integer.parseInt(maxEntrants.trim());
                if (value > 0) {
                    maxEntrantsInt = value;
                }
            } catch (NumberFormatException ignored) {}
        }

        Event newEvent = new Event();
        newEvent.setOrganizerId(CurrentUser.getInstance().getFid());
        newEvent.setName(eventName);
        newEvent.setDescription(eventDescription);
        newEvent.setCriteria(eventCriteria);
        newEvent.setPosterBase64(posterBase64);
        newEvent.setRegistrationStartDate(regStartTs);
        newEvent.setRegistrationEndDate(regEndTs);
        newEvent.setEventStartDate(eventStartTs);
        newEvent.setEventEndDate(eventEndTs);
        newEvent.setLocation(eventLocation);
        newEvent.setMaxEntrants(maxEntrantsInt);
        newEvent.setCurrentEntrants(0);
        newEvent.setGeolocationRequirement(geolocationRequired);
        newEvent.setAlreadyDrew(false);

        EventDB.getInstance().createEvent(newEvent, task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(null);
            } else {
                Exception e = task.getException() != null
                        ? task.getException()
                        : new Exception("Failed to create event");
                callback.onFailure(e);
            }
        });
    }

    /**
     * Converts a user-provided date string into a Firestore {@link Timestamp}.
     *
     * @param text The date string to parse.
     * @return The parsed {@link Timestamp}, or {@code null} if parsing failed.
     */
    public Timestamp textToTimestamp(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        try {
            Date date = sdf.parse(text);
            if (date == null) return null;
            return new Timestamp(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Validates an event name.
     *
     * @param name The event name.
     * @return Error message if invalid, otherwise {@code null}.
     */
    public String validateName(String name) {
        if (name == null || name.isEmpty()) {
            return "Name cannot be empty";
        }
        return null;
    }

    /**
     * Validates an event description.
     *
     * @param description The event description.
     * @return Error message if invalid, otherwise {@code null}.
     */
    public String validateDescription(String description) {
        if (description == null || description.isEmpty()) {
            return "Description cannot be empty";
        }
        return null;
    }

    /**
     * Validates the registration opening date against other date constraints.
     *
     * @param regOpen    Registration open date.
     * @param regClose   Registration close date.
     * @param eventStart Event start date.
     * @param eventEnd   Event end date.
     * @return Error message if invalid, otherwise {@code null}.
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
     * Validates the registration closing date against other event dates.
     *
     * @param regOpen    Registration open date.
     * @param regClose   Registration close date.
     * @param eventStart Event start date.
     * @param eventEnd   Event end date.
     * @return Error message if invalid, otherwise {@code null}.
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
     * Validates the event start date against other important dates.
     *
     * @param regOpen    Registration open date.
     * @param regClose   Registration close date.
     * @param eventStart Event start date.
     * @param eventEnd   Event end date.
     * @return Error message if invalid, otherwise {@code null}.
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
     * Validates the event end date against registration dates and event start.
     *
     * @param regOpen    Registration open date.
     * @param regClose   Registration close date.
     * @param eventStart Event start date.
     * @param eventEnd   Event end date.
     * @return Error message if invalid, otherwise {@code null}.
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
     * Validates the "max entrants" input.
     *
     * @param maxEntrants Text input for maximum entrants.
     * @return Error message if invalid, otherwise {@code null}.
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