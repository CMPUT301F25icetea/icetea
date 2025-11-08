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
 * Adapter that displays the list of final entrants in a RecyclerView.
 */
public class OrganizerFinalEntrantAdapter extends RecyclerView.Adapter<OrganizerFinalEntrantAdapter.ViewHolder> {

    private final List<Waitlist> entrants;

    /**
     * Constructs a new {@code OrganizerFinalEntrantAdapter}.
     *
     * @param entrants the list of waitlist entrants to display
     */
    public OrganizerFinalEntrantAdapter(List<Waitlist> entrants) {
        this.entrants = entrants;
    }

    /**
     * get the layout for a single entrant item in the RecyclerView.
     *
     * @param parent   the parent view that the new view will be attached to
     * @param viewType the view type of the new view
     * @return a new {@link ViewHolder} instance containing the inflated view
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.final_entrant_item, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Binds entrant data to the item view for a given position in the list.
     *
     * @param holder   the {@link ViewHolder} to bind data to
     * @param position the position of the entrant item in the dataset
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Waitlist entrant = entrants.get(position);

        holder.userIdText.setText("User ID: " + entrant.getUserId());
        holder.joinedAtText.setText("Joined: " + entrant.getFormattedJoinedAt());
        holder.statusText.setText("Status: " + entrant.getStatus());
    }

    /**
     * Returns the total number of entrants in the list.
     *
     * @return the number of items in the dataset
     */
    @Override
    public int getItemCount() {
        return entrants.size();
    }

    /**
     * Holds references to the views within each entrant item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userIdText, joinedAtText, statusText;

        /**
         * Constructs a {@code ViewHolder} and binds view references.
         *
         * @param itemView the root view of the item layout
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userIdText = itemView.findViewById(R.id.userIdText);
            joinedAtText = itemView.findViewById(R.id.joinedAtText);
            statusText = itemView.findViewById(R.id.statusText);
        }
    }
}
