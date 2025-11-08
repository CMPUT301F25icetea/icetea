package com.example.icetea.event;

import com.example.icetea.auth.FBAuthenticator;
import com.example.icetea.util.Callback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Controller class for handling Event-related operations such as creation,
 * retrieval, update, and deletion.
 */
public class EventController {

    private final EventDB eventDB;

    /**
     * Constructs an EventController and initializes the EventDB instance.
     */
    public EventController() {
        eventDB = EventDB.getInstance();
    }

    /**
     * Saves a new event in the database.
     *
     * @param event The Event to create
     * @param callback Callback to handle success or failure
     */
    public void createEvent(Event event, Callback<Void> callback) {
        eventDB.saveEvent(event, task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(null);
            } else {
                callback.onFailure(new Exception(task.getException() != null ? task.getException().getMessage() : "Unknown error when creating event"));
            }
        });
    }

    /**
     * Retrieves an event by its ID.
     *
     * @param eventId The ID of the event
     * @param callback Callback to return the Event or an error
     */
    public void getEventById(String eventId, Callback<Event> callback) {
        eventDB.getEvent(eventId, task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc != null && doc.exists()) {
                    Event event = doc.toObject(Event.class);
                    if (event != null) event.setId(doc.getId());
                    callback.onSuccess(event);
                } else {
                    callback.onFailure(new Exception("Event not found"));
                }
            } else {
                callback.onFailure(new Exception(task.getException() != null ? task.getException().getMessage() : "Failed to get event"));
            }
        });
    }

    /**
     * Retrieves all events organized by a specific organizer.
     *
     * @param organizerId The ID of the organizer
     * @param callback Callback to return the list of events or an error
     */
    public void getEventsByOrganizerId(String organizerId, Callback<List<Event>> callback) {
        eventDB.getEventsByOrganizerId(organizerId, task -> {
            if (task.isSuccessful()) {
                List<Event> events = new ArrayList<>();
                for (DocumentSnapshot doc : task.getResult()) {
                    Event e = doc.toObject(Event.class);
                    if (e != null)  {
                        e.setId(doc.getId());
                        events.add(e);
                    }
                }
                callback.onSuccess(events);
            } else {
                callback.onFailure(new Exception("getEventsByOrganizerId : EventController failed"));
            }
        });
    }

    /**
     * Updates an existing event in the database.
     *
     * @param event The Event to update
     * @param callback Callback to handle success or failure
     */
    public void updateEvent(Event event, Callback<Void> callback) {
        eventDB.saveEvent(event, task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(null);
            } else {
                callback.onFailure(new Exception(task.getException() != null ? task.getException().getMessage() : "Unknown error when creating event"));
            }
        });
    }

    /**
     * Deletes an event from the database.
     *
     * @param event The Event to delete
     * @param callback Callback to handle success or failure
     */
    public void deleteEvent(Event event, Callback<Void> callback) {
        eventDB.deleteEvent(event, task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(null);
            } else {
                callback.onFailure(new Exception(task.getException() != null ? task.getException().getMessage() : "Unknown error deleting event")
                );
            }
        });
    }

    /**
     * Validates input fields for creating or updating an event.
     *
     * @param name Event name
     * @param description Event description
     * @param location Event location
     * @param startDate Event start date
     * @param endRegistration Registration end date
     * @return Error message if invalid, null if valid
     */
    public String validateInput(String name, String description, String location, Timestamp startDate, Timestamp endRegistration) {

        if (name == null || name.trim().isEmpty()) {
            return "Event name is required.";
        }
        if (description == null || description.trim().isEmpty()) {
            return "Event description is required.";
        }
        if (location == null || location.trim().isEmpty()) {
            return "Event location is required.";
        }
        if (startDate == null) {
            return "Event start date is required.";
        }
        if (endRegistration == null) {
            return "Registration end date is required.";
        }
        if (startDate.toDate().after(endRegistration.toDate())) {
            return "Event start date cannot be after registration end date.";
        }
        return null;
    }

    /**
     * Constructs an Event object from input strings.
     *
     * @param name Event name
     * @param description Event description
     * @param location Event location
     * @param capacityStr Event capacity as string
     * @param startDateStr Event start date as string (yyyy-MM-dd)
     * @param endDateStr Event end date as string (yyyy-MM-dd)
     * @param regStartStr Registration start date as string (yyyy-MM-dd)
     * @param regEndStr Registration end date as string (yyyy-MM-dd)
     * @return Event object
     * @throws IllegalArgumentException If any input is invalid
     */
    public Event createEventFromInput(String name,
                                      String description,
                                      String location,
                                      String capacityStr,
                                      String startDateStr,
                                      String endDateStr,
                                      String regStartStr,
                                      String regEndStr) throws IllegalArgumentException {

        Integer capacity = null;
        if (capacityStr != null && !capacityStr.isEmpty()) {
            try {
                capacity = Integer.parseInt(capacityStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Capacity must be a number");
            }
        }

        Timestamp startDate = parseDateToTimestamp(startDateStr);
        Timestamp endDate = parseDateToTimestamp(endDateStr);
        Timestamp regStart = parseDateToTimestamp(regStartStr);
        Timestamp regEnd = parseDateToTimestamp(regEndStr);

        //required fields, might remove location from this
        String validationError = validateInput(name, description, location, startDate, regEnd);
        if (validationError != null) {
            throw new IllegalArgumentException(validationError);
        }
        String organizerId = FBAuthenticator.getCurrentUserId();

        return new Event(
                null,
                organizerId,
                name,
                description,
                location,
                capacity,
                startDate,
                endDate,
                regStart,
                regEnd,
                null,
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    /**
     * Parses a date string in yyyy-MM-dd format to a Firebase Timestamp.
     *
     * @param dateStr Date string
     * @return Timestamp or null if parsing fails
     */
    private Timestamp parseDateToTimestamp(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
        try {
            Date date = dateFormat.parse(dateStr);
            assert date != null;
            return new Timestamp(date);
        } catch (ParseException e) {
            return null;
        }
    }
}
