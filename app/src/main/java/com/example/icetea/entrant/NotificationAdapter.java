package com.example.icetea.entrant;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;

import com.example.icetea.R;

import java.util.ArrayList;
import java.util.Date;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Notification> notifications;

    public NotificationAdapter(Context context, ArrayList<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        holder.tvEventName.setText(notification.getEventName());
        holder.tvMessage.setText(notification.getMessage());

        // Format timestamp to readable string
        String time = DateFormat.format("MMM dd, yyyy hh:mm a", new Date(notification.getTimestampMillis())).toString();

        holder.tvTimestamp.setText(time);

        // Set icon and text color based on type
        switch (notification.getType()) {
            case "won":
                holder.ivIcon.setImageResource(R.drawable.ic_check_circle);
                holder.tvEventName.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
                break;
            case "lost":
                holder.ivIcon.setImageResource(R.drawable.ic_cancel);
                holder.tvEventName.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
                break;
            case "replacement":
            default:
                holder.ivIcon.setImageResource(R.drawable.ic_info);
                holder.tvEventName.setTextColor(ContextCompat.getColor(context, android.R.color.holo_blue_dark));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return notifications != null ? notifications.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvEventName, tvMessage, tvTimestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_notification_icon);
            tvEventName = itemView.findViewById(R.id.tv_notification_event_name);
            tvMessage = itemView.findViewById(R.id.tv_notification_message);
            tvTimestamp = itemView.findViewById(R.id.tv_notification_timestamp);
        }
    }
}
