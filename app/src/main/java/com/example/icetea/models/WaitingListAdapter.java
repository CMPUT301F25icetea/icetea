package com.example.icetea.models;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icetea.R;

import java.util.ArrayList;
import java.util.List;

public class WaitingListAdapter extends RecyclerView.Adapter<WaitingListAdapter.WaitingListViewHolder> {

    private List<WaitingListEntry> itemList;

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

        holder.userEmail.setText(item.getEmail());
        holder.userStatus.setText(item.getStatus());
        holder.userJoinTime.setText(item.getJoinTime());

        holder.selectUserCheckBox.setOnCheckedChangeListener(null); //necessary
        holder.selectUserCheckBox.setChecked(item.isSelected());
        holder.selectUserCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSelected(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public List<WaitingListEntry> getSelectedItems() {
        List<WaitingListEntry> selected = new ArrayList<>();
        for (WaitingListEntry item : itemList) {
            if (item.isSelected()) {
                selected.add(item);
            }
        }
        return selected;
    }

    public static class WaitingListViewHolder extends RecyclerView.ViewHolder {
        TextView userEmail, userStatus, userJoinTime;
        CheckBox selectUserCheckBox;

        public WaitingListViewHolder(@NonNull View itemView) {
            super(itemView);
            userEmail = itemView.findViewById(R.id.userEmail);
            userStatus = itemView.findViewById(R.id.userStatus);
            userJoinTime = itemView.findViewById(R.id.userJoinTime);
            selectUserCheckBox = itemView.findViewById(R.id.selectUserCheckBox);
        }
    }
}