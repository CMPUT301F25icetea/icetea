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
import android.widget.Toast;

import com.example.icetea.R;
import com.example.icetea.models.Event;
import com.example.icetea.util.Callback;
import com.example.icetea.util.ImageUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManageEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageEventFragment extends Fragment {

    private ManageEventController controller;
    private static final String ARG_EVENT_ID = "eventId";
    private Event event;
    private String eventId;

    private ImageView posterImageView;
    private MaterialButton changePosterButton;
    private boolean posterChanged = false;
    private String newPosterBase64 = null;

    private final ActivityResultLauncher<String> pickPosterLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    try {
                        String base64 = ImageUtil.uriToBase64(requireContext(), uri);

                        posterImageView.setImageURI(uri);
                        posterChanged = true;
                        newPosterBase64 = base64;

                        updateEventPoster();

                    } catch (ImageUtil.ImageTooLargeException e) {
                        Toast.makeText(getContext(),
                                "Image too large. Please select a smaller image.",
                                Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(),
                                "Failed to process image.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

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
        changePosterButton = view.findViewById(R.id.buttonChangeEventPoster);

        changePosterButton.setOnClickListener(v ->
                pickPosterLauncher.launch("image/*")
        );

        posterImageView.setOnClickListener(v ->
                pickPosterLauncher.launch("image/*")
        );

        MaterialButton drawWinners = view.findViewById(R.id.buttonDrawWinners);

        controller.getEventObject(eventId, new Callback<Event>() {
            @Override
            public void onSuccess(Event result) {
                event = result;

                if (event.getAlreadyDrew()) {
                    drawWinners.setText("View Final Entrants");
                }

                // Show correct poster
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

        MaterialButton viewWaitingListButton = view.findViewById(R.id.buttonViewWaitingList);
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

                    controller.drawWinners(eventId, count, new Callback<Void>() {
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
    }

    private void updateEventPoster() {
        if (!posterChanged || newPosterBase64 == null || eventId == null) {
            return;
        }

        controller.updateEventPoster(eventId, newPosterBase64, new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(getContext(), "Event poster updated", Toast.LENGTH_SHORT).show();
                posterChanged = false;

                if (event != null) {
                    event.setPosterBase64(newPosterBase64);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(),
                        "Failed to update poster: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
