package com.example.icetea.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icetea.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying a waiting list of users for an event in a RecyclerView.
 * Handles selection of users via checkboxes and revoking invitations for invited users.
 */
public class WaitingListAdapter extends RecyclerView.Adapter<WaitingListAdapter.WaitingListViewHolder> {

    private final List<WaitingListEntry> itemList;

    /**
     * Constructs a WaitingListAdapter with a given list of entries.
     *
     * @param itemList List of WaitingListEntry items to display
     */
    public WaitingListAdapter(List<WaitingListEntry> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public WaitingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.waiting_list_item, parent, false);
        return new WaitingListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WaitingListViewHolder holder, int position) {
        WaitingListEntry item = itemList.get(position);

        // Bind user info to UI elements
        holder.userEmail.setText(item.getEmail());
        holder.userStatus.setText(item.getStatus());
        holder.userJoinTime.setText(item.getJoinTime());

        // Setup checkbox state and listener
        holder.selectUserCheckBox.setOnCheckedChangeListener(null); // Reset listener to avoid unwanted triggers
        holder.selectUserCheckBox.setChecked(item.isSelected());
        holder.selectUserCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> item.setSelected(isChecked));

        // Only show cancel button for invited users
        if (!item.getStatus().equals("invited")) {
            holder.cancelEntrant.setVisibility(View.GONE);
        }

        // Handle cancel button click
        holder.cancelEntrant.setOnClickListener(v -> {
            WaitlistDB.getInstance().updateWaitlistStatus(item.getEventId(), item.getUserId(), "cancelled", task -> {
                if (task.isSuccessful()) {
                    holder.cancelEntrant.setVisibility(View.GONE);
                    item.setStatus("cancelled");
                    int pos = holder.getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        notifyItemChanged(pos);
                    }
                } else {
                    Toast.makeText(v.getContext(), "Error revoking invitation", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    /**
     * Returns the list of currently selected items from the adapter.
     *
     * @return List of selected WaitingListEntry items
     */
    public List<WaitingListEntry> getSelectedItems() {
        List<WaitingListEntry> selected = new ArrayList<>();
        for (WaitingListEntry item : itemList) {
            if (item.isSelected()) {
                selected.add(item);
            }
        }
        return selected;
    }

    /**
     * ViewHolder for displaying a single waiting list entry.
     * Contains TextViews for user email, status, join time, a CheckBox for selection, and a cancel button.
     */
    public static class WaitingListViewHolder extends RecyclerView.ViewHolder {
        TextView userEmail, userStatus, userJoinTime;
        CheckBox selectUserCheckBox;
        Button cancelEntrant;

        public WaitingListViewHolder(@NonNull View itemView) {
            super(itemView);
            userEmail = itemView.findViewById(R.id.userEmail);
            userStatus = itemView.findViewById(R.id.userStatus);
            userJoinTime = itemView.findViewById(R.id.userJoinTime);
            selectUserCheckBox = itemView.findViewById(R.id.selectUserCheckBox);
            cancelEntrant = itemView.findViewById(R.id.buttonCancelEntrant);
        }
    }
}
