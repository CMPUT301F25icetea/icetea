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

public class EntrantHomeFragment extends Fragment {

    private static final String TAG = "EntrantHomeFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ListView listView;
    private Button notificationButton;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> eventNamesList;
    private ArrayList<Event> eventsList;
    private FirebaseFirestore db;

    public EntrantHomeFragment() {
    }

    public static EntrantHomeFragment newInstance() {
        return new EntrantHomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        db = FirebaseFirestore.getInstance();
    }

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

    private void loadEventsFromFirestore() {
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
