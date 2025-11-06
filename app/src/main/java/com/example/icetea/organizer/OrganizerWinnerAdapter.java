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

public class OrganizerWinnerAdapter extends RecyclerView.Adapter<OrganizerWinnerAdapter.ViewHolder> {

    private List<DocumentSnapshot> winnersList;

    public OrganizerWinnerAdapter(List<DocumentSnapshot> winnersList) {
        this.winnersList = winnersList;
    }

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

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userIdText, statusText;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            userIdText = itemView.findViewById(R.id.textUserId);
            statusText = itemView.findViewById(R.id.textStatus);
        }
    }
}

