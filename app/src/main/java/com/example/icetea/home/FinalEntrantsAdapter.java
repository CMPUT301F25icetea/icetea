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
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RecyclerView Adapter for displaying the final entrants of an event.
 * <p>
 * Each item in the list shows the entrant's name, email, profile image,
 * and their waitlist status. User data is fetched asynchronously from Firestore
 * and cached to reduce repeated network requests.
 */
public class FinalEntrantsAdapter extends RecyclerView.Adapter<FinalEntrantsAdapter.ViewHolder> {

    /** Context used for inflating layouts */
    private final Context context;

    /** List of waitlist entries representing the final entrants */
    private final List<Waitlist> entries;

    /** Cache of user data keyed by userId to avoid repeated Firestore requests */
    private final Map<String, UserData> userCache = new HashMap<>();

    /**
     * Constructor for the adapter.
     *
     * @param context The context used to inflate views
     * @param entries The list of Waitlist entries to display
     */
    public FinalEntrantsAdapter(Context context, List<Waitlist> entries) {
        this.context = context;
        this.entries = entries;
    }

    /**
     * Inflates the item layout and returns a new ViewHolder.
     *
     * @param parent The parent ViewGroup
     * @param viewType The type of view
     * @return A new ViewHolder instance
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_waitlist_card, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds data to the ViewHolder at a given position.
     * Loads user details from Firestore if not cached, and updates UI components.
     *
     * @param holder The ViewHolder to bind data to
     * @param position The position in the dataset
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Waitlist entry = entries.get(position);

        holder.textName.setText("Loading...");
        holder.textEmail.setText("");
        holder.imageProfile.setImageResource(R.drawable.default_avatar);
        holder.textStatus.setText("Status: " + entry.getStatus());

        if (userCache.containsKey(entry.getUserId())) {
            UserData cached = userCache.get(entry.getUserId());
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
    }

    /**
     * Returns the total number of entries in the list.
     *
     * @return The number of items
     */
    @Override
    public int getItemCount() {
        return entries.size();
    }

    /**
     * Updates the adapter's data list with new entries and refreshes the RecyclerView.
     *
     * @param newEntries New list of Waitlist entries
     */
    public void updateList(List<Waitlist> newEntries) {
        entries.clear();
        entries.addAll(newEntries);
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for displaying entrant information.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        /** ImageView for the entrant's profile picture */
        ImageView imageProfile;
        /** TextView for the entrant's name */
        TextView textName;
        /** TextView for the entrant's email */
        TextView textEmail;
        /** TextView for the entrant's status */
        TextView textStatus;

        /**
         * Constructor for the ViewHolder.
         *
         * @param itemView The root view of the item layout
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.imageProfile);
            textName = itemView.findViewById(R.id.textName);
            textEmail = itemView.findViewById(R.id.textEmail);
            textStatus = itemView.findViewById(R.id.textStatus);
        }
    }

    /**
     * Internal class to cache user data to minimize Firestore reads.
     */
    private static class UserData {
        /** User's full name */
        String name;
        /** User's email */
        String email;
        /** User's avatar as a Bitmap */
        Bitmap avatar;
    }
}