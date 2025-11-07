package com.example.icetea.entrant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.icetea.R;
import com.example.icetea.auth.AuthActivity;
import com.example.icetea.auth.FBAuthenticator;
import com.example.icetea.organizer.OrganizerContainerFragment;
import com.example.icetea.util.NavigationHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private EditText firstNameInput, lastNameInput, emailInput, phoneInput;
    private ImageView profileImage;

    private FirebaseFirestore firestore;
    private DocumentReference profileRef;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();

//         Get current Firebase user
        FirebaseUser currentUser = FBAuthenticator.getCurrentUser();
        if (currentUser == null) {
            currentUser = FirebaseAuth.getInstance().getCurrentUser();
        }

        if (currentUser != null) {
            profileRef = firestore.collection("users").document(currentUser.getUid());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firstNameInput = view.findViewById(R.id.inputFirstName);
        lastNameInput = view.findViewById(R.id.inputLastName);
        emailInput = view.findViewById(R.id.inputEmail);
        phoneInput = view.findViewById(R.id.inputPhone);
        Button updateButton = view.findViewById(R.id.buttonUpdateInfo);
        Button deleteButton = view.findViewById(R.id.buttonDeleteProfile);
        Button logoutButton = view.findViewById(R.id.buttonLogout);
        Button swapRoleButton = view.findViewById(R.id.buttonSwapRole);

        if (FBAuthenticator.getCurrentUser() == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        loadProfile();

        updateButton.setOnClickListener(v -> saveProfile());
        deleteButton.setOnClickListener(v -> deleteProfile());
        logoutButton.setOnClickListener(v -> logoutUser());
        swapRoleButton.setOnClickListener(v -> swapRole());
    }

    private void logoutUser() {
        FBAuthenticator.logout();
        NavigationHelper.openActivity(this, AuthActivity.class);
    }
    private void swapRole() {

        if (getId() == R.id.entrant_fragment_container) {
            NavigationHelper.replaceFragment(requireActivity().getSupportFragmentManager(), R.id.main, OrganizerContainerFragment.newInstance(), false);

        } else if (getId() == R.id.organizer_fragment_container) {
            NavigationHelper.replaceFragment(requireActivity().getSupportFragmentManager(), R.id.main, EntrantContainerFragment.newInstance(), false);

        }
    }

    private void loadProfile() {
        profileRef.get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        firstNameInput.setText(document.getString("firstName"));
                        lastNameInput.setText(document.getString("lastName"));
                        emailInput.setText(document.getString("email"));
                        phoneInput.setText(document.getString("phone"));
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
                );
    }

    private void saveProfile() {
        String first = firstNameInput.getText().toString().trim();
        String last = lastNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();

        if (first.isEmpty() || last.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill out all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> profileData = new HashMap<>();
        profileData.put("firstName", first);
        profileData.put("lastName", last);
        profileData.put("phone", phone);

        // Append without overwriting existing fields
        profileRef.update(profileData)
                .addOnSuccessListener(unused ->
                        Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    // If doc doesn’t exist yet, create it while preserving structure
                    profileRef.set(profileData, com.google.firebase.firestore.SetOptions.merge())
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(requireContext(), "Profile created", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(err ->
                                    Toast.makeText(requireContext(), "Error saving profile", Toast.LENGTH_SHORT).show());
                });
    }

    private void deleteProfile() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", null);
        updates.put("lastName", null);
        updates.put("phone", null);

        profileRef.update(updates)
                .addOnSuccessListener(unused -> {
                    firstNameInput.setText("");
                    lastNameInput.setText("");
                    emailInput.setText("");
                    phoneInput.setText("");
                    Toast.makeText(requireContext(), "Profile info cleared", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Failed to clear profile", Toast.LENGTH_SHORT).show());
    }
}
