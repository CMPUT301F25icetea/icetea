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

public class AdminNotificationsAdapter extends RecyclerView.Adapter<AdminNotificationsAdapter.NotificationViewHolder> {

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationItemAdmin item);
    }

    private final List<NotificationItemAdmin> notifications;
    private final OnNotificationClickListener listener;

    public AdminNotificationsAdapter(List<NotificationItemAdmin> notifications, OnNotificationClickListener listener) {
        this.notifications = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_card, parent, false);
        return new NotificationViewHolder(view);
    }

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

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, time;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textNotificationTitle);
            message = itemView.findViewById(R.id.textNotificationMessage);
            time = itemView.findViewById(R.id.textNotificationTime);
        }
    }
}
