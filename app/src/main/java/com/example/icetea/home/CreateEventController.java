package com.example.icetea.home;

import android.util.Log;

import com.example.icetea.auth.CurrentUser;
import com.example.icetea.event.Event;
import com.example.icetea.event.EventDB;
import com.example.icetea.util.Callback;
import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;

public class CreateEventController {
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());
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
        newEvent.setEventName(eventName);
        newEvent.setDescription(eventDescription);
        newEvent.setCriteria(eventCriteria);
        newEvent.setPosterBase64(posterBase64);
        newEvent.setRegistrationStartDate(regStartTs);
        newEvent.setRegistrationEndDate(regEndTs);
        newEvent.setEventStartDate(eventStartTs);
        newEvent.setEventEndDate(eventEndTs);
        newEvent.setLocation(eventLocation);
        newEvent.setMaxEntrants(maxEntrantsInt);
        newEvent.setGeolocationRequirement(geolocationRequired);
        Log.d("tag", "ran");
        EventDB.getInstance().createEvent(newEvent, task -> {
            if (task.isSuccessful()) {
                Log.d("tag", "ran2");
                callback.onSuccess(null);
            } else {
                Exception e = task.getException() != null
                        ? task.getException()
                        : new Exception("Failed to create event");
                callback.onFailure(e);
            }
        });
    }

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
    public String validateName(String name) {
        if (name == null || name.isEmpty()) {
            return "Name cannot be empty";
        }
        return null;
    }

    public String validateDescription(String description) {
        if (description == null || description.isEmpty()) {
            return "Description cannot be empty";
        }
        return null;
    }

    //TODO: validate image size (also for profile)

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
