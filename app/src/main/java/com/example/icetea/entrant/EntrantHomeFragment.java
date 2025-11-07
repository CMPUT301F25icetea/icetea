package com.example.icetea.entrant;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.icetea.R;
import com.example.icetea.models.Event; // ✅ connected to shared model
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * Fragment that serves as the home screen for entrants in the application.
 * Displays a list of available events retrieved from Firestore and provides
 * navigation to event details and notifications.
 *
 * <p>This fragment shows all events in a ListView where each item displays
 * the event name and description. Users can tap on an event to view its details
 * or access the notifications screen via a dedicated button.</p>
 */
public class EntrantHomeFragment extends Fragment {

    /** Tag for logging purposes */
    private static final String TAG = "EntrantHomeFragment";

    /** Argument key for first parameter (currently unused) */
    private static final String ARG_PARAM1 = "param1";

    /** Argument key for second parameter (currently unused) */
    private static final String ARG_PARAM2 = "param2";

    /** First parameter value (currently unused) */
    private String mParam1;

    /** Second parameter value (currently unused) */
    private String mParam2;

    /** ListView displaying the list of events */
    public ListView listView;

    /** Button for navigating to the notifications screen */
    public Button notificationButton;

    /** Adapter for displaying event names in the ListView */
    public ArrayAdapter<String> adapter;

    /** List of formatted event names and descriptions for display */
    public ArrayList<String> eventNamesList;

    /** List of Event objects corresponding to the displayed items */
    public ArrayList<Event> eventsList;

    /** Firestore database instance for retrieving events */
    public FirebaseFirestore db;

    /**
     * Required empty public constructor for fragment instantiation.
     * Use {@link #newInstance()} to create instances of this fragment.
     */
    public EntrantHomeFragment() {
    }

    /**
     * Factory method to create a new instance of this fragment.
     * This is the preferred way to create instances of this fragment.
     *
     * @return a new instance of EntrantHomeFragment
     */
    public static EntrantHomeFragment newInstance() {
        return new EntrantHomeFragment();
    }

    /**
     * Called when the fragment is first created. Retrieves any arguments passed
     * to the fragment and initializes the Firestore database instance.
     *
     * @param savedInstanceState the saved state of the fragment, if any
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        db = FirebaseFirestore.getInstance();
    }

    /**
     * Creates and returns the view hierarchy associated with the fragment.
     * Initializes the ListView and notification button, sets up their listeners,
     * and loads the events from Firestore.
     *
     * <p>The ListView allows users to tap on events to view details, while the
     * notification button navigates to the notifications screen.</p>
     *
     * @param inflater the LayoutInflater object to inflate views
     * @param container the parent view that the fragment's UI should be attached to
     * @param savedInstanceState the saved state of the fragment, if any
     * @return the root View of the fragment's layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_entrant_home, container, false);

        listView = view.findViewById(R.id.List);
        notificationButton = view.findViewById(R.id.Notification);

        notificationButton.setOnClickListener(v -> {
            EntrantNotificationsFragment fragment = new EntrantNotificationsFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.entrant_fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        eventNamesList = new ArrayList<>();
        eventsList = new ArrayList<>();

        adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, eventNamesList);
        listView.setAdapter(adapter);

        loadEventsFromFirestore();

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Event selectedEvent = eventsList.get(position);

            Bundle bundle = new Bundle();
            bundle.putString("event_id", selectedEvent.getId());

            UserEventDetailsFragment detailsFragment = new UserEventDetailsFragment();
            detailsFragment.setArguments(bundle);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.entrant_fragment_container, detailsFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    /**
     * Loads all events from the Firestore "events" collection and populates
     * the ListView with the results. Each event is converted to an Event object
     * and its name and description are formatted for display.
     *
     * <p>If no events are found, a toast message is displayed to inform the user.
     * Any errors during the loading process are logged and shown via toast messages.</p>
     *
     * <p>The method clears existing data before loading to ensure the list reflects
     * the current state of the database. After successful loading, the adapter is
     * notified to refresh the display.</p>
     */
    public void loadEventsFromFirestore() {
        db.collection("events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eventNamesList.clear();
                        eventsList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            event.setId(document.getId());

                            eventsList.add(event);

                            String name = event.getName() != null ? event.getName() : "Unnamed Event";
                            String description = event.getDescription() != null ? event.getDescription() : "No description";

                            eventNamesList.add(name + "\n" + description);
                        }

                        adapter.notifyDataSetChanged();

                        if (eventsList.isEmpty()) {
                            Toast.makeText(getContext(), "No events found", Toast.LENGTH_SHORT).show();
                        }

                        Log.d(TAG, "Events loaded: " + eventsList.size());
                    } else {
                        Log.e(TAG, "Error getting events: ", task.getException());
                        Toast.makeText(getContext(), "Error loading events", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}