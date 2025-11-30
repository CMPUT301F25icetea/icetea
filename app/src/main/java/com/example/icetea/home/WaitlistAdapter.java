package com.example.icetea.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icetea.R;
import com.example.icetea.models.UserDB;
import com.example.icetea.models.Waitlist;
import com.example.icetea.util.ImageUtil;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter for displaying waitlist entries in a RecyclerView.
 * Handles displaying user information, status, and actions such as replacing or revoking winners.
 */
public class WaitlistAdapter extends RecyclerView.Adapter<WaitlistAdapter.WaitlistViewHolder> {

    /**
     * Interface to handle actions performed on waitlist entries.
     */
    public interface ActionListener {
        /**
         * Called when the "Replace" button is clicked for a waitlist entry.
         *
         * @param entry The waitlist entry to replace
         */
        void onReplace(Waitlist entry);

        /**
         * Called when the "Revoke" button is clicked for a waitlist entry.
         *
         * @param entry The waitlist entry to revoke
         */
        void onRevoke(Waitlist entry);
    }

    /** Listener to handle entry actions */
    private final ActionListener listener;

    /** Context for inflating views */
    private final Context context;

    /** List of waitlist entries */
    private final List<Waitlist> entries;

    /** Cache to store user data for faster display */
    private final Map<String, UserData> userCache = new HashMap<>();

    /**
     * Constructor for the adapter.
     *
     * @param context Context for view inflation
     * @param entries List of waitlist entries to display
     * @param listener ActionListener to handle replace/revoke actions
     */
    public WaitlistAdapter(Context context, List<Waitlist> entries, ActionListener listener) {
        this.context = context;
        this.entries = entries;
        this.listener = listener;
    }

    /**
     * Inflates the item layout and creates a ViewHolder.
     *
     * @param parent   The parent ViewGroup
     * @param viewType The view type of the new View
     * @return A new WaitlistViewHolder instance
     */
    @NonNull
    @Override
    public WaitlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_waitlist_card, parent, false);
        return new WaitlistViewHolder(view);
    }

    /**
     * Binds waitlist entry data to the ViewHolder.
     * Handles displaying user info, status, and button visibility/behavior.
     *
     * @param holder   The ViewHolder to bind data to
     * @param position The position of the entry in the list
     */
    @Override
    public void onBindViewHolder(@NonNull WaitlistViewHolder holder, int position) {
        Waitlist entry = entries.get(position);

        // Initialize default display
        holder.textName.setText("Loading...");
        holder.textEmail.setText("");
        holder.imageProfile.setImageResource(R.drawable.default_avatar);
        holder.textStatus.setText("Status: " + entry.getStatus());

        // Update button visibility and state based on entry status
        if (Waitlist.STATUS_SELECTED.equals(entry.getStatus())) {
            holder.buttonRevoke.setVisibility(View.VISIBLE);
            holder.buttonReplace.setVisibility(View.VISIBLE);
            holder.buttonReplace.setEnabled(true);
            holder.buttonReplace.setAlpha(1f);
            holder.buttonReplace.setText("Replace");

        } else if (Waitlist.STATUS_DECLINED.equals(entry.getStatus())) {
            holder.buttonRevoke.setVisibility(View.GONE);
            holder.buttonReplace.setVisibility(View.VISIBLE);
            if (entry.getReplaced()) {
                holder.buttonReplace.setText("Replaced");
                holder.buttonReplace.setEnabled(false);
                holder.buttonReplace.setAlpha(0.5f);
            } else {
                holder.buttonReplace.setText("Replace");
                holder.buttonReplace.setEnabled(true);
                holder.buttonReplace.setAlpha(1f);
            }

        } else {
            holder.buttonRevoke.setVisibility(View.GONE);
            holder.buttonReplace.setVisibility(View.GONE);
        }

        // Load user data from cache or Firestore
        if (userCache.containsKey(entry.getUserId())) {
            UserData cached = userCache.getOrDefault(entry.getUserId(), null);
            if (cached != null) {
                holder.textName.setText(cached.name);
                holder.textEmail.setText(cached.email);
                if (cached.avatar != null) holder.imageProfile.setImageBitmap(cached.avatar);
            }
        } else {
            UserDB.getInstance().getUser(entry.getUserId(), task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot doc = task.getResult();
                    String name = doc.getString("name");
                    String email = doc.getString("email");
                    String avatarBase64 = doc.getString("avatar");

                    Bitmap bitmap = null;
                    if (avatarBase64 != null) {
                        bitmap = ImageUtil.base64ToBitmap(avatarBase64);
                    }

                    UserData data = new UserData();
                    data.name = name != null ? name : "Unknown";
                    data.email = email != null ? email : "";
                    data.avatar = bitmap;

                    userCache.put(entry.getUserId(), data);

                    holder.textName.setText(data.name);
                    holder.textEmail.setText(data.email);
                    if (data.avatar != null) holder.imageProfile.setImageBitmap(data.avatar);
                } else {
                    holder.textName.setText("Unknown");
                    holder.textEmail.setText("");
                }
            });
        }

        // Set click listeners for action buttons
        holder.buttonReplace.setOnClickListener(v -> {
            if (listener != null && holder.buttonReplace.isEnabled()) listener.onReplace(entry);
        });

        holder.buttonRevoke.setOnClickListener(v -> {
            if (listener != null) listener.onRevoke(entry);
        });

    }

    /**
     * Returns the total number of entries in the adapter.
     *
     * @return Number of entries
     */
    @Override
    public int getItemCount() {
        return entries.size();
    }

    /**
     * ViewHolder for individual waitlist items.
     * Holds references to views for name, email, status, profile image, and action buttons.
     */
    public static class WaitlistViewHolder extends RecyclerView.ViewHolder {

        ImageView imageProfile;
        TextView textName, textEmail, textStatus;
        MaterialButton buttonRevoke, buttonReplace;

        /**
         * Constructor that binds views from the item layout.
         *
         * @param itemView The item view
         */
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

    /**
     * Updates the adapter's list of entries and refreshes the RecyclerView.
     *
     * @param newEntries New list of waitlist entries
     */
    public void updateList(List<Waitlist> newEntries) {
        entries.clear();
        entries.addAll(newEntries);
        notifyDataSetChanged();
    }

    /**
     * Internal class to cache user data locally.
     */
    private static class UserData {
        String name;
        String email;
        Bitmap avatar;
    }

}