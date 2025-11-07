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
 * Fragment that displays detailed information about an event and allows entrants
 * to manage their waitlist status. Shows event capacity, current waitlist count,
 * lottery process information, and provides functionality to join or leave the waitlist.
 *
 * <p>This fragment retrieves event data from Firestore and updates the UI accordingly.
 * It also checks the current user's waitlist status and enables them to toggle their
 * participation in the event's waitlist.</p>
 */
public class UserEventDetailsFragment extends Fragment {

    /** Argument key for passing event ID to the fragment */
    private static final String ARG_EVENT_ID = "event_id";

    /** TextView displaying the total number of participants (event capacity) */
    public TextView tvTotalParticipants;

    /** TextView displaying the current waitlist count */
    public TextView tvWaitlistCount;

    /** TextView displaying the lottery process information */
    public TextView tvLotteryProcess;

    /** Button for joining or leaving the waitlist */
    public Button btnJoinWaitlist;

    /** Progress bar shown during loading operations */
    public ProgressBar progressBar;

    /** The ID of the event being displayed */
    private String eventId;

    /** The current event object loaded from the database */
    public Event currentEvent;

    /** Flag indicating whether the current user is in the waitlist */
    public boolean isInWaitlist = false;

    /** Database instance for event operations */
    public EventDB eventDB;

    /**
     * Required empty public constructor for fragment instantiation.
     * Use {@link #newInstance(String)} to create instances with arguments.
     */
    public UserEventDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment with the specified event ID.
     * This is the preferred way to create instances of this fragment.
     *
     * @param eventId the ID of the event to display details for
     * @return a new instance of UserEventDetailsFragment configured with the given event ID
     */
    public static UserEventDetailsFragment newInstance(String eventId) {
        UserEventDetailsFragment fragment = new UserEventDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called when the fragment is first created. Retrieves the event ID from arguments
     * and initializes the EventDB instance.
     *
     * @param savedInstanceState the saved state of the fragment, if any
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
        }
        eventDB = EventDB.getInstance();
    }

    /**
     * Creates and returns the view hierarchy associated with the fragment.
     *
     * @param inflater the LayoutInflater object to inflate views
     * @param container the parent view that the fragment's UI should be attached to
     * @param savedInstanceState the saved state of the fragment, if any
     * @return the root View of the fragment's layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_event_details, container, false);
    }

    /**
     * Called after the view has been created. Initializes all UI components,
     * sets up event listeners, and loads the event data from the database.
     *
     * @param view the View returned by onCreateView
     * @param savedInstanceState the saved state of the fragment, if any
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

        // Make lottery process scrollable
        tvLotteryProcess.setMovementMethod(new ScrollingMovementMethod());

        // Set up button click listener
        btnJoinWaitlist.setOnClickListener(v -> handleWaitlistAction());

        // Load event data
        loadEventData();
    }

    /**
     * Loads event data from Firestore database. Validates the event ID,
     * retrieves the event document, and initiates the display and waitlist
     * status check processes. Shows appropriate error messages if the event
     * cannot be loaded.
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

    /**
     * Displays the event data in the UI components. Updates the total participants
     * (capacity), waitlist count, and lottery process information. Handles null
     * values gracefully by displaying default values or messages.
     */
    public void displayEventData() {
        if (currentEvent == null) return;

        try {
            // Display capacity as total participants (0 if null or doesn't exist)
            int capacity = currentEvent.getCapacity();
            tvTotalParticipants.setText(String.valueOf(capacity));
        } catch (Exception e) {
            tvTotalParticipants.setText("0");
        }

        try {
            tvWaitlistCount.setText(String.valueOf(currentEvent.getWaitlistCount()));
        } catch (Exception e) {
            tvWaitlistCount.setText("0");
        }

        // Handle null lottery process text
        try {
            String lotteryText = currentEvent.getLotteryProcess();
            tvLotteryProcess.setText(lotteryText != null && !lotteryText.isEmpty() ? lotteryText : "No lottery process information available");
        } catch (Exception e) {
            tvLotteryProcess.setText("No lottery process information available");
        }
    }

    /**
     * Checks whether the current user is already in the event's waitlist.
     * Updates the button state accordingly once the status is determined.
     * Uses the current user's ID from FBAuthenticator to query the database.
     */
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

    /**
     * Updates the join/leave waitlist button based on the current user's
     * waitlist status. Changes button text to "LEAVE WAITLIST" if the user
     * is in the waitlist, or "JOIN WAITLIST" if they are not.
     */
    private void updateButtonState() {
        if (isInWaitlist) {
            btnJoinWaitlist.setText("LEAVE WAITLIST");
            btnJoinWaitlist.setEnabled(true);
        } else {
            btnJoinWaitlist.setText("JOIN WAITLIST");
            btnJoinWaitlist.setEnabled(true);
        }
    }

    /**
     * Handles the user's action to join or leave the waitlist. Checks if the user
     * is logged in, then performs the appropriate database operation based on their
     * current waitlist status. Updates the UI and reloads event data upon successful
     * completion. Shows appropriate error messages if the operation fails.
     */
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

    /**
     * Shows or hides the progress bar loading indicator.
     *
     * @param show true to show the progress bar, false to hide it
     */
    public void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }


}