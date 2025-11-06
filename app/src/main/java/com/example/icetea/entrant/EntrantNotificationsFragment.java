package com.example.icetea.entrant;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icetea.R;
import com.example.icetea.auth.FBAuthenticator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class EntrantNotificationsFragment extends Fragment {

    private RecyclerView recyclerNotifications;
    private ProgressBar progressBar;
    private NotificationAdapter adapter;
    private ArrayList<Notification> notifications;
    private FirebaseFirestore db;

    public EntrantNotificationsFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrant_notifications, container, false);

        recyclerNotifications = view.findViewById(R.id.recycler_notifications);
        progressBar = view.findViewById(R.id.progress_bar);

        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(requireContext(), notifications);
        recyclerNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerNotifications.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadNotifications();

        return view;
    }

    private void loadNotifications() {
        if (!FBAuthenticator.isLoggedIn()) {
            Toast.makeText(getContext(), "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        String userId = FBAuthenticator.getCurrentUser().getUid();
        Log.d("FirestoreDebug", "Current userId: " + userId);

        NotificationDB.getInstance().getNotificationsForUser(userId, task -> {
            progressBar.setVisibility(View.GONE);

            if (task.isSuccessful()) {
                notifications.clear();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Log.d("FirestoreDebug", "Doc found: " + doc.getData());
                    Notification notif = doc.toObject(Notification.class);
                    notif.setId(doc.getId());
                    notifications.add(notif);
                }
                adapter.notifyDataSetChanged();

                if (notifications.isEmpty()) {
                    Toast.makeText(getContext(), "No notifications yet", Toast.LENGTH_SHORT).show();
                }
            } else {
                Exception e = task.getException();
                Log.e("FirestoreDebug", "Error loading notifications", e);
                Toast.makeText(getContext(), "Failed to load notifications", Toast.LENGTH_SHORT).show();
            }
        });
    }
}