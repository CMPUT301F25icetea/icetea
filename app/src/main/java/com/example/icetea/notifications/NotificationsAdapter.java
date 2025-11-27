package com.example.icetea.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icetea.R;
import com.example.icetea.models.Notification;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {

    private final OnNotificationClickListener listener;
    private List<Notification> notifications = new ArrayList<>();

    public interface OnNotificationClickListener {
        void onNotificationClick(String eventId);
    }
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

    public void updateList(List<Notification> newList) {
        notifications.clear();
        if (newList != null) notifications.addAll(newList);
        notifyDataSetChanged();
    }
    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, timestamp;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textNotificationTitle);
            message = itemView.findViewById(R.id.textNotificationMessage);
            timestamp = itemView.findViewById(R.id.textNotificationTime);
        }
    }
}
