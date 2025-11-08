package com.example.icetea.event;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.icetea.R;
import java.util.List;

/**
 * RecyclerView Adapter for displaying a list of events.
 *
 * Each event is represented by an EventViewHolder, which shows the event's name,
 * description, start date, and registration end date. Supports click events on items.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    /**
     * Interface to handle clicks on event items.
     */
    public interface OnItemClickListener {
        /**
         * Called when an event item is clicked.
         *
         * @param event The Event object that was clicked
         */
        void onItemClick(Event event);
    }

    private final List<Event> events;
    private final OnItemClickListener listener;

    /**
     * Constructs an EventAdapter.
     *
     * @param events The list of events to display
     * @param listener The click listener for event items
     */
    public EventAdapter(List<Event> events, OnItemClickListener listener) {
        this.events = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_list_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        holder.bind(events.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    /**
     * ViewHolder class for representing a single event item in the RecyclerView.
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameText;
        private final TextView descriptionText;
        private final TextView startDateText;
        private final TextView registrationEndText;

        /**
         * Constructs an EventViewHolder and initializes its views.
         *
         * @param itemView The view representing a single item
         */
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.eventName);
            descriptionText = itemView.findViewById(R.id.eventDescription);
            startDateText = itemView.findViewById(R.id.eventStart);
            registrationEndText = itemView.findViewById(R.id.eventRegistrationEnd);
        }

        /**
         * Binds an Event object to the ViewHolder, populating the views and setting the click listener.
         *
         * @param event The event to display
         * @param listener The click listener to invoke when the item is clicked
         */
        public void bind(Event event, OnItemClickListener listener) {
            nameText.setText(event.getName());
            descriptionText.setText(event.getDescription());
            startDateText.setText("Event starts on: " + Event.formatTimestampHumanReadable(event.getStartDate()));
            registrationEndText.setText("Registration closes on: " + Event.formatTimestampHumanReadable(event.getStartDate()));

            itemView.setOnClickListener(v -> listener.onItemClick(event));
        }
    }
}
