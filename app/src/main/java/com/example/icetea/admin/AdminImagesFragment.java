package com.example.icetea.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.icetea.R;
import com.example.icetea.models.EventDB;
import com.example.icetea.models.ImageItem;
import com.example.icetea.models.UserDB;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminImagesFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdminImagesAdapter adapter;
    private List<ImageItem> images;

    public AdminImagesFragment() { }

    public static AdminImagesFragment newInstance() {
        return new AdminImagesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_images, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerAdminImages);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2)); // 2 columns
        images = new ArrayList<>();

        adapter = new AdminImagesAdapter(requireContext(), images, item -> {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Image")
                    .setMessage("Are you sure you want to delete this image? This action cannot be undone.")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Delete", (dialog, which) -> {
                        if ("user".equals(item.getType())) {
                            UserDB.getInstance().updateUser(item.getId(),
                                    new java.util.HashMap<String, Object>() {{
                                        put("avatar", null);
                                    }},
                                    task -> {
                                        if (task.isSuccessful()) {
                                            images.remove(item);
                                            adapter.notifyDataSetChanged();
                                            Toast.makeText(requireContext(), "User avatar deleted", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(requireContext(), "Delete failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else if ("event".equals(item.getType())) {
                            EventDB.getInstance().updateEvent(item.getId(),
                                    new java.util.HashMap<String, Object>() {{
                                        put("posterBase64", null);
                                    }},
                                    task -> {
                                        if (task.isSuccessful()) {
                                            images.remove(item);
                                            adapter.notifyDataSetChanged();
                                            Toast.makeText(requireContext(), "Event poster deleted", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(requireContext(), "Delete failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .show();
        });


        recyclerView.setAdapter(adapter);

        loadImages();
    }

    private void loadImages() {
        images.clear();

        UserDB.getInstance().getAllUsersWithAvatar(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                    String base64 = doc.getString("avatar");
                    if (base64 != null && !base64.isEmpty()) {
                        images.add(new ImageItem("user", doc.getId(), base64));
                    }
                }

                EventDB.getInstance().getAllEventsWithPoster(eventTask -> {
                    if (eventTask.isSuccessful() && eventTask.getResult() != null) {
                        for (DocumentSnapshot doc : eventTask.getResult().getDocuments()) {
                            String base64 = doc.getString("posterBase64");
                            if (base64 != null && !base64.isEmpty()) {
                                images.add(new ImageItem("event", doc.getId(), base64));
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}
