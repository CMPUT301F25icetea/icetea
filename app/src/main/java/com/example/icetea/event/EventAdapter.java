package com.example.icetea.event;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.icetea.R;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Event event);
    }

    private final List<Event> events;
    private final OnItemClickListener listener;

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

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameText;
        private final TextView descriptionText;
        private final TextView startDateText;
        private final TextView registrationEndText;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.eventName);
            descriptionText = itemView.findViewById(R.id.eventDescription);
            startDateText = itemView.findViewById(R.id.eventStart);
            registrationEndText = itemView.findViewById(R.id.eventRegistrationEnd);
        }

        public void bind(Event event, OnItemClickListener listener) {
            nameText.setText(event.getName());
            descriptionText.setText(event.getDescription());
            startDateText.setText("Event starts on: " + Event.formatTimestampHumanReadable(event.getStartDate()));
            registrationEndText.setText("Registration closes on: " +Event.formatTimestampHumanReadable(event.getStartDate()));

            itemView.setOnClickListener(v -> listener.onItemClick(event));
        }
    }
}
