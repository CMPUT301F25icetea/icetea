package com.example.icetea.history;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.icetea.R;
import com.example.icetea.auth.CurrentUser;
import com.example.icetea.home.EventDetailsFragment;
import com.example.icetea.models.Event;
import com.example.icetea.models.EventDB;
import com.example.icetea.models.Waitlist;
import com.example.icetea.models.WaitlistDB;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fragment displaying a list of active events that the current user is participating in.
 * Each event shows the user's waitlist status.
 */
public class ActiveEventsFragment extends Fragment {

    /** RecyclerView displaying the list of active events. */
    private RecyclerView recyclerView;

    /** Adapter for binding active event data to the RecyclerView. */
    private HistoryEventAdapter adapter;

    /** List of HistoryEventItems representing active events. */
    private List<HistoryEventItem> eventList;

    /** TextView shown when there are no active events to display. */
    private TextView emptyTextView;

    /**
     * Default constructor.
     */
    public ActiveEventsFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment.
     *
     * @return A new instance of ActiveEventsFragment
     */
    public static ActiveEventsFragment newInstance() {
        return new ActiveEventsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment layout
        return inflater.inflate(R.layout.fragment_active_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerActiveEvents);
        emptyTextView = view.findViewById(R.id.textEmptyActiveEvents);

        eventList = new ArrayList<>();

        // Initialize adapter with click listener to open event details
        adapter = new HistoryEventAdapter(eventList, event -> {
            FragmentManager fm = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.setReorderingAllowed(true);
            transaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
            );
            transaction.replace(R.id.main_fragment_container,
                    EventDetailsFragment.newInstance(event.getEvent().getEventId()));
            transaction.addToBackStack(null);
            transaction.commit();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Load all active events for the current user
        loadActiveEvents();
    }

    /**
     * Loads all waitlist entries for the current user and filters them
     * to include only active events.
     */
    private void loadActiveEvents() {
        String userId = CurrentUser.getInstance().getFid();

        WaitlistDB.getInstance().getUserWaitlist(userId, task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                showEmptyState();
                return;
            }

            List<DocumentSnapshot> waitlistDocs = task.getResult().getDocuments();
            if (waitlistDocs.isEmpty()) {
                showEmptyState();
                return;
            }

            Map<String, String> eventStatusMap = new HashMap<>();
            List<String> eventIds = new ArrayList<>();

            // Collect event IDs and corresponding waitlist status
            for (DocumentSnapshot doc : waitlistDocs) {
                Waitlist waitlist = doc.toObject(Waitlist.class);
                if (waitlist != null) {
                    eventIds.add(waitlist.getEventId());
                    eventStatusMap.put(waitlist.getEventId(), waitlist.getStatus());
                }
            }

            if (eventIds.isEmpty()) {
                showEmptyState();
                return;
            }

            // Load Event objects along with their waitlist status
            loadEventsWithStatus(eventIds, eventStatusMap);
        });
    }

    /**
     * Fetches Event objects from the database and pairs them with
     * the user's waitlist status. Only includes active events.
     *
     * @param eventIds  List of event IDs to fetch
     * @param statusMap Mapping from event ID to waitlist status
     */
    private void loadEventsWithStatus(List<String> eventIds, Map<String, String> statusMap) {
        List<HistoryEventItem> tempList = new ArrayList<>();
        int[] loadedCount = {0};

        for (String eventId : eventIds) {
            EventDB.getInstance().getEvent(eventId, task -> {
                loadedCount[0]++;

                if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                    Event event = task.getResult().toObject(Event.class);
                    if (event != null && isEventActive(event)) {
                        String status = statusMap.get(eventId);
                        tempList.add(new HistoryEventItem(event, status));
                    }
                }

                // Update adapter when all events are loaded
                if (loadedCount[0] == eventIds.size()) {
                    eventList.clear();
                    eventList.addAll(tempList);

                    if (eventList.isEmpty()) {
                        showEmptyState();
                    } else {
                        hideEmptyState();
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    /**
     * Determines whether an event is currently active.
     * An event is active if its registration hasn't ended or if it hasn't ended yet.
     *
     * @param event The event to check
     * @return True if the event is active, false otherwise
     */
    private boolean isEventActive(Event event) {
        long currentTime = System.currentTimeMillis();

        if (event.getRegistrationEndDate() != null) {
            long regEndTime = event.getRegistrationEndDate().toDate().getTime();
            if (currentTime < regEndTime) return true;
        }

        if (event.getEventEndDate() != null) {
            long eventEndTime = event.getEventEndDate().toDate().getTime();
            return currentTime < eventEndTime;
        }

        if (event.getEventStartDate() != null) {
            long eventStartTime = event.getEventStartDate().toDate().getTime();
            return currentTime >= eventStartTime;
        }

        return true; // Default to active if dates are unclear
    }

    /**
     * Shows the empty state view when there are no active events.
     */
    private void showEmptyState() {
        emptyTextView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    /**
     * Hides the empty state view and shows the RecyclerView.
     */
    private void hideEmptyState() {
        emptyTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
}