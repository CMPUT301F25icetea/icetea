package com.example.icetea.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icetea.R;
import com.example.icetea.models.NotificationItemAdmin;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView Adapter for displaying notifications in the Admin section.
 * Each notification is displayed as a card with title, message, and timestamp.
 */
public class AdminNotificationsAdapter extends RecyclerView.Adapter<AdminNotificationsAdapter.NotificationViewHolder> {

    /**
     * Interface to handle clicks on notification items.
     */
    public interface OnNotificationClickListener {
        /**
         * Called when a notification item is clicked.
         *
         * @param item the NotificationItemAdmin associated with the clicked item
         */
        void onNotificationClick(NotificationItemAdmin item);
    }

    private final List<NotificationItemAdmin> notifications;
    private final OnNotificationClickListener listener;

    /**
     * Constructor for AdminNotificationsAdapter.
     *
     * @param notifications the list of notifications to display
     * @param listener      the listener to handle click events
     */
    public AdminNotificationsAdapter(List<NotificationItemAdmin> notifications, OnNotificationClickListener listener) {
        this.notifications = notifications;
        this.listener = listener;
    }

    /**
     * Inflates the item layout and creates the ViewHolder.
     *
     * @param parent   the parent ViewGroup
     * @param viewType the view type of the new View
     * @return a new NotificationViewHolder instance
     */
    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_card, parent, false);
        return new NotificationViewHolder(view);
    }

    /**
     * Binds the notification data to the ViewHolder.
     *
     * @param holder   the NotificationViewHolder to bind data to
     * @param position the position of the item in the dataset
     */
    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItemAdmin item = notifications.get(position);

        holder.title.setText(item.getTitle());
        holder.message.setText(item.getMessage());

        Timestamp ts = item.getTimestamp();
        if (ts != null) {
            Date date = ts.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
            holder.time.setText(sdf.format(date));
        } else {
            holder.time.setText("");
        }

        holder.itemView.setOnClickListener(v -> listener.onNotificationClick(item));
    }

    /**
     * Returns the number of items in the dataset.
     *
     * @return the size of the notifications list
     */
    @Override
    public int getItemCount() {
        return notifications.size();
    }

    /**
     * ViewHolder class for holding and caching views for each notification item.
     */
    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, time;

        /**
         * Constructor for NotificationViewHolder.
         *
         * @param itemView the root view of the notification item layout
         */
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textNotificationTitle);
            message = itemView.findViewById(R.id.textNotificationMessage);
            time = itemView.findViewById(R.id.textNotificationTime);
        }
    }
}
