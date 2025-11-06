package com.example.icetea.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icetea.R;
import com.example.icetea.models.Waitlist;
import com.example.icetea.models.WaitlistDB;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows the list of final entrants (accepted users) for a specific event.
 */
public class OrganizerFinalEntrantsFragment extends Fragment {
    private RecyclerView recyclerView;
    private OrganizerFinalEntrantAdapter adapter;
    private final List<Waitlist> entrants = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_organizer_final_entrants, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.finalEntrantsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrganizerFinalEntrantAdapter(entrants);
        recyclerView.setAdapter(adapter);

        String eventId = getArguments().getString("eventId");

        WaitlistDB.getInstance().getFinalEntrants(eventId, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                entrants.clear();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    entrants.add(doc.toObject(Waitlist.class));
                }
                adapter.notifyDataSetChanged();

                if (entrants.isEmpty()) {
                    Toast.makeText(getContext(), "No final entrants yet.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Failed to load entrants.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

