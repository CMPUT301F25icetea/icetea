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

public class FinalEntrantsAdapter extends RecyclerView.Adapter<FinalEntrantsAdapter.ViewHolder> {

    private final Context context;
    private final List<Waitlist> entries;
    private final Map<String, UserData> userCache = new HashMap<>();

    public FinalEntrantsAdapter(Context context, List<Waitlist> entries) {
        this.context = context;
        this.entries = entries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_waitlist_card, parent, false);
        return new ViewHolder(view);
    }

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

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public void updateList(List<Waitlist> newEntries) {
        entries.clear();
        entries.addAll(newEntries);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProfile;
        TextView textName, textEmail, textStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.imageProfile);
            textName = itemView.findViewById(R.id.textName);
            textEmail = itemView.findViewById(R.id.textEmail);
            textStatus = itemView.findViewById(R.id.textStatus);
        }
    }

    private static class UserData {
        String name;
        String email;
        Bitmap avatar;
    }
}
