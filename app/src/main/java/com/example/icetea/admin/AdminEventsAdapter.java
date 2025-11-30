package com.example.icetea.admin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icetea.R;
import com.example.icetea.models.Event;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView Adapter for displaying a list of events in the Admin section.
 * Each event is displayed in a card layout with details like name, description, poster image,
 * current entrants, event date, and registration end date.
 */
public class AdminEventsAdapter extends RecyclerView.Adapter<AdminEventsAdapter.EventViewHolder> {

    /**
     * Interface to handle click actions on an event item.
     */
    public interface ActionListener {
        /**
         * Called when an event item is clicked.
         *
         * @param event the Event object associated with the clicked item
         */
        void onEventClick(Event event);
    }

    private final Context context;
    private final List<Event> events;
    private final ActionListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy @ h:mm a", Locale.getDefault());
    private final SimpleDateFormat regDateFormat = new SimpleDateFormat("MMM d, yyyy @ h:mm a", Locale.getDefault());

    /**
     * Constructor for AdminEventsAdapter.
     *
     * @param context  the context in which the adapter is used
     * @param events   the list of Event objects to display
     * @param listener the listener to handle event clicks
     */
    public AdminEventsAdapter(Context context, List<Event> events, ActionListener listener) {
        this.context = context;
        this.events = events;
        this.listener = listener;
    }

    /**
     * Inflates the item layout and creates the ViewHolder.
     *
     * @param parent   the parent ViewGroup
     * @param viewType the view type of the new View
     * @return a new EventViewHolder instance
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event_card, parent, false);
        return new EventViewHolder(view);
    }

    /**
     * Binds the event data to the ViewHolder.
     *
     * @param holder   the EventViewHolder to bind data to
     * @param position the position of the item in the dataset
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);

        holder.textEventName.setText(event.getName());
        holder.textEventDescription.setText(event.getDescription());
        holder.textCurrentEntrants.setText("👤 " + (event.getCurrentEntrants() != null ? event.getCurrentEntrants() : 0));

        // Format dates
        if (event.getEventStartDate() != null) {
            holder.textEventDate.setText(dateFormat.format(event.getEventStartDate().toDate()));
        } else {
            holder.textEventDate.setText("Date TBD");
        }

        if (event.getRegistrationEndDate() != null) {
            holder.textRegEnd.setText("Register by " + regDateFormat.format(event.getRegistrationEndDate().toDate()));
        } else {
            holder.textRegEnd.setText("Registration end TBD");
        }

        // Decode Base64 poster image
        Bitmap posterBitmap = null;
        if (event.getPosterBase64() != null && !event.getPosterBase64().isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(event.getPosterBase64(), Base64.DEFAULT);
                posterBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            } catch (IllegalArgumentException e) {
                e.printStackTrace(); // Could log this properly in production
            }
        }

        // Set image or default
        if (posterBitmap != null) {
            holder.imageEventPoster.setImageBitmap(posterBitmap);
        } else {
            holder.imageEventPoster.setImageResource(R.drawable.default_poster);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> listener.onEventClick(event));
    }

    /**
     * Returns the number of items in the dataset.
     *
     * @return the size of the events list
     */
    @Override
    public int getItemCount() {
        return events.size();
    }

    /**
     * ViewHolder class for holding and caching views for each event item.
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imageEventPoster;
        TextView textEventName, textEventDescription, textCurrentEntrants, textEventDate, textRegEnd;

        /**
         * Constructor for EventViewHolder.
         *
         * @param itemView the root view of the event item layout
         */
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            imageEventPoster = itemView.findViewById(R.id.imageEventPoster);
            textEventName = itemView.findViewById(R.id.textEventName);
            textEventDescription = itemView.findViewById(R.id.textEventDescription);
            textCurrentEntrants = itemView.findViewById(R.id.textCurrentEntrants);
            textEventDate = itemView.findViewById(R.id.textEventDate);
            textRegEnd = itemView.findViewById(R.id.textRegEnd);
        }
    }
}
