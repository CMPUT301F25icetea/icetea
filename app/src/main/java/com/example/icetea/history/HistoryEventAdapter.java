package com.example.icetea.history;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icetea.R;
import com.example.icetea.models.Event;
import com.example.icetea.models.Waitlist;
import com.example.icetea.util.ImageUtil;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HistoryEventAdapter extends RecyclerView.Adapter<HistoryEventAdapter.HistoryEventViewHolder> {

    private final List<HistoryEventItem> eventItems;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(HistoryEventItem item);
    }

    public HistoryEventAdapter(List<HistoryEventItem> eventItems, OnItemClickListener listener) {
        this.eventItems = eventItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HistoryEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_event_card, parent, false);
        return new HistoryEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryEventViewHolder holder, int position) {
        HistoryEventItem item = eventItems.get(position);
        Event event = item.getEvent();
        String status = item.getWaitlistStatus();

        holder.textEventName.setText(event.getName());
        holder.textEventDescription.setText(event.getDescription());
        holder.textEventLocation.setText(event.getLocation());

        if (event.getEventStartDate() != null) {
            holder.textEventDate.setText(
                    new SimpleDateFormat("MMM d, yyyy @ h:mm a", Locale.getDefault())
                            .format(event.getEventStartDate().toDate())
            );
        }

        // Set status label with appropriate styling
        setStatusLabel(holder, status);

        Bitmap posterBitmap = ImageUtil.base64ToBitmap(event.getPosterBase64());
        if (posterBitmap != null) {
            holder.imageEventPoster.setImageBitmap(posterBitmap);
        } else {
            holder.imageEventPoster.setImageResource(R.drawable.default_poster);
        }

        holder.bind(item, listener);
    }

    private void setStatusLabel(HistoryEventViewHolder holder, String status) {
        if (status == null) {
            holder.textStatus.setVisibility(View.GONE);
            return;
        }

        holder.textStatus.setVisibility(View.VISIBLE);

        switch (status) {
            case Waitlist.STATUS_WAITING:
                holder.textStatus.setText("Status: Waiting");
                holder.textStatus.setTextColor(holder.itemView.getContext()
                        .getResources().getColor(android.R.color.holo_orange_dark));
                holder.textStatus.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_info, 0, 0, 0);
                break;

            case Waitlist.STATUS_SELECTED:
                holder.textStatus.setText("Status: Selected");
                holder.textStatus.setTextColor(holder.itemView.getContext()
                        .getResources().getColor(android.R.color.holo_blue_dark));
                holder.textStatus.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_check_circle, 0, 0, 0);
                break;

            case Waitlist.STATUS_ACCEPTED:
                holder.textStatus.setText("Status: Accepted");
                holder.textStatus.setTextColor(holder.itemView.getContext()
                        .getResources().getColor(android.R.color.holo_green_dark));
                holder.textStatus.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_check_circle, 0, 0, 0);
                break;

            case Waitlist.STATUS_DECLINED:
                holder.textStatus.setText("Status: Declined");
                holder.textStatus.setTextColor(holder.itemView.getContext()
                        .getResources().getColor(android.R.color.holo_red_dark));
                holder.textStatus.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_cancel, 0, 0, 0);
                break;

            case Waitlist.STATUS_CANCELLED:
                holder.textStatus.setText("Status: Cancelled");
                holder.textStatus.setTextColor(holder.itemView.getContext()
                        .getResources().getColor(android.R.color.holo_red_dark));
                holder.textStatus.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_cancel, 0, 0, 0);
                break;

            default:
                holder.textStatus.setText("Status: " + status);
                holder.textStatus.setTextColor(holder.itemView.getContext()
                        .getResources().getColor(android.R.color.darker_gray));
                holder.textStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return eventItems.size();
    }

    public static class HistoryEventViewHolder extends RecyclerView.ViewHolder {
        ImageView imageEventPoster;
        TextView textEventName, textEventDescription, textEventDate, textEventLocation, textStatus;

        public HistoryEventViewHolder(@NonNull View itemView) {
            super(itemView);
            imageEventPoster = itemView.findViewById(R.id.imageEventPoster);
            textEventName = itemView.findViewById(R.id.textEventName);
            textEventDescription = itemView.findViewById(R.id.textEventDescription);
            textEventDate = itemView.findViewById(R.id.textEventDate);
            textEventLocation = itemView.findViewById(R.id.textEventLocation);
            textStatus = itemView.findViewById(R.id.textEventStatus);
        }

        public void bind(final HistoryEventItem item, final OnItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}