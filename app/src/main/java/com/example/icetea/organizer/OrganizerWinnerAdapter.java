package com.example.icetea.organizer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icetea.R;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

/**
 * RecyclerView Adapter for displaying a list of winners in the organizer's view.
 * Each item shows the winner's user ID and status.
 */
public class OrganizerWinnerAdapter extends RecyclerView.Adapter<OrganizerWinnerAdapter.ViewHolder> {

    /** List of DocumentSnapshot objects representing the winners. */
    private List<DocumentSnapshot> winnersList;

    /**
     * Constructs the adapter with the initial list of winners.
     *
     * @param winnersList List of winners as DocumentSnapshots.
     */
    public OrganizerWinnerAdapter(List<DocumentSnapshot> winnersList) {
        this.winnersList = winnersList;
    }

    /**
     * Updates the adapter's data with a new list of winners.
     *
     * @param newData The new list of winners.
     */
    public void setData(List<DocumentSnapshot> newData) {
        this.winnersList = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrganizerWinnerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_organizer_winner_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrganizerWinnerAdapter.ViewHolder holder, int position) {
        DocumentSnapshot doc = winnersList.get(position);
        String userId = doc.getString("userId");
        String status = doc.getString("status");
        holder.userIdText.setText("User ID: " + userId);
        holder.statusText.setText("Status: " + status);
    }

    @Override
    public int getItemCount() {
        return winnersList.size();
    }

    /**
     * ViewHolder class for a single winner item in the RecyclerView.
     * Holds references to the TextViews for user ID and status.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        /** TextView displaying the winner's user ID. */
        TextView userIdText;

        /** TextView displaying the winner's status. */
        TextView statusText;

        /**
         * Constructs a ViewHolder and binds the TextView references.
         *
         * @param itemView The root view of the item layout.
         */
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            userIdText = itemView.findViewById(R.id.textUserId);
            statusText = itemView.findViewById(R.id.textStatus);
        }
    }
}
