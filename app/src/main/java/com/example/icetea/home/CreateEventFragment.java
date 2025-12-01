package com.example.icetea.home;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import com.example.icetea.util.Callback;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.example.icetea.util.ImageUtil;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Fragment responsible for creating a new event. This screen allows the user to
 * input event metadata, select date/time values, choose a poster image, and
 * submit the event to the backend via {@link CreateEventController}.
 *
 * <p>The fragment validates all fields before submitting and displays input
 * errors using {@link TextInputLayout}. A poster image can optionally be
 * selected and will be converted to Base64 before upload.</p>
 */
public class CreateEventFragment extends Fragment {

    /** Controller that handles input validation and event creation logic. */
    private CreateEventController controller;

    /** ImageView that displays the event poster preview. */
    private ImageView eventPosterImageView;

    /** URI of the newly selected event poster image, if any. */
    private Uri newPosterUri = null;

    /**
     * Launcher used for selecting an image from the user's gallery.
     * Utilizes {@link ActivityResultContracts.GetContent} and returns a content URI.
     */
    private final ActivityResultLauncher<String> pickPosterLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    eventPosterImageView.setImageURI(uri);
                    newPosterUri = uri;
                }
            });

    /**
     * Required empty public constructor.
     */
    public CreateEventFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment.
     *
     * @return a new {@link CreateEventFragment} instance
     */
    public static CreateEventFragment newInstance() {
        return new CreateEventFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Inflates the fragment layout.
     *
     * @param inflater  LayoutInflater used to inflate the view
     * @param container Parent view container
     * @param savedInstanceState Saved state bundle
     * @return The inflated view hierarchy for this fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_event, container, false);
    }

    /**
     * Initializes UI elements, sets listeners, and prepares validation + creation logic.
     *
     * @param view The root view returned from {@link #onCreateView}
     * @param savedInstanceState Saved state bundle
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = new CreateEventController();

        ImageButton backButton = view.findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        MaterialButton createButton = view.findViewById(R.id.buttonCreateEvent);

        eventPosterImageView = view.findViewById(R.id.imageViewEventPoster);
        MaterialCardView cardEventPoster = view.findViewById(R.id.cardEventPoster);

        cardEventPoster.setOnClickListener(v -> pickPosterLauncher.launch("image/*"));

        // TextInputLayouts for displaying field errors
        TextInputLayout inputLayoutEventName = view.findViewById(R.id.inputLayoutEventName);
        TextInputLayout inputLayoutEventDescription = view.findViewById(R.id.inputLayoutEventDescription);
        TextInputLayout inputLayoutEventCriteria = view.findViewById(R.id.inputLayoutEventCriteria);
        TextInputLayout inputLayoutRegStart = view.findViewById(R.id.inputLayoutRegStart);
        TextInputLayout inputLayoutRegEnd = view.findViewById(R.id.inputLayoutRegEnd);
        TextInputLayout inputLayoutEventStart = view.findViewById(R.id.inputLayoutEventStart);
        TextInputLayout inputLayoutEventEnd = view.findViewById(R.id.inputLayoutEventEnd);
        TextInputLayout inputLayoutEventLocation = view.findViewById(R.id.inputLayoutEventLocation);
        TextInputLayout inputLayoutMaxEntrants = view.findViewById(R.id.inputLayoutMaxEntrants);

        // Input fields
        TextInputEditText editTextEventName = view.findViewById(R.id.editTextEventName);
        TextInputEditText editTextEventDescription = view.findViewById(R.id.editTextEventDescription);
        TextInputEditText editTextEventCriteria = view.findViewById(R.id.editTextEventCriteria);
        TextInputEditText editTextRegStart = view.findViewById(R.id.editTextRegStart);
        TextInputEditText editTextRegEnd = view.findViewById(R.id.editTextRegEnd);
        TextInputEditText editTextEventStart = view.findViewById(R.id.editTextEventStart);
        TextInputEditText editTextEventEnd = view.findViewById(R.id.editTextEventEnd);
        TextInputEditText editTextEventLocation = view.findViewById(R.id.editTextEventLocation);
        TextInputEditText editTextMaxEntrants = view.findViewById(R.id.editTextMaxEntrants);

        SwitchMaterial switchGeolocation = view.findViewById(R.id.switchGeolocation);

        // Attach date-time pickers
        editTextRegStart.setOnClickListener(v -> showDateTimePicker(editTextRegStart));
        editTextRegEnd.setOnClickListener(v -> showDateTimePicker(editTextRegEnd));
        editTextEventStart.setOnClickListener(v -> showDateTimePicker(editTextEventStart));
        editTextEventEnd.setOnClickListener(v -> showDateTimePicker(editTextEventEnd));

        /**
         * Create button handler — validates inputs, converts poster to Base64 if needed,
         * and invokes {@link CreateEventController#createEvent}.
         */
        createButton.setOnClickListener(v -> {
            // Clear previous errors
            inputLayoutEventName.setError(null);
            inputLayoutEventDescription.setError(null);
            inputLayoutRegStart.setError(null);
            inputLayoutRegEnd.setError(null);
            inputLayoutEventStart.setError(null);
            inputLayoutEventEnd.setError(null);
            inputLayoutMaxEntrants.setError(null);

            // Extract inputs safely
            String eventName = editTextEventName.getText() != null ? editTextEventName.getText().toString().trim() : "";
            String eventDescription = editTextEventDescription.getText() != null ? editTextEventDescription.getText().toString().trim() : "";
            String eventCriteria = editTextEventCriteria.getText() != null ? editTextEventCriteria.getText().toString().trim() : "";
            String regStartText = editTextRegStart.getText() != null ? editTextRegStart.getText().toString().trim() : "";
            String regEndText = editTextRegEnd.getText() != null ? editTextRegEnd.getText().toString().trim() : "";
            String eventStartText = editTextEventStart.getText() != null ? editTextEventStart.getText().toString().trim() : "";
            String eventEndText = editTextEventEnd.getText() != null ? editTextEventEnd.getText().toString().trim() : "";
            String location = editTextEventLocation.getText() != null ? editTextEventLocation.getText().toString().trim() : "";
            String maxEntrantsText = editTextMaxEntrants.getText() != null ? editTextMaxEntrants.getText().toString().trim() : "";
            boolean geolocationRequired = switchGeolocation.isChecked();

            boolean hasError = false;

            // Validate inputs through controller
            String eventNameError = controller.validateName(eventName);
            if (eventNameError != null) {
                inputLayoutEventName.setError(eventNameError);
                hasError = true;
            }

            String eventDescriptionError = controller.validateDescription(eventDescription);
            if (eventDescriptionError != null) {
                inputLayoutEventDescription.setError(eventDescriptionError);
                hasError = true;
            }

            String regOpenError = controller.validateRegOpen(regStartText, regEndText, eventStartText, eventEndText);
            if (regOpenError != null) {
                inputLayoutRegStart.setError(regOpenError);
                hasError = true;
            }

            String regCloseError = controller.validateRegClose(regStartText, regEndText, eventStartText, eventEndText);
            if (regCloseError != null) {
                inputLayoutRegEnd.setError(regCloseError);
                hasError = true;
            }

            String eventStartError = controller.validateEventStart(regStartText, regEndText, eventStartText, eventEndText);
            if (eventStartError != null) {
                inputLayoutEventStart.setError(eventStartError);
                hasError = true;
            }

            String eventEndError = controller.validateEventEnd(regStartText, regEndText, eventStartText, eventEndText);
            if (eventEndError != null) {
                inputLayoutEventEnd.setError(eventEndError);
                hasError = true;
            }

            String maxEntrantsError = controller.validateMaxEntrants(maxEntrantsText);
            if (maxEntrantsError != null) {
                inputLayoutMaxEntrants.setError(maxEntrantsError);
                hasError = true;
            }

            // Convert poster to Base64 if selected
            String posterBase64 = null;
            if (newPosterUri != null) {
                try {
                    posterBase64 = ImageUtil.uriToBase64(requireContext(), newPosterUri);
                } catch (IOException e) {
                    Toast.makeText(getContext(), "Failed loading image", Toast.LENGTH_SHORT).show();
                    hasError = true;
                } catch (ImageUtil.ImageTooLargeException e) {
                    Toast.makeText(getContext(), "Image too large to upload", Toast.LENGTH_SHORT).show();
                    hasError = true;
                }
            }

            if (hasError) return;

            controller.createEvent(
                    eventName, eventDescription, eventCriteria, posterBase64,
                    regStartText, regEndText, eventStartText, eventEndText,
                    location, maxEntrantsText, geolocationRequired,
                    new Callback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            requireActivity().getSupportFragmentManager().popBackStack();
                            Toast.makeText(getContext(), "Event Created", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(getContext(), e.getMessage() + " Error creating event", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        });
    }

    /**
     * Displays a combined date and time picker dialog. After the user selects both
     * values, the result is formatted and placed into the provided {@link TextInputEditText}.
     *
     * @param targetEditText The text field into which the formatted date-time will be placed
     */
    private void showDateTimePicker(TextInputEditText targetEditText) {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog timePicker = new TimePickerDialog(requireContext(),
                            (timeView, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault());
                                targetEditText.setText(sdf.format(calendar.getTime()));
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true);
                    timePicker.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePicker.show();
    }
}