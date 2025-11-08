package com.example.icetea.organizer;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.icetea.R;
import com.example.icetea.auth.FBAuthenticator;
import com.example.icetea.event.Event;
import com.example.icetea.event.EventController;
import com.example.icetea.util.Callback;
import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * Handles the organizer’s event creation form, including input validation,
 * date selection, and submission to the database.
 */
public class OrganizerCreateEventFragment extends Fragment {

    private EventController controller;
    private EditText inputName, inputDescription, inputStartDate, inputEndDate,
            inputRegistrationOpen, inputRegistrationEnd, inputCapacity, inputLocation;

    /**
     * defult empty constructor
     */
    public OrganizerCreateEventFragment() { }

    /**
     * Creates a new instance of {@code OrganizerCreateEventFragment}.
     *
     * @return a new instance of OrganizerCreateEventFragment
     */
    public static OrganizerCreateEventFragment newInstance() {
        return new OrganizerCreateEventFragment();
    }

    /**
     * get the layout for this fragment's UI.
     *
     * @param inflater  the LayoutInflater used to inflate the fragment's layout
     * @param container the parent view that the fragment’s UI should be attached to
     * @param savedInstanceState if non-null, this fragment is being re-created from a previous saved state
     * @return the root View for this fragment’s layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_organizer_create_event, container, false);
    }

    /**
     * Called after {@link #onCreateView}. Initializes the event creation form,
     * sets up date pickers, and handles the create button click to
     * trigger event creation through {@link EventController}.
     *
     * @param view the view returned by {@link #onCreateView}
     * @param savedInstanceState if non-null, this fragment is being re-created from a previous saved state
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = new EventController();

        inputName = view.findViewById(R.id.createEventName);
        inputDescription = view.findViewById(R.id.createEventDescription);
        inputLocation = view.findViewById(R.id.createEventLocation);
        inputCapacity = view.findViewById(R.id.createEventCapacity);
        inputStartDate = view.findViewById(R.id.createEventStartDate);
        inputEndDate = view.findViewById(R.id.createEventEndDate);
        inputRegistrationOpen = view.findViewById(R.id.createEventRegistrationStartDate);
        inputRegistrationEnd = view.findViewById(R.id.createEventRegistrationEndDate);

        Button buttonCreate = view.findViewById(R.id.buttonCreateEvent);

        inputStartDate.setOnClickListener(v -> {showDatePicker(inputStartDate);});
        inputEndDate.setOnClickListener(v -> {showDatePicker(inputEndDate);});
        inputRegistrationOpen.setOnClickListener(v -> {showDatePicker(inputRegistrationOpen);});
        inputRegistrationEnd.setOnClickListener(v -> {showDatePicker(inputRegistrationEnd);});

        buttonCreate.setOnClickListener(v -> createEvent());
    }

    private void showDatePicker(EditText field) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog picker = new DatePickerDialog(requireContext(),
                (view, year, month, day) -> {
                    calendar.set(year, month, day);
                    field.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        picker.getDatePicker().setMinDate(calendar.getTimeInMillis());
        picker.show();
    }


    private void createEvent() {

        Event newEvent;
        try {
            newEvent = controller.createEventFromInput(
                    inputName.getText().toString(),
                    inputDescription.getText().toString(),
                    inputLocation.getText().toString(),
                    inputCapacity.getText().toString(),
                    inputStartDate.getText().toString(),
                    inputEndDate.getText().toString(),
                    inputRegistrationOpen.getText().toString(),
                    inputRegistrationEnd.getText().toString()
            );

        } catch (IllegalArgumentException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        controller.createEvent(newEvent, new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(getContext(), "Created event!", Toast.LENGTH_SHORT).show();
                clearFields();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Something went wrong creating event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearFields() {
        inputName.setText("");
        inputDescription.setText("");
        inputStartDate.setText("");
        inputEndDate.setText("");
        inputRegistrationOpen.setText("");
        inputRegistrationEnd.setText("");
        inputCapacity.setText("");
        inputLocation.setText("");
    }
}
