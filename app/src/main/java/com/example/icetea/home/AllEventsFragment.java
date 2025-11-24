package com.example.icetea.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.icetea.R;
import com.example.icetea.event.Event;
import com.example.icetea.event.EventDB;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AllEventsFragment extends Fragment {
    private List<Event> eventList;
    private EventAdapter adapter;

    public AllEventsFragment() {
        // Required empty public constructor
    }

    public static AllEventsFragment newInstance() {
        return new AllEventsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewAllEvents);
        eventList = new ArrayList<>();
        adapter = new EventAdapter(eventList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        loadEvents();
    }

    private void loadEvents() {
        EventDB.getInstance().getActiveEvents(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Event> fetchedEvents = new ArrayList<>();
                for (DocumentSnapshot doc : task.getResult()) {
                    Event event = doc.toObject(Event.class);
                    if (event != null) {
                        fetchedEvents.add(event);
                    }
                }
                eventList.clear();
                eventList.addAll(fetchedEvents);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
                Log.e("AllEventsFragment", "Failed to fetch events", task.getException());
            }
        });
    }
}