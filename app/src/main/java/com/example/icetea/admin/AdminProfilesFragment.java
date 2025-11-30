package com.example.icetea.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.icetea.R;
import com.example.icetea.models.User;
import com.example.icetea.models.UserDB;
import com.example.icetea.profile.ProfileController;
import com.example.icetea.util.Callback;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminProfilesFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdminProfilesAdapter adapter;
    private List<User> users;

    public AdminProfilesFragment() {
        // Required empty public constructor
    }

    public static AdminProfilesFragment newInstance() {
        return new AdminProfilesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_profiles, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerAdminProfiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        users = new ArrayList<>();
        adapter = new AdminProfilesAdapter(requireContext(), users, user -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Profile")
                    .setMessage("Are you sure you want to delete this profile? This action cannot be undone.")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Delete", (dialog, which) -> {

                        ProfileController controller = new ProfileController();
                        controller.deleteProfile(user.getId(), new Callback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                users.remove(user);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(requireContext(), "Profile deleted", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(requireContext(), "Delete failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    })
                    .show();
        });

        recyclerView.setAdapter(adapter);

        loadUsers();
    }

    private void loadUsers() {
        UserDB.getInstance().getAllUsers(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                QuerySnapshot snap = task.getResult();
                users.clear();
                for (var doc : snap.getDocuments()) {
                    User user = doc.toObject(User.class);
                    if (user != null) users.add(user);
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(requireContext(), "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }

}