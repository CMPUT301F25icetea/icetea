package com.example.icetea.home;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.icetea.R;
import com.example.icetea.models.Event;
import com.example.icetea.models.EventDB;
import com.example.icetea.util.Callback;
import com.example.icetea.util.ImageUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class EditEventFragment extends Fragment {

    private static final String ARG_EVENT_ID = "eventId";
    private String eventId;
    private EditEventController controller;
    private ImageView eventPosterImageView;
    private Uri newPosterUri = null;
    private Event currentEvent;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());

    private final ActivityResultLauncher<String> pickPosterLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    eventPosterImageView.setImageURI(uri);
                    newPosterUri = uri;
                }
            });


    public EditEventFragment() {
        // Required empty public constructor
    }

    public static EditEventFragment newInstance(String eventId) {
        EditEventFragment fragment = new EditEventFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller = new EditEventController();


        ImageButton backButton = view.findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> {
                    requireActivity().getSupportFragmentManager().popBackStack();
        });

        String eventId = requireArguments().getString("eventId");
        if (eventId == null) {
            Toast.makeText(getContext(), "Missing eventId", Toast.LENGTH_SHORT).show();
            return;
        }

        TextInputLayout inputLayoutEventName = view.findViewById(R.id.inputLayoutEventName);
        TextInputLayout inputLayoutEventDescription = view.findViewById(R.id.inputLayoutEventDescription);
        TextInputLayout inputLayoutEventCriteria = view.findViewById(R.id.inputLayoutEventCriteria);
        TextInputLayout inputLayoutRegStart = view.findViewById(R.id.inputLayoutRegStart);
        TextInputLayout inputLayoutRegEnd = view.findViewById(R.id.inputLayoutRegEnd);
        TextInputLayout inputLayoutEventStart = view.findViewById(R.id.inputLayoutEventStart);
        TextInputLayout inputLayoutEventEnd = view.findViewById(R.id.inputLayoutEventEnd);
        TextInputLayout inputLayoutEventLocation = view.findViewById(R.id.inputLayoutEventLocation);
        TextInputLayout inputLayoutMaxEntrants = view.findViewById(R.id.inputLayoutMaxEntrants);

        TextInputEditText editName = view.findViewById(R.id.editTextEventName);
        TextInputEditText editDesc = view.findViewById(R.id.editTextEventDescription);
        TextInputEditText editCriteria = view.findViewById(R.id.editTextEventCriteria);
        TextInputEditText editRegStart = view.findViewById(R.id.editTextRegStart);
        TextInputEditText editRegEnd = view.findViewById(R.id.editTextRegEnd);
        TextInputEditText editEventStart = view.findViewById(R.id.editTextEventStart);
        TextInputEditText editEventEnd = view.findViewById(R.id.editTextEventEnd);
        TextInputEditText editLocation = view.findViewById(R.id.editTextEventLocation);
        TextInputEditText editMaxEntrants = view.findViewById(R.id.editTextMaxEntrants);

        SwitchMaterial switchGeo = view.findViewById(R.id.switchGeolocation);

        MaterialButton updateButton = view.findViewById(R.id.buttonUpdateEvent);

        eventPosterImageView = view.findViewById(R.id.imageViewEventPoster);
        MaterialCardView cardPoster = view.findViewById(R.id.cardEventPoster);
        cardPoster.setOnClickListener(v -> pickPosterLauncher.launch("image/*"));

        EventDB.getInstance().getEvent(eventId, task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                Toast.makeText(getContext(), "Failed to load event", Toast.LENGTH_SHORT).show();
                return;
            }

            DocumentSnapshot doc = task.getResult();
            currentEvent = doc.toObject(Event.class);

            if (currentEvent == null) {
                Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
                return;
            }

            editName.setText(currentEvent.getName());
            editDesc.setText(currentEvent.getDescription());
            editCriteria.setText(currentEvent.getCriteria());

            if (currentEvent.getPosterBase64() != null) {
                eventPosterImageView.setImageBitmap(
                        ImageUtil.base64ToBitmap(currentEvent.getPosterBase64())
                );
            }

            if (currentEvent.getRegistrationStartDate() != null)
                editRegStart.setText(sdf.format(currentEvent.getRegistrationStartDate().toDate()));

            if (currentEvent.getRegistrationEndDate() != null)
                editRegEnd.setText(sdf.format(currentEvent.getRegistrationEndDate().toDate()));

            if (currentEvent.getEventStartDate() != null)
                editEventStart.setText(sdf.format(currentEvent.getEventStartDate().toDate()));

            if (currentEvent.getEventEndDate() != null)
                editEventEnd.setText(sdf.format(currentEvent.getEventEndDate().toDate()));

            editLocation.setText(currentEvent.getLocation());

            if (currentEvent.getMaxEntrants() != null)
                editMaxEntrants.setText(String.valueOf(currentEvent.getMaxEntrants()));

            switchGeo.setChecked(currentEvent.getGeolocationRequirement());
        });


        editRegStart.setOnClickListener(v -> showDateTimePicker(editRegStart));
        editRegEnd.setOnClickListener(v -> showDateTimePicker(editRegEnd));
        editEventStart.setOnClickListener(v -> showDateTimePicker(editEventStart));
        editEventEnd.setOnClickListener(v -> showDateTimePicker(editEventEnd));

        updateButton.setOnClickListener(v -> {
            inputLayoutEventName.setError(null);
            inputLayoutEventDescription.setError(null);
            inputLayoutRegStart.setError(null);
            inputLayoutRegEnd.setError(null);
            inputLayoutEventStart.setError(null);
            inputLayoutEventEnd.setError(null);
            inputLayoutMaxEntrants.setError(null);

            String name = editName.getText() != null ? editName.getText().toString().trim() : "";
            String desc = editDesc.getText() != null ? editDesc.getText().toString().trim() : "";
            String criteria = editCriteria.getText() != null ? editCriteria.getText().toString().trim() : "";
            String regStart = editRegStart.getText() != null ? editRegStart.getText().toString().trim() : "";
            String regEnd = editRegEnd.getText() != null ? editRegEnd.getText().toString().trim() : "";
            String eventStart = editEventStart.getText() != null ? editEventStart.getText().toString().trim() : "";
            String eventEnd = editEventEnd.getText() != null ? editEventEnd.getText().toString().trim() : "";
            String location = editLocation.getText() != null ? editLocation.getText().toString().trim() : "";
            String maxEnt = editMaxEntrants.getText() != null ? editMaxEntrants.getText().toString().trim() : "";
            boolean geo = switchGeo.isChecked();

            boolean error = false;

            String nameError = controller.validateName(name);
            if (nameError != null) { inputLayoutEventName.setError(nameError); error = true; }

            String descError = controller.validateDescription(desc);
            if (descError != null) { inputLayoutEventDescription.setError(descError); error = true; }

            String regOpenError = controller.validateRegOpen(regStart, regEnd, eventStart, eventEnd);
            if (regOpenError != null) { inputLayoutRegStart.setError(regOpenError); error = true; }

            String regCloseError = controller.validateRegClose(regStart, regEnd, eventStart, eventEnd);
            if (regCloseError != null) { inputLayoutRegEnd.setError(regCloseError); error = true; }

            String eventStartError = controller.validateEventStart(regStart, regEnd, eventStart, eventEnd);
            if (eventStartError != null) { inputLayoutEventStart.setError(eventStartError); error = true; }

            String eventEndError = controller.validateEventEnd(regStart, regEnd, eventStart, eventEnd);
            if (eventEndError != null) { inputLayoutEventEnd.setError(eventEndError); error = true; }

            String maxEntrantsError = controller.validateMaxEntrants(maxEnt);
            if (maxEntrantsError != null) { inputLayoutMaxEntrants.setError(maxEntrantsError); error = true; }

            String posterBase64 = null;
            if (newPosterUri != null) {
                try {
                    posterBase64 = ImageUtil.uriToBase64(requireContext(), newPosterUri);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Failed loading image", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (error) return;

            controller.updateEvent(
                    currentEvent,
                    name, desc, criteria, posterBase64,
                    regStart, regEnd, eventStart, eventEnd, location, maxEnt, geo,
                    new Callback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            Toast.makeText(getContext(), "Event updated", Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(getContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        });
    }

    private void showDateTimePicker(TextInputEditText target) {
        final Calendar cal = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(requireContext(),
                (dpView, y, m, d) -> {
                    cal.set(y, m, d);

                    TimePickerDialog timePicker = new TimePickerDialog(requireContext(),
                            (tpView, h, min) -> {
                                cal.set(Calendar.HOUR_OF_DAY, h);
                                cal.set(Calendar.MINUTE, min);
                                target.setText(sdf.format(cal.getTime()));
                            },
                            cal.get(Calendar.HOUR_OF_DAY),
                            cal.get(Calendar.MINUTE),
                            true
                    );
                    timePicker.show();
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        );
        datePicker.show();
    }
}