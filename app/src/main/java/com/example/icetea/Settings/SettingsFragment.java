package com.example.icetea.Settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SwitchCompat;

import com.example.icetea.R;
import com.example.icetea.auth.CurrentUser;
import com.example.icetea.models.UserDB;

import java.util.HashMap;

public class SettingsFragment extends Fragment {

    private SwitchCompat notificationSwitch;
    private final UserDB userDB = UserDB.getInstance();

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_entrant_settings, container, false);

        notificationSwitch = view.findViewById(R.id.switch_notifications);

        // Load current user notifications setting
        boolean currentValue = CurrentUser.getInstance().getNotifications();
        notificationSwitch.setChecked(currentValue);

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateNotificationSetting(isChecked);
        });

        return view;
    }

    private void updateNotificationSetting(boolean enabled) {

        String uid = CurrentUser.getInstance().getFid();
        Log.d("Settings", "Attempting update for UID: " + uid);

        if (uid == null || uid.isEmpty()) {
            Log.e("Settings", "❌ User ID is NULL — cannot update notifications");
            Toast.makeText(requireContext(), "User not loaded yet", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> update = new HashMap<>();
        update.put("notifications", enabled);

        userDB.updateUser(uid, update, task -> {
            if (task.isSuccessful()) {
                CurrentUser.getInstance().setNotifications(enabled);
                Log.d("Settings", "✔ Firestore update success");
                Toast.makeText(requireContext(), "Notifications " + (enabled ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
            } else {
                Log.e("Settings", "❌ Firestore update FAILED", task.getException());
                Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }}
