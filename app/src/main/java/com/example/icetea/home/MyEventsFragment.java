package com.example.icetea.home;

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
import android.widget.Toast;

import com.example.icetea.R;
import com.example.icetea.auth.CurrentUser;
import com.example.icetea.models.Event;
import com.example.icetea.models.EventDB;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that displays all events created by the current user (organizer).
 * Users can view their events in a RecyclerView, tap to manage a specific event,
 * or create a new event via the floating action button.
 */
public class MyEventsFragment extends Fragment {

    /** List holding the user's events */
    private List<Event> myEventList = new ArrayList<>();

    /** Adapter for the RecyclerView displaying events */
    private EventAdapter adapter;

    /**
     * Required empty public constructor.
     */
    public MyEventsFragment() {
    }

    /**
     * Factory method to create a new instance of this fragment.
     *
     * @return A new instance of MyEventsFragment
     */
    public static MyEventsFragment newInstance() {
        return new MyEventsFragment();
    }

    /**
     * Called to do initial creation of the fragment.
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // No additional initialization needed here
    }

    /**
     * Inflates the fragment layout.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state
     * @return The inflated view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_events, container, false);
    }

    /**
     * Called immediately after onCreateView.
     * Sets up the RecyclerView, adapter, and floating action button to create new events.
     *
     * @param view The View returned by onCreateView
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // FloatingActionButton to navigate to CreateEventFragment
        FloatingActionButton createEventButton = view.findViewById(R.id.fabCreateEvent);
        createEventButton.setOnClickListener(v -> {
            FragmentManager fm = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.setReorderingAllowed(true);
            transaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
            );
            transaction.replace(R.id.main_fragment_container, CreateEventFragment.newInstance());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // RecyclerView setup
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewMyEvents);
        adapter = new EventAdapter(myEventList, event -> {
            // Navigate to ManageEventFragment when an event is clicked
            FragmentManager fm = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.setReorderingAllowed(true);
            transaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
            );
            transaction.replace(R.id.main_fragment_container, ManageEventFragment.newInstance(event.getEventId()));
            transaction.addToBackStack(null);
            transaction.commit();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Load the current user's events from the database
        loadMyEvents();
    }

    /**
     * Fetches events created by the current user (organizer) from the database.
     * Updates the RecyclerView adapter upon successful retrieval.
     * Displays a Toast message if fetching fails.
     */
    private void loadMyEvents() {
        EventDB.getInstance().getEventsByOrganizer(CurrentUser.getInstance().getFid(), task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Event> fetchedEvents = new ArrayList<>();
                for (DocumentSnapshot doc : task.getResult()) {
                    Event event = doc.toObject(Event.class);
                    if (event != null) fetchedEvents.add(event);
                }

                myEventList.clear();
                myEventList.addAll(fetchedEvents);
                adapter.notifyDataSetChanged();

            } else {
                Toast.makeText(getContext(), "Failed to load your events", Toast.LENGTH_SHORT).show();
                Log.e("MyEventsFragment", "Error fetching events", task.getException());
            }
        });
    }
}