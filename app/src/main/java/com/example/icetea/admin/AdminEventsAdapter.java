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

public class AdminEventsAdapter extends RecyclerView.Adapter<AdminEventsAdapter.EventViewHolder> {

    public interface ActionListener {
        void onEventClick(Event event);
    }

    private final Context context;
    private final List<Event> events;
    private final ActionListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy @ h:mm a", Locale.getDefault());
    private final SimpleDateFormat regDateFormat = new SimpleDateFormat("MMM d, yyyy @ h:mm a", Locale.getDefault());

    public AdminEventsAdapter(Context context, List<Event> events, ActionListener listener) {
        this.context = context;
        this.events = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event_card, parent, false);
        return new EventViewHolder(view);
    }

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

        Bitmap posterBitmap = null;
        if (event.getPosterBase64() != null && !event.getPosterBase64().isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(event.getPosterBase64(), Base64.DEFAULT);
                posterBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        if (posterBitmap != null) {
            holder.imageEventPoster.setImageBitmap(posterBitmap);
        } else {
            holder.imageEventPoster.setImageResource(R.drawable.default_poster);
        }

        holder.itemView.setOnClickListener(v -> listener.onEventClick(event));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imageEventPoster;
        TextView textEventName, textEventDescription, textCurrentEntrants, textEventDate, textRegEnd;

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
