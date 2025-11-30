package com.example.icetea.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icetea.R;
import com.example.icetea.models.Notification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView Adapter for displaying a list of {@link Notification} objects.
 *
 * <p>Each notification displays a title, message, and timestamp. Clicking on a notification
 * triggers the {@link OnNotificationClickListener} callback with the associated event ID.</p>
 */
public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {

    private final OnNotificationClickListener listener;
    private List<Notification> notifications = new ArrayList<>();

    /**
     * Interface for handling notification click events.
     */
    public interface OnNotificationClickListener {
        /**
         * Called when a notification is clicked.
         *
         * @param eventId The ID of the event associated with the notification.
         */
        void onNotificationClick(String eventId);
    }

    /**
     * Constructs the adapter with an initial list of notifications and a click listener.
     *
     * @param initialList Initial list of notifications; can be null.
     * @param listener    Listener to handle notification click events.
     */
    public NotificationsAdapter(List<Notification> initialList, OnNotificationClickListener listener) {
        if (initialList != null) {
            notifications.addAll(initialList);
        }
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification_card, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification n = notifications.get(position);
        holder.title.setText(n.getTitle());
        holder.message.setText(n.getMessage());
        if (n.getTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy hh:mm a", Locale.getDefault());
            holder.timestamp.setText(sdf.format(n.getTimestamp().toDate()));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null && n.getEventId() != null) {
                listener.onNotificationClick(n.getEventId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    /**
     * Updates the adapter's list of notifications and refreshes the RecyclerView.
     *
     * @param newList New list of notifications; can be null.
     */
    public void updateList(List<Notification> newList) {
        notifications.clear();
        if (newList != null) notifications.addAll(newList);
        notifyDataSetChanged();
    }

    /**
     * ViewHolder for a single notification item.
     *
     * <p>Displays the title, message, and timestamp.</p>
     */
    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, timestamp;

        /**
         * Constructs the ViewHolder and binds the UI elements.
         *
         * @param itemView Root view of the notification item.
         */
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textNotificationTitle);
            message = itemView.findViewById(R.id.textNotificationMessage);
            timestamp = itemView.findViewById(R.id.textNotificationTime);
        }
    }
}