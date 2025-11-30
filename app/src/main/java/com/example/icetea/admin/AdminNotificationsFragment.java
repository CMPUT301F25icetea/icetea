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

import com.example.icetea.R;
import com.example.icetea.models.NotificationItemAdmin;
import com.example.icetea.models.NotificationsLogDB;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class AdminNotificationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdminNotificationsAdapter adapter;
    private List<NotificationItemAdmin> notifications;

    public AdminNotificationsFragment() { }

    public static AdminNotificationsFragment newInstance() {
        return new AdminNotificationsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerAdminNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        notifications = new ArrayList<>();
        adapter = new AdminNotificationsAdapter(notifications, this::openNotificationDetails);
        recyclerView.setAdapter(adapter);

        loadNotifications();
    }

    private void loadNotifications() {
        NotificationsLogDB.getInstance().getAllNotifications(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                notifications.clear();
                for (var doc : task.getResult().getDocuments()) {
                    String eventId = doc.getString("eventId");
                    String title = doc.getString("title");
                    String message = doc.getString("message");

                    List<?> rawRecipients = (List<?>) doc.get("recipients");
                    List<String> recipients = new ArrayList<>();
                    if (rawRecipients != null) {
                        for (Object r : rawRecipients) {
                            recipients.add(r.toString());
                        }
                    }

                    List<?> rawStatuses = (List<?>) doc.get("statuses");
                    List<String> statuses = new ArrayList<>();
                    if (rawStatuses != null) {
                        for (Object s : rawStatuses) {
                            statuses.add(s.toString());
                        }
                    }

                    Timestamp timestamp = doc.getTimestamp("timestamp");

                    notifications.add(new NotificationItemAdmin(eventId, title, message,
                            recipients, statuses, timestamp));
                }
                adapter.notifyDataSetChanged();
            }
        });
    }


    private void openNotificationDetails(NotificationItemAdmin item) {
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setReorderingAllowed(true);
        transaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
        );

        transaction.replace(R.id.main_fragment_container,
                NotificationDetailsFragment.newInstance(
                        item.getEventId(),
                        item.getTitle(),
                        item.getMessage(),
                        item.getRecipients(),
                        item.getStatuses(),
                        item.getTimestamp()
                ));
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
