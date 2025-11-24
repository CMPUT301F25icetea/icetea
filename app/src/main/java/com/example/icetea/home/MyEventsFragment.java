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
import com.example.icetea.auth.SignUpFragment;
import com.example.icetea.event.Event;
import com.example.icetea.event.EventDB;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import org.checkerframework.checker.units.qual.Current;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyEventsFragment extends Fragment {
    private List<Event> myEventList = new ArrayList<>();
    private EventAdapter adapter;
    public MyEventsFragment() {
        // Required empty public constructor
    }

    public static MyEventsFragment newInstance() {

        return new MyEventsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewMyEvents);
        adapter = new EventAdapter(myEventList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadMyEvents();
    }
    private void loadMyEvents() {

        EventDB.getInstance().getEventsByOrganizer(CurrentUser.getInstance().getFid(),task -> {
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