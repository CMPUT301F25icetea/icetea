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

import com.example.icetea.event.Event;
import com.example.icetea.event.EventAdapter;
import com.example.icetea.event.EventController;
import com.example.icetea.R;
import com.example.icetea.util.Callback;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class OrganizerHomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<Event> eventList = new ArrayList<>();
    private EventController eventController;

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

        adapter = new EventAdapter(eventList, event -> {
            // NavigationHelper.navigateToEventDetails(this, event);
        });
        recyclerView.setAdapter(adapter);

        loadEvents();
    }
    private void loadEvents() {

        String organizerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        eventController.getEventsByOrganizerId(organizerId, new Callback<List<Event>>() {
            @Override
            public void onSuccess(List<Event> events) {
                eventList.clear();
                eventList.addAll(events);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }
}