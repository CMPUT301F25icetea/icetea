package com.example.icetea.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.icetea.R;
import com.example.icetea.models.Event;
import com.example.icetea.models.EventDB;
import com.example.icetea.util.Callback;
import com.example.icetea.util.ImageUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ManageEventFragment extends Fragment {

    private ManageEventController controller;
    private static final String ARG_EVENT_ID = "eventId";
    private Event event;
    private String eventId;
    private ImageView posterImageView;
    private TextView eventNameTextView;


    public ManageEventFragment() {
        // Required empty public constructor
    }

    public static ManageEventFragment newInstance(String eventId) {
        ManageEventFragment fragment = new ManageEventFragment();
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
        return inflater.inflate(R.layout.fragment_manage_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = new ManageEventController();

        ImageButton backButton = view.findViewById(R.id.buttonBackManageEvent);
        backButton.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        posterImageView = view.findViewById(R.id.imageManageEventPoster);
        eventNameTextView = view.findViewById(R.id.textManageEventName);

        MaterialButton drawWinners = view.findViewById(R.id.buttonDrawWinners);
        MaterialButton viewWaitingListButton = view.findViewById(R.id.buttonViewWaitingList);

        //jimmy edit from here
        MaterialButton viewQrCodeButton = view.findViewById(R.id.buttonViewQrCode);
        viewQrCodeButton.setOnClickListener(v -> {
            if (eventId == null) {
                Toast.makeText(getContext(), "Error: event not loaded yet", Toast.LENGTH_SHORT).show();
                return;
            }

            ViewQRcodeFragment qrFragment = ViewQRcodeFragment.newInstance(eventId);

            FragmentManager fm = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.setReorderingAllowed(true);
            transaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
            );
            transaction.replace(R.id.main_fragment_container, qrFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        //to here

        MaterialButton editEventButton = view.findViewById(R.id.buttonEditEvent);
        editEventButton.setOnClickListener(v -> {
            FragmentManager fm = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.setReorderingAllowed(true);
            transaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
            );
            transaction.replace(R.id.main_fragment_container, EditEventFragment.newInstance(eventId));
            transaction.addToBackStack(null);
            transaction.commit();
        });

        controller.getEventObject(eventId, new Callback<Event>() {
            @Override
            public void onSuccess(Event result) {
                event = result;

                if (eventNameTextView != null && event.getName() != null) {
                    eventNameTextView.setText(event.getName());
                }

                if (event.getAlreadyDrew()) {
                    drawWinners.setText("View Final Entrants");
                }

                String posterBase64 = event.getPosterBase64();
                if (posterBase64 != null && !posterBase64.isEmpty()) {
                    posterImageView.setImageBitmap(
                            ImageUtil.base64ToBitmap(posterBase64)
                    );
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Error loading event", Toast.LENGTH_SHORT).show();
            }
        });

        viewWaitingListButton.setOnClickListener(v -> {
            FragmentManager fm = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.setReorderingAllowed(true);
            transaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
            );
            transaction.replace(R.id.main_fragment_container, WaitlistFragment.newInstance(eventId));
            transaction.addToBackStack(null);
            transaction.commit();
        });

        drawWinners.setOnClickListener(v -> {
            if (event != null && !event.getAlreadyDrew()) {
                View dialogView = LayoutInflater.from(getContext())
                        .inflate(R.layout.dialog_draw_winners, null);

                TextInputLayout inputLayout = dialogView.findViewById(R.id.inputLayoutDrawWinnersDialog);
                TextInputEditText inputNumber = dialogView.findViewById(R.id.inputNumberWinners);

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Draw Winners")
                        .setMessage("How many winners would you like to draw?\nCurrent amount of entrants: " + event.getCurrentEntrants())
                        .setView(dialogView)
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Draw", null);

                AlertDialog dialog = builder.show();

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
                    String text = inputNumber.getText() != null ? inputNumber.getText().toString() : "";

                    if (text.isEmpty()) {
                        inputLayout.setError("Enter a number");
                        return;
                    }

                    int count;
                    try {
                        count = Integer.parseInt(text);
                    } catch (NumberFormatException e) {
                        inputLayout.setError("Number is invalid");
                        return;
                    }

                    if (count <= 0) {
                        inputLayout.setError("Number must be greater than 0");
                        return;
                    }

                    inputLayout.setError(null);

                    controller.drawWinners(event, count, new Callback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                            event.setAlreadyDrew(true);
                            drawWinners.setText("View Final Entrants");
                            dialog.dismiss();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            inputLayout.setError(e.getMessage());
                        }
                    });
                });

            } else if (event != null && event.getAlreadyDrew()) {
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.setReorderingAllowed(true);
                transaction.setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                );
                transaction.replace(R.id.main_fragment_container, FinalEntrantsFragment.newInstance(eventId));
                transaction.addToBackStack(null);
                transaction.commit();
            } else {
                Toast.makeText(getContext(), "Please refresh, error finding event", Toast.LENGTH_SHORT).show();
            }

        });

        MaterialButton deleteEventButton = view.findViewById(R.id.buttonDeleteEvent);
        deleteEventButton.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Event")
                    .setMessage("Are you sure you want to delete this event? This action cannot be undone.")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Delete", (dialog, which) -> {
                        EventDB.getInstance().deleteEvent(eventId, task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(requireContext(), "Event deleted", Toast.LENGTH_SHORT).show();
                                requireActivity().getSupportFragmentManager().popBackStack();
                            } else {
                                Toast.makeText(requireContext(), "Error deleting event: " + (task.getException() != null ? task.getException().getMessage() : ""), Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .show();
        });
    }
}
