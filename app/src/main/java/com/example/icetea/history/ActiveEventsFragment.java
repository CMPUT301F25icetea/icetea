package com.example.icetea.history;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

public class ActiveEventsFragment extends Fragment {

    private RecyclerView recyclerView;
    private HistoryEventAdapter adapter;
    private List<HistoryEventItem> eventList;
    private TextView emptyTextView;

    public ActiveEventsFragment() {
        // Required empty public constructor
    }

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
        return inflater.inflate(R.layout.fragment_active_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerActiveEvents);
        emptyTextView = view.findViewById(R.id.textEmptyActiveEvents);

        eventList = new ArrayList<>();

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

        loadActiveEvents();
    }

    private void loadActiveEvents() {
        String userId = CurrentUser.getInstance().getFid();

        // Get all waitlist entries for this user
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

            // Create a map to store waitlist status by eventId
            Map<String, String> eventStatusMap = new HashMap<>();
            List<String> eventIds = new ArrayList<>();

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

            // Load events for each waitlist entry
            loadEventsWithStatus(eventIds, eventStatusMap);
        });
    }

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

                // When all events are loaded, update the UI
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

    private boolean isEventActive(Event event) {
        // Event is active if registration hasn't ended yet OR event hasn't ended yet
        long currentTime = System.currentTimeMillis();

        if (event.getRegistrationEndDate() != null) {
            long regEndTime = event.getRegistrationEndDate().toDate().getTime();
            if (currentTime < regEndTime) {
                return true;
            }
        }

        if (event.getEventEndDate() != null) {
            long eventEndTime = event.getEventEndDate().toDate().getTime();
            return currentTime < eventEndTime;
        }

        // If event has started but no end date, consider it active
        if (event.getEventStartDate() != null) {
            long eventStartTime = event.getEventStartDate().toDate().getTime();
            return currentTime >= eventStartTime;
        }

        return true; // Default to active if dates are unclear
    }

    private void showEmptyState() {
        emptyTextView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void hideEmptyState() {
        emptyTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
}