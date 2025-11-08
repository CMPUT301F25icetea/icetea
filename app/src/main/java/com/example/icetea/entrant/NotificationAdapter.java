package com.example.icetea.entrant;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;

import com.example.icetea.R;

import java.util.ArrayList;
import java.util.Date;

/**
 * Adapter for displaying a list of notifications in a RecyclerView.
 * This adapter binds Notification objects to their respective views, formatting
 * the timestamp and applying visual styling (icons and colors) based on the
 * notification type ("won", "lost", or "replacement"). For "won" notifications
 * that are pending, it displays Accept and Decline buttons THAT CAN BE CLICKED AND THE STATUS WOULD BE
 *UPDATED IN NOTIFICATION COLLECTION IN FIRESTORE DB
 * @author avyaan
 * @version 1.0
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Notification> notifications;

    /**
     * Constructs a NotificationAdapter with the given context and notification list.
     *
     * @param context the context used for inflating layouts and accessing resources
     * @param notifications the ArrayList of Notification objects to display
     */
    public NotificationAdapter(Context context, ArrayList<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    /**
     * Creates a new ViewHolder instance when the RecyclerView needs a new view.
     *
     * @param parent the parent ViewGroup
     * @param viewType the view type (not used in this adapter)
     * @return a new ViewHolder instance
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds a Notification to the given ViewHolder at the specified position.
     *
     * This method sets the event name, message, and formatted timestamp text,
     * and applies styling (icon and text color) based on the notification type.
     * For "won" notifications that are pending, it shows Accept/Decline buttons.
     *
     * @param holder the ViewHolder to bind data to
     * @param position the position of the item in the notifications list
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        holder.tvEventName.setText(notification.getEventName());
        holder.tvMessage.setText(notification.getMessage());

        // Format timestamp to readable string
        String time = DateFormat.format("MMM dd, yyyy hh:mm a", new Date(notification.getTimestampMillis())).toString();
        holder.tvTimestamp.setText(time);

        // Show/hide action buttons based on notification type and status
        if (notification.canRespond()) {
            holder.btnAccept.setVisibility(View.VISIBLE);
            holder.btnDecline.setVisibility(View.VISIBLE);
            holder.tvStatus.setVisibility(View.GONE);

            // Accept button click
            holder.btnAccept.setOnClickListener(v -> {
                handleAccept(notification, holder);
            });

            // Decline button click
            holder.btnDecline.setOnClickListener(v -> {
                handleDecline(notification, holder);
            });
        } else {
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnDecline.setVisibility(View.GONE);

            // Show status if already responded
            if (notification.isWonNotification() && !notification.isPending()) {
                holder.tvStatus.setVisibility(View.VISIBLE);
                if (notification.isAccepted()) {
                    holder.tvStatus.setText("✓ Accepted");
                    holder.tvStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
                } else if (notification.isDeclined()) {
                    holder.tvStatus.setText("✗ Declined");
                    holder.tvStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
                }
            } else {
                holder.tvStatus.setVisibility(View.GONE);
            }
        }

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

    /**
     * Handles accepting an invitation.
     *
     * @param notification the notification to accept
     * @param holder the ViewHolder for UI updates
     */
    private void handleAccept(Notification notification, ViewHolder holder) {
        // Disable buttons to prevent multiple clicks
        holder.btnAccept.setEnabled(false);
        holder.btnDecline.setEnabled(false);

        NotificationDB.getInstance().updateNotificationStatus(notification.getId(), "accepted", task -> {
            if (task.isSuccessful()) {
                notification.setStatus("accepted");
                notifyItemChanged(notifications.indexOf(notification));
                Toast.makeText(context, "Invitation accepted!", Toast.LENGTH_SHORT).show();
            } else {
                holder.btnAccept.setEnabled(true);
                holder.btnDecline.setEnabled(true);
                Toast.makeText(context, "Failed to accept invitation", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Handles declining an invitation.
     *
     * @param notification the notification to decline
     * @param holder the ViewHolder for UI updates
     */
    private void handleDecline(Notification notification, ViewHolder holder) {
        // Disable buttons to prevent multiple clicks
        holder.btnAccept.setEnabled(false);
        holder.btnDecline.setEnabled(false);

        NotificationDB.getInstance().updateNotificationStatus(notification.getId(), "declined", task -> {
            if (task.isSuccessful()) {
                notification.setStatus("declined");
                notifyItemChanged(notifications.indexOf(notification));
                Toast.makeText(context, "Invitation declined", Toast.LENGTH_SHORT).show();
            } else {
                holder.btnAccept.setEnabled(true);
                holder.btnDecline.setEnabled(true);
                Toast.makeText(context, "Failed to decline invitation", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Gets the total number of notifications in the list.
     *
     * @return the number of notifications, or 0 if the list is null
     */
    @Override
    public int getItemCount() {
        return notifications != null ? notifications.size() : 0;
    }

    /**
     * ViewHolder for holding references to the views in a notification item.
     *
     * This inner class caches the views for each notification item to avoid
     * repeated findViewById calls.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvEventName, tvMessage, tvTimestamp, tvStatus;
        Button btnAccept, btnDecline;

        /**
         * Constructs a ViewHolder and initializes its view references.
         *
         * @param itemView the root view of the notification item layout
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_notification_icon);
            tvEventName = itemView.findViewById(R.id.tv_notification_event_name);
            tvMessage = itemView.findViewById(R.id.tv_notification_message);
            tvTimestamp = itemView.findViewById(R.id.tv_notification_timestamp);
            tvStatus = itemView.findViewById(R.id.tv_notification_status);
            btnAccept = itemView.findViewById(R.id.btn_accept);
            btnDecline = itemView.findViewById(R.id.btn_decline);
        }
    }
}