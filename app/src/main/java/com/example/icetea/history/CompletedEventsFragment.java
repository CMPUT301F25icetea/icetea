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
 * Fragment displaying a list of completed events that the current user participated in.
 * Each event is shown with the user's waitlist status.
 */
public class CompletedEventsFragment extends Fragment {

    /** RecyclerView for displaying the list of completed events. */
    private RecyclerView recyclerView;

    /** Adapter for binding event data to the RecyclerView. */
    private HistoryEventAdapter adapter;

    /** List of HistoryEventItems representing completed events. */
    private List<HistoryEventItem> eventList;

    /** TextView shown when there are no completed events to display. */
    private TextView emptyTextView;

    /**
     * Default constructor.
     */
    public CompletedEventsFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment.
     *
     * @return A new instance of CompletedEventsFragment
     */
    public static CompletedEventsFragment newInstance() {
        return new CompletedEventsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment layout
        return inflater.inflate(R.layout.fragment_completed_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerCompletedEvents);
        emptyTextView = view.findViewById(R.id.textEmptyCompletedEvents);

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

        // Load completed events for the current user
        loadCompletedEvents();
    }

    /**
     * Loads all waitlisted events for the current user and filters
     * them to include only completed events.
     */
    private void loadCompletedEvents() {
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

            // Collect event IDs and waitlist status for the user
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

            // Load event objects with their statuses
            loadEventsWithStatus(eventIds, eventStatusMap);
        });
    }

    /**
     * Fetches Event objects from the database and pairs them with
     * the user's waitlist status. Only includes completed events.
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
                    if (event != null && isEventCompleted(event)) {
                        String status = statusMap.get(eventId);
                        tempList.add(new HistoryEventItem(event, status));
                    }
                }

                // Update adapter once all events are loaded
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
     * Determines whether an event is considered completed.
     * An event is completed if its end date has passed or, if no end date,
     * its registration has ended.
     *
     * @param event The event to check
     * @return True if the event is completed, false otherwise
     */
    private boolean isEventCompleted(Event event) {
        long currentTime = System.currentTimeMillis();

        if (event.getEventEndDate() != null) {
            long eventEndTime = event.getEventEndDate().toDate().getTime();
            return currentTime >= eventEndTime;
        }

        if (event.getRegistrationEndDate() != null) {
            long regEndTime = event.getRegistrationEndDate().toDate().getTime();
            return currentTime >= regEndTime;
        }

        return false;
    }

    /**
     * Shows the empty state view when there are no completed events.
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