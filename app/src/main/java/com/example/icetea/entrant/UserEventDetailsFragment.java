package com.example.icetea.entrant;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.icetea.R;
import com.example.icetea.auth.FBAuthenticator;
import com.example.icetea.models.Event;
import com.example.icetea.models.EventDB;
import com.google.firebase.firestore.DocumentSnapshot;

public class UserEventDetailsFragment extends Fragment {

    private static final String ARG_EVENT_ID = "event_id";

    private TextView tvTotalParticipants;
    private TextView tvWaitlistCount;
    private TextView tvLotteryProcess;
    private Button btnJoinWaitlist;
    private ProgressBar progressBar;

    private String eventId;
    private Event currentEvent;
    private boolean isInWaitlist = false;
    private EventDB eventDB;

    public UserEventDetailsFragment() {
        // Required empty public constructor
    }

    public static UserEventDetailsFragment newInstance(String eventId) {
        UserEventDetailsFragment fragment = new UserEventDetailsFragment();
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
        eventDB = EventDB.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_event_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        tvTotalParticipants = view.findViewById(R.id.tvTotalParticipants);
        tvWaitlistCount = view.findViewById(R.id.tvWaitlistCount);
        tvLotteryProcess = view.findViewById(R.id.tvLotteryProcess);
        btnJoinWaitlist = view.findViewById(R.id.btnJoinWaitlist);
        progressBar = view.findViewById(R.id.progressBar);

        // Make lottery process scrollable
        tvLotteryProcess.setMovementMethod(new ScrollingMovementMethod());

        // Set up button click listener
        btnJoinWaitlist.setOnClickListener(v -> handleWaitlistAction());

        // Load event data
        loadEventData();
    }

    private void loadEventData() {
        showLoading(true);

        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(getContext(), "Invalid event ID", Toast.LENGTH_SHORT).show();
            showLoading(false);
            return;
        }

        // Load event details
        eventDB.getEvent(eventId, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    currentEvent = document.toObject(Event.class);
                    if (currentEvent != null) {
                        displayEventData();
                        checkWaitlistStatus();
                    }
                } else {
                    Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
                    showLoading(false);
                }
            } else {
                Toast.makeText(getContext(), "Failed to load event data", Toast.LENGTH_SHORT).show();
                showLoading(false);
            }
        });
    }

    private void displayEventData() {
        if (currentEvent == null) return;

        tvTotalParticipants.setText(String.valueOf(currentEvent.getTotalParticipants()));
        tvWaitlistCount.setText(String.valueOf(currentEvent.getWaitlistCount()));
        tvLotteryProcess.setText(currentEvent.getLotteryProcess());
    }

    private void checkWaitlistStatus() {
        String userId = FBAuthenticator.getCurrentUser().getUid();

        eventDB.isUserInWaitlist(eventId, userId, task -> {
            showLoading(false);
            if (task.isSuccessful() && task.getResult() != null) {
                isInWaitlist = task.getResult().exists();
                updateButtonState();
            } else {
                Toast.makeText(getContext(), "Failed to check waitlist status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateButtonState() {
        if (isInWaitlist) {
            btnJoinWaitlist.setText("LEAVE WAITLIST");
            btnJoinWaitlist.setEnabled(true);
        } else {
            btnJoinWaitlist.setText("JOIN WAITLIST");
            btnJoinWaitlist.setEnabled(true);
        }
    }

    private void handleWaitlistAction() {
        if (!FBAuthenticator.isLoggedIn()) {
            Toast.makeText(getContext(), "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);
        btnJoinWaitlist.setEnabled(false);

        String userId = FBAuthenticator.getCurrentUser().getUid();

        if (isInWaitlist) {
            // Leave waitlist
            eventDB.leaveWaitlist(eventId, userId, task -> {
                showLoading(false);
                if (task.isSuccessful()) {
                    isInWaitlist = false;
                    updateButtonState();
                    Toast.makeText(getContext(), "Left waitlist successfully", Toast.LENGTH_SHORT).show();
                    // Refresh data to update count
                    loadEventData();
                } else {
                    btnJoinWaitlist.setEnabled(true);
                    Toast.makeText(getContext(), "Failed to leave waitlist", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Join waitlist
            eventDB.joinWaitlist(eventId, userId, task -> {
                showLoading(false);
                if (task.isSuccessful()) {
                    isInWaitlist = true;
                    updateButtonState();
                    Toast.makeText(getContext(), "Joined waitlist successfully", Toast.LENGTH_SHORT).show();
                    // Refresh data to update count
                    loadEventData();
                } else {
                    btnJoinWaitlist.setEnabled(true);
                    Toast.makeText(getContext(), "Failed to join waitlist", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}