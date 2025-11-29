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

public class EditEventController {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());

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


    public String validateName(String name) {
        return (name == null || name.isEmpty()) ? "Name cannot be empty" : null;
    }

    public String validateDescription(String description) {
        return (description == null || description.isEmpty()) ? "Description cannot be empty" : null;
    }

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
