package com.example.icetea.organizer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.icetea.auth.FBAuthenticator;
import com.example.icetea.event.Event;
import com.example.icetea.event.EventAdapter;
import com.example.icetea.event.EventController;
import com.example.icetea.R;
import com.example.icetea.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class OrganizerHomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private final List<Event> eventList = new ArrayList<>();
    private EventController eventController;
    TextView emptyMessage;

    public OrganizerHomeFragment() {
        // Required empty public constructor
    }

    public static OrganizerHomeFragment newInstance() {
        return new OrganizerHomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_organizer_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.organizerEventList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        eventController = new EventController();

        adapter = new EventAdapter(eventList, event -> openEventDetails(event));
        recyclerView.setAdapter(adapter);
        loadEvents(view);
    }
    private void loadEvents(View view) {
        String organizerId = FBAuthenticator.getCurrentUserId();

        eventController.getEventsByOrganizerId(organizerId, new Callback<List<Event>>() {
            @Override
            public void onSuccess(List<Event> events) {
                eventList.clear();

                if (events.isEmpty()) {
                    emptyMessage = view.findViewById(R.id.emptyMessage);
                    emptyMessage.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    eventList.addAll(events);
                }
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to load events: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openEventDetails(Event event) {
        OrganizerEventDetailsFragment fragment = new OrganizerEventDetailsFragment();

        Bundle args  = new Bundle();
        args.putString("eventId", event.getId());
        args.putString("name", event.getName());
        args.putString("description", event.getDescription());
        args.putString("location", event.getLocation());
        args.putInt("capacity", event.getCapacity() != null ? event.getCapacity() : 0);

        if (event.getStartDate() != null) {
            args.putLong("startDate", event.getStartDate().toDate().getTime());
        }
        if (event.getEndDate() != null) {
            args.putLong("endDate", event.getEndDate().toDate().getTime());
        }
        if (event.getRegistrationStartDate() != null) {
            args.putLong("regOpen", event.getRegistrationStartDate().toDate().getTime());
        }
        if (event.getRegistrationEndDate() != null) {
            args.putLong("regClose", event.getRegistrationEndDate().toDate().getTime());
        }

        fragment.setArguments(args);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.organizer_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}