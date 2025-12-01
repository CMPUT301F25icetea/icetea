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
import com.example.icetea.models.Event;
import com.example.icetea.util.ImageUtil;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView adapter for displaying a list of {@link Event} objects
 * in a card-style layout. Each item displays event details and a poster image.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    /** The list of events to display. */
    private final List<Event> events;

    /** Callback for handling click events on list items. */
    private final OnItemClickListener listener;

    /**
     * Listener interface for handling clicks on an event card.
     */
    public interface OnItemClickListener {
        /**
         * Called when an event item is clicked.
         *
         * @param event the clicked event
         */
        void onItemClick(Event event);
    }

    /**
     * Constructs a new {@link EventAdapter}.
     *
     * @param events   the list of events to display
     * @param listener callback invoked when an event item is clicked
     */
    public EventAdapter(List<Event> events, OnItemClickListener listener) {
        this.events = events;
        this.listener = listener;
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

        holder.textEventName.setText(event.getName());
        holder.textEventDescription.setText(event.getDescription());

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

        int entrants = event.getCurrentEntrants() != null ? event.getCurrentEntrants() : 0;
        holder.textCurrentEntrants.setText("👤 " + entrants);

        Bitmap posterBitmap = ImageUtil.base64ToBitmap(event.getPosterBase64());
        if (posterBitmap != null) {
            holder.imageEventPoster.setImageBitmap(posterBitmap);
        } else {
            holder.imageEventPoster.setImageResource(R.drawable.default_poster);
        }

        holder.bind(event, listener);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    /**
     * ViewHolder class for representing each event card item.
     * Holds references to the views within the card layout.
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {

        ImageView imageEventPoster;
        TextView textEventName, textEventDescription, textEventDate, textEventLocation, textRegEnd, textCurrentEntrants;

        /**
         * Creates a new ViewHolder instance.
         *
         * @param itemView the inflated event card view
         */
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            imageEventPoster = itemView.findViewById(R.id.imageEventPoster);
            textEventName = itemView.findViewById(R.id.textEventName);
            textEventDescription = itemView.findViewById(R.id.textEventDescription);
            textEventDate = itemView.findViewById(R.id.textEventDate);
            textCurrentEntrants = itemView.findViewById(R.id.textCurrentEntrants);
            textRegEnd = itemView.findViewById(R.id.textRegEnd);
        }

        /**
         * Binds an event object to this ViewHolder and attaches a click listener.
         *
         * @param event    the event data to bind
         * @param listener callback for click events
         */
        public void bind(final Event event, final OnItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(event));
        }
    }
}