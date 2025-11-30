package com.example.icetea.admin;

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
import android.widget.Toast;

import com.example.icetea.R;
import com.example.icetea.home.ManageEventFragment;
import com.example.icetea.models.Event;
import com.example.icetea.models.EventDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment used in the Admin section to display a list of all events.
 * <p>
 * Events are displayed in a RecyclerView using AdminEventsAdapter. Clicking an event
 * opens the ManageEventFragment for that specific event.
 */
public class AdminEventsFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdminEventsAdapter adapter;
    private List<Event> events;

    /**
     * Default public constructor (required).
     */
    public AdminEventsFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of AdminEventsFragment.
     *
     * @return a new instance of AdminEventsFragment
     */
    public static AdminEventsFragment newInstance() {
        return new AdminEventsFragment();
    }

    /**
     * Called to do initial creation of the fragment.
     *
     * @param savedInstanceState the saved state of the fragment
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           LayoutInflater object to inflate views
     * @param container          parent view that the fragment's UI should attach to
     * @param savedInstanceState saved state of the fragment
     * @return the root view for the fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_events, container, false);
    }

    /**
     * Called immediately after onCreateView() has returned.
     * Initializes the RecyclerView, adapter, and loads events from the database.
     *
     * @param view               the View returned by onCreateView
     * @param savedInstanceState saved state of the fragment
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerAdminEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        events = new ArrayList<>();
        adapter = new AdminEventsAdapter(requireContext(), events, event -> {
            // Navigate to ManageEventFragment on event click
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

        recyclerView.setAdapter(adapter);

        loadEvents();
    }

    /**
     * Loads all events from the EventDB and updates the RecyclerView adapter.
     * Displays a Toast if loading fails.
     */
    private void loadEvents() {
        EventDB.getInstance().getAllEvents(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                events.clear();
                for (var doc : task.getResult().getDocuments()) {
                    Event event = doc.toObject(Event.class);
                    if (event != null) events.add(event);
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(requireContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
