package com.example.icetea.organizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icetea.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrganizerEntrantWinnersFragment extends Fragment {

    private RecyclerView winnersRecyclerView;
    private Button notifyButton;
    private OrganizerWinnerAdapter adapter;
    private String eventId;

    public OrganizerEntrantWinnersFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_organizer_winners, container, false);

        winnersRecyclerView = view.findViewById(R.id.winnersRecyclerView);
        notifyButton = view.findViewById(R.id.buttonNotifyWinners);

        winnersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrganizerWinnerAdapter(new ArrayList<>());
        winnersRecyclerView.setAdapter(adapter);

        Bundle args = getArguments();
        if (args != null) {
            eventId = args.getString("eventId");
            loadWinners(eventId);
        }

        notifyButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Notifications coming soon!", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void loadWinners(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("waitlist")
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("status", "invited")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<DocumentSnapshot> docs = task.getResult().getDocuments();
                        adapter.setData(docs);
                        if (docs.isEmpty()) {
                            Toast.makeText(getContext(), "No winners yet.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to load winners.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
