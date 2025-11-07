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

/**
 * Fragment for displaying a list of notifications to an entrant user.
 *
 * This fragment loads notifications from Firestore for the currently logged-in user
 * and displays them in a RecyclerView using the NotificationAdapter. It handles
 * user authentication checks, progress indication, and error handling.
 *
 * @author IceTea
 * @version 1.0
 */
public class EntrantNotificationsFragment extends Fragment {

    private RecyclerView recyclerNotifications;
    private ProgressBar progressBar;
    private NotificationAdapter adapter;
    private ArrayList<Notification> notifications;
    private FirebaseFirestore db;

    /**
     * Default constructor required for Fragment instantiation.
     */
    public EntrantNotificationsFragment() {
        // Required empty constructor
    }

    /**
     * Creates and returns the root view for this fragment.
     *
     * Initializes the RecyclerView, adapter, and progress bar, then calls
     * loadNotifications() to fetch data from Firestore.
     *
     * @param inflater the LayoutInflater used to inflate views
     * @param container the parent ViewGroup
     * @param savedInstanceState the Bundle containing saved state (if any)
     * @return the root view of the fragment
     */
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

    /**
     * Loads notifications from Firestore for the currently logged-in user.
     *
     * This method checks if the user is logged in before attempting to fetch data.
     * A progress bar is shown while loading. On success, notifications are added to
     * the adapter and the list is refreshed. On failure, an error message is logged
     * and displayed to the user.
     */
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