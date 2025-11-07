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

/**
 * A {@link Fragment} subclass that displays detailed information about a specific event,
 * focusing on waitlist details and lottery processes.
 * It allows a user to join or leave the waitlist for the event.
 */
public class UserEventDetailsFragment extends Fragment {

    /** Key for passing the event ID as an argument to this fragment. */
    private static final String ARG_EVENT_ID = "event_id";

    // UI Components
    private TextView tvTotalParticipants;
    private TextView tvWaitlistCount;
    private TextView tvLotteryProcess;
    private Button btnJoinWaitlist;
    private ProgressBar progressBar;

    // State and Data
    private String eventId;
    private Event currentEvent;
    private boolean isInWaitlist = false;
    private EventDB eventDB;

    /**
     * Required empty public constructor for Fragment instantiation.
     */
    public UserEventDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment
     * using the provided event ID.
     *
     * @param eventId The ID of the event to display.
     * @return A new instance of fragment UserEventDetailsFragment.
     */
    public static UserEventDetailsFragment newInstance(String eventId) {
        UserEventDetailsFragment fragment = new UserEventDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called when the fragment is first created. Responsible for
     * initializing non-view components and retrieving arguments.
     *
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
        }
        // Get a singleton instance of the EventDB
        eventDB = EventDB.getInstance();
    }

    /**
     * Called to create the fragment's view hierarchy.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views.
     * @param container          If non-null, this is the parent view that the fragment's
     * UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed.
     * @return The root View of the fragment's layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_event_details, container, false);
    }

    /**
     * Called after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has returned.
     * Responsible for initializing UI components and setting listeners.
     *
     * @param view               The View returned by {@link #onCreateView}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        tvTotalParticipants = view.findViewById(R.id.tvTotalParticipants);
        tvWaitlistCount = view.findViewById(R.id.tvWaitlistCount);
        tvLotteryProcess = view.findViewById(R.id.tvLotteryProcess);
        btnJoinWaitlist = view.findViewById(R.id.btnJoinWaitlist);
        progressBar = view.findViewById(R.id.progressBar);

        // Make lottery process TextView scrollable
        tvLotteryProcess.setMovementMethod(new ScrollingMovementMethod());

        // Set up button click listener
        btnJoinWaitlist.setOnClickListener(v -> handleWaitlistAction());

        // Load event data from database
        loadEventData();
    }

    /**
     * Fetches the event details from the database using the eventId.
     * On success, it calls {@link #displayEventData()} and {@link #checkWaitlistStatus()}.
     * Displays Toasts on failure.
     */
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
                        checkWaitlistStatus(); // Check waitlist status *after* event data is loaded
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

    /**
     * Populates the UI elements with data from the {@link #currentEvent} object.
     */
    private void displayEventData() {
        if (currentEvent == null) return;

        tvTotalParticipants.setText(String.valueOf(currentEvent.getTotalParticipants()));
        tvWaitlistCount.setText(String.valueOf(currentEvent.getWaitlistCount()));
        tvLotteryProcess.setText(currentEvent.getLotteryProcess());
    }

    /**
     * Checks if the currently logged-in user is already in the waitlist for this event.
     * Updates the {@link #isInWaitlist} flag and calls {@link #updateButtonState()}
     * on completion.
     */
    private void checkWaitlistStatus() {
        String userId = FBAuthenticator.getCurrentUser().getUid();

        eventDB.isUserInWaitlist(eventId, userId, task -> {
            showLoading(false); // Hide loading after *all* data is fetched
            if (task.isSuccessful() && task.getResult() != null) {
                isInWaitlist = task.getResult().exists();
                updateButtonState();
            } else {
                Toast.makeText(getContext(), "Failed to check waitlist status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Toggles the text and state of the join/leave button based on
     * the {@link #isInWaitlist} flag.
     */
    private void updateButtonState() {
        if (isInWaitlist) {
            btnJoinWaitlist.setText("LEAVE WAITLIST");
        } else {
            btnJoinWaitlist.setText("JOIN WAITLIST");
        }
        btnJoinWaitlist.setEnabled(true); // Enable button after status is confirmed
    }

    /**
     * Handles the click event for the waitlist button.
     * It either calls {@link EventDB#joinWaitlist(String, String, com.google.android.gms.tasks.OnCompleteListener)}
     * or {@link EventDB#leaveWaitlist(String, String, com.google.android.gms.tasks.OnCompleteListener)}
     * based on the current waitlist status.
     */
    private void handleWaitlistAction() {
        if (!FBAuthenticator.isLoggedIn()) {
            Toast.makeText(getContext(), "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);
        btnJoinWaitlist.setEnabled(false); // Disable button during operation

        String userId = FBAuthenticator.getCurrentUser().getUid();

        if (isInWaitlist) {
            // User wants to leave the waitlist
            eventDB.leaveWaitlist(eventId, userId, task -> {
                showLoading(false);
                if (task.isSuccessful()) {
                    isInWaitlist = false;
                    updateButtonState();
                    Toast.makeText(getContext(), "Left waitlist successfully", Toast.LENGTH_SHORT).show();
                    // Refresh data to update waitlist count
                    loadEventData();
                } else {
                    btnJoinWaitlist.setEnabled(true); // Re-enable on failure
                    Toast.makeText(getContext(), "Failed to leave waitlist", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // User wants to join the waitlist
            eventDB.joinWaitlist(eventId, userId, task -> {
                showLoading(false);
                if (task.isSuccessful()) {
                    isInWaitlist = true;
                    updateButtonState();
                    Toast.makeText(getContext(), "Joined waitlist successfully", Toast.LENGTH_SHORT).show();
                    // Refresh data to update waitlist count
                    loadEventData();
                } else {
                    btnJoinWaitlist.setEnabled(true); // Re-enable on failure
                    Toast.makeText(getContext(), "Failed to join waitlist", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Shows or hides the progress bar.
     *
     * @param show True to show the progress bar, false to hide it.
     */
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}