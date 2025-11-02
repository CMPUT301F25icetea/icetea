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
import com.example.icetea.auth.FBAuthenticator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EntrantProfileFragment extends Fragment {

    private EditText firstNameEditText, lastNameEditText, emailEditText, phoneEditText;
    private Button saveButton, deleteButton;
    private ImageView profileImage;

    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    private DocumentReference profileRef;

    public EntrantProfileFragment() {
        // Required empty public constructor
    }

    public static EntrantProfileFragment newInstance() {
        return new EntrantProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();

        // Get current Firebase user
        currentUser = FBAuthenticator.getCurrentUser();
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
        return inflater.inflate(R.layout.fragment_entrant_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firstNameEditText = view.findViewById(R.id.Firstname);
        lastNameEditText = view.findViewById(R.id.Lastname);
        emailEditText = view.findViewById(R.id.email);
        phoneEditText = view.findViewById(R.id.Phone);
        saveButton = view.findViewById(R.id.button);
        deleteButton = view.findViewById(R.id.button2);

        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        loadProfile();

        saveButton.setOnClickListener(v -> saveProfile());
        deleteButton.setOnClickListener(v -> deleteProfile());
    }

    private void loadProfile() {
        profileRef.get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        firstNameEditText.setText(document.getString("firstName"));
                        lastNameEditText.setText(document.getString("lastName"));
                        emailEditText.setText(document.getString("email"));
                        phoneEditText.setText(document.getString("phone"));
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
                );
    }

    private void saveProfile() {
        String first = firstNameEditText.getText().toString().trim();
        String last = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

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
                    firstNameEditText.setText("");
                    lastNameEditText.setText("");
                    emailEditText.setText("");
                    phoneEditText.setText("");
                    Toast.makeText(requireContext(), "Profile info cleared", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Failed to clear profile", Toast.LENGTH_SHORT).show());
    }
}
