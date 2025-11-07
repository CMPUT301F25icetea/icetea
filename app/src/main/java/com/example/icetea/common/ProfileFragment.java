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

public class ProfileFragment extends Fragment {

    private EditText firstNameInput, lastNameInput, emailInput, phoneInput;
    private ImageView profileImage;
    private ProfileController controller;

    public ProfileFragment() {
        // Required empty public constructor
    }

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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = new ProfileController();

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

        UserDB.getInstance().getUserTopRole(FBAuthenticator.getCurrentUserId(), new Callback<String>() {
            @Override
            public void onSuccess(String role) {
                if ("entrant".equals(role)) {
                    swapRoleButton.setVisibility(View.GONE);
                } else if ("organizer".equals(role)) {
                    swapRoleButton.setVisibility(View.VISIBLE);
                    swapRoleButton.setOnClickListener(v -> {
                        //TODO: once admins are added, add them here
                        if (getId() == R.id.entrant_fragment_container) {
                            NavigationHelper.replaceFragment(requireActivity().getSupportFragmentManager(), R.id.main, OrganizerContainerFragment.newInstance(), false);

                        } else if (getId() == R.id.organizer_fragment_container) {
                            NavigationHelper.replaceFragment(requireActivity().getSupportFragmentManager(), R.id.main, EntrantContainerFragment.newInstance(), false);
                        }
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

        deleteButton.setOnClickListener(v -> controller.deleteProfile(new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                NavigationHelper.openActivity(ProfileFragment.this, AuthActivity.class);
                // calling delete auto logs out fb authenticator
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));

        logoutButton.setOnClickListener(v -> {
            controller.logoutUser();
            NavigationHelper.openActivity(this, AuthActivity.class);
        });
    }
}
