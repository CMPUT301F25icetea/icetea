package com.example.icetea.admin;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icetea.R;
import com.example.icetea.models.User;
import com.example.icetea.util.ImageUtil;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

/**
 * RecyclerView Adapter for displaying user profiles in the Admin section.
 * Each profile shows the user's name, email, and avatar.
 * Admins can delete users via a delete button.
 */
public class AdminProfilesAdapter extends RecyclerView.Adapter<AdminProfilesAdapter.UserViewHolder> {

    /**
     * Interface to handle actions on user items, such as deletion.
     */
    public interface ActionListener {
        /**
         * Called when the delete button of a user is clicked.
         *
         * @param user the User object associated with the clicked delete button
         */
        void onDelete(User user);
    }

    private final Context context;
    private final List<User> users;
    private final ActionListener listener;

    /**
     * Constructor for AdminProfilesAdapter.
     *
     * @param context  the context in which the adapter is used
     * @param users    the list of User objects to display
     * @param listener the listener to handle delete actions
     */
    public AdminProfilesAdapter(Context context, List<User> users, ActionListener listener) {
        this.context = context;
        this.users = users;
        this.listener = listener;
    }

    /**
     * Inflates the item layout and creates the ViewHolder.
     *
     * @param parent   the parent ViewGroup
     * @param viewType the view type of the new View
     * @return a new UserViewHolder instance
     */
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile_card, parent, false);
        return new UserViewHolder(view);
    }

    /**
     * Binds the user data to the ViewHolder and sets the delete button listener.
     *
     * @param holder   the UserViewHolder to bind data to
     * @param position the position of the item in the dataset
     */
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.textName.setText(user.getName());
        holder.textEmail.setText(user.getEmail());

        String avatarBase64 = user.getAvatar();
        if (avatarBase64 == null || avatarBase64.isEmpty()) {
            holder.imageProfile.setImageResource(R.drawable.default_avatar);
        } else {
            Bitmap profileBitmap = ImageUtil.base64ToBitmap(avatarBase64);
            if (profileBitmap != null) {
                holder.imageProfile.setImageBitmap(profileBitmap);
            } else {
                holder.imageProfile.setImageResource(R.drawable.default_avatar);
            }
        }

        holder.buttonDelete.setOnClickListener(v -> listener.onDelete(user));
    }

    /**
     * Returns the number of items in the dataset.
     *
     * @return the size of the users list
     */
    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     * ViewHolder class for holding and caching views for each user item.
     */
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imageProfile;
        TextView textName, textEmail;
        ImageButton buttonDelete;

        /**
         * Constructor for UserViewHolder.
         *
         * @param itemView the root view of the user item layout
         */
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.imageProfile);
            textName = itemView.findViewById(R.id.textName);
            textEmail = itemView.findViewById(R.id.textEmail);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
