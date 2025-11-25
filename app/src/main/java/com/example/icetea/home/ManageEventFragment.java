package com.example.icetea.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.icetea.R;
import com.example.icetea.auth.SignUpFragment;
import com.example.icetea.models.Event;
import com.example.icetea.util.Callback;
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
        // Inflate the layout for this fragment
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

        MaterialButton drawWinners = view.findViewById(R.id.buttonDrawWinners);

        controller.getEventObject(eventId, new Callback<Event>() {
            @Override
            public void onSuccess(Event result) {
                event = result;
                if (event.getAlreadyDrew()){
                    drawWinners.setText("View Final Entrants");
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Error loading event", Toast.LENGTH_SHORT).show();
            }
        });

        MaterialButton editEventButton = view.findViewById(R.id.buttonManageEventEditEvent);
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
}