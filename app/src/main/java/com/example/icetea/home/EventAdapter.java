package com.example.icetea.home;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icetea.R;
import com.example.icetea.event.Event;
import com.example.icetea.util.ImageUtil;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final List<Event> events;

    public EventAdapter(List<Event> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_card, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.textEventName.setText(event.getEventName());
        holder.textEventDescription.setText(event.getDescription());
        holder.textEventLocation.setText(event.getLocation());

        if (event.getEventStartDate() != null) {
            holder.textEventDate.setText(
                    new SimpleDateFormat("MMM d, yyyy @ h:mm a", Locale.getDefault())
                            .format(event.getEventStartDate().toDate())
            );
        }

        if (event.getRegistrationEndDate() != null) {
            holder.textRegEnd.setText(
                    "Register by " +
                            new SimpleDateFormat("MMM d, yyyy @ h:mm a", Locale.getDefault())
                                    .format(event.getRegistrationEndDate().toDate())
            );
        }

        Bitmap posterBitmap = ImageUtil.base64ToBitmap(event.getPosterBase64());
        if (posterBitmap != null) {
            holder.imageEventPoster.setImageBitmap(posterBitmap);
        } else {
            holder.imageEventPoster.setImageResource(R.drawable.default_poster);
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView imageEventPoster;
        TextView textEventName, textEventDescription, textEventDate, textEventLocation, textRegEnd;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            imageEventPoster = itemView.findViewById(R.id.imageEventPoster);
            textEventName = itemView.findViewById(R.id.textEventName);
            textEventDescription = itemView.findViewById(R.id.textEventDescription);
            textEventDate = itemView.findViewById(R.id.textEventDate);
            textEventLocation = itemView.findViewById(R.id.textEventLocation);
            textRegEnd = itemView.findViewById(R.id.textRegEnd);
        }
    }
}
