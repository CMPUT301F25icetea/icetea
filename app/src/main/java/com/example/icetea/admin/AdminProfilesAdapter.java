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

public class AdminProfilesAdapter extends RecyclerView.Adapter<AdminProfilesAdapter.UserViewHolder> {

    public interface ActionListener {
        void onDelete(User user);
    }

    private final Context context;
    private final List<User> users;
    private final ActionListener listener;

    public AdminProfilesAdapter(Context context, List<User> users, ActionListener listener) {
        this.context = context;
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile_card, parent, false);
        return new UserViewHolder(view);
    }

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

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imageProfile;
        TextView textName, textEmail;
        ImageButton buttonDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.imageProfile);
            textName = itemView.findViewById(R.id.textName);
            textEmail = itemView.findViewById(R.id.textEmail);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
