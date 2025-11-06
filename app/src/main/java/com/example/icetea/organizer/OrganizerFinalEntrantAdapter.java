package com.example.icetea.organizer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icetea.R;
import com.example.icetea.models.Waitlist;

import java.util.List;

/**
 * Adapter that displays each entrant in the final list.
 */
public class OrganizerFinalEntrantAdapter extends RecyclerView.Adapter<OrganizerFinalEntrantAdapter.ViewHolder> {

    private final List<Waitlist> entrants;

    public OrganizerFinalEntrantAdapter(List<Waitlist> entrants) {
        this.entrants = entrants;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.final_entrant_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Waitlist entrant = entrants.get(position);

        holder.userIdText.setText("User ID: " + entrant.getUserId());
        holder.joinedAtText.setText("Joined: " + entrant.getFormattedJoinedAt());
        holder.statusText.setText("Status: " + entrant.getStatus());
    }

    @Override
    public int getItemCount() {
        return entrants.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userIdText, joinedAtText, statusText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userIdText = itemView.findViewById(R.id.userIdText);
            joinedAtText = itemView.findViewById(R.id.joinedAtText);
            statusText = itemView.findViewById(R.id.statusText);
        }
    }
}
