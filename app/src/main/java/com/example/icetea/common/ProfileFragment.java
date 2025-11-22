package com.example.icetea.common;

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
import com.example.icetea.entrant.EntrantContainerFragment;
import com.example.icetea.models.UserDB;
import com.example.icetea.organizer.OrganizerContainerFragment;
import com.example.icetea.util.Callback;
import com.example.icetea.util.NavigationHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Fragment representing the user's profile page.
 *
 * Allows the user to view and update profile information, delete their account,
 * log out, and swap roles (entrant/organizer).
 */
public class ProfileFragment extends Fragment {

    private EditText firstNameInput, lastNameInput, emailInput, phoneInput;
    private ImageView profileImage;
    private ProfileController controller;

    /**
     * Default constructor for the fragment.
     */
    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment.
     *
     * @return A new instance of ProfileFragment.
     */
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = new ProfileController();

        // Initialize UI components
        firstNameInput = view.findViewById(R.id.inputFirstName);
        lastNameInput = view.findViewById(R.id.inputLastName);
        emailInput = view.findViewById(R.id.inputEmail);
        phoneInput = view.findViewById(R.id.inputPhone);
        Button updateButton = view.findViewById(R.id.buttonUpdateInfo);
        Button deleteButton = view.findViewById(R.id.buttonDeleteProfile);
        Button logoutButton = view.findViewById(R.id.buttonLogout);
        Button swapRoleButton = view.findViewById(R.id.buttonSwapRole);

        // Ensure user is logged in
        if (FBAuthenticator.getCurrentUser() == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Determine user role and configure role swap button
        UserDB.getInstance().getUserTopRole(FBAuthenticator.getCurrentUserId(), new Callback<String>() {
            @Override
            public void onSuccess(String role) {
                if ("entrant".equals(role)) {
                    swapRoleButton.setVisibility(View.GONE);
                } else if ("organizer".equals(role)) {
                    swapRoleButton.setVisibility(View.VISIBLE);
                    swapRoleButton.setOnClickListener(v -> {
                        // Swap between organizer and entrant views
//                        if (getId() == R.id.entrant_fragment_container) {
//                            NavigationHelper.replaceFragment(requireActivity().getSupportFragmentManager(), R.id.main, OrganizerContainerFragment.newInstance(), false);
//                        } else if (getId() == R.id.organizer_fragment_container) {
//                            NavigationHelper.replaceFragment(requireActivity().getSupportFragmentManager(), R.id.main, EntrantContainerFragment.newInstance(), false);
//                        }
                    });

                } else {
                    Toast.makeText(getContext(), "Error retrieving role: " + role, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed from profile fragment call to getUserTopRole" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Load profile data into input fields
        controller.loadProfile(firstNameInput, lastNameInput, emailInput, phoneInput, new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                return;
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Handle profile update button click
        updateButton.setOnClickListener(v -> controller.updateProfile(firstNameInput, lastNameInput, emailInput, phoneInput, new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(getContext(), "Updated profile", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));

        // Handle delete profile button click
        deleteButton.setOnClickListener(v -> controller.deleteProfile(new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                NavigationHelper.openActivity(ProfileFragment.this, AuthActivity.class);
                // deleting automatically logs out via FBAuthenticator
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));

        // Handle logout button click
        logoutButton.setOnClickListener(v -> {
            controller.logoutUser();
            NavigationHelper.openActivity(this, AuthActivity.class);
        });
    }
}
