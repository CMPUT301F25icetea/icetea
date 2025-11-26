package com.example.icetea.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icetea.R;
import com.example.icetea.models.Waitlist;
import com.example.icetea.models.WaitlistEntry;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class WaitlistAdapter extends RecyclerView.Adapter<WaitlistAdapter.WaitlistViewHolder> {

    private final Context context;
    private final List<WaitlistEntry> entries;

    public WaitlistAdapter(Context context, List<WaitlistEntry> entries) {
        this.context = context;
        this.entries = entries;
    }

    @NonNull
    @Override
    public WaitlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_waitlist_card, parent, false);
        return new WaitlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WaitlistViewHolder holder, int position) {
        WaitlistEntry entry = entries.get(position);

        holder.textName.setText(entry.getName());
        holder.textEmail.setText(entry.getEmail());
        holder.textStatus.setText(entry.getStatus());
        if (entry.getAvatarBitmap() != null) {
            holder.imageProfile.setImageBitmap(entry.getAvatarBitmap());
        } else {
            holder.imageProfile.setImageResource(R.drawable.default_avatar);
        }
        if (Waitlist.STATUS_SELECTED.equals(entry.getStatus())) {
            holder.buttonRevoke.setVisibility(View.VISIBLE);
            holder.buttonReplace.setVisibility(View.VISIBLE);
        } else {
            holder.buttonRevoke.setVisibility(View.GONE);
            holder.buttonReplace.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public static class WaitlistViewHolder extends RecyclerView.ViewHolder {

        ImageView imageProfile;
        TextView textName, textEmail, textStatus;
        MaterialButton buttonRevoke, buttonReplace;

        public WaitlistViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.imageProfile);
            textName = itemView.findViewById(R.id.textName);
            textEmail = itemView.findViewById(R.id.textEmail);
            textStatus = itemView.findViewById(R.id.textStatus);
            buttonRevoke = itemView.findViewById(R.id.buttonRevoke);
            buttonReplace = itemView.findViewById(R.id.buttonReplace);
        }
    }
}
