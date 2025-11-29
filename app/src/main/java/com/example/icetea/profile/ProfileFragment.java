package com.example.icetea.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.icetea.R;
import com.example.icetea.admin.AdminHomeFragment;
import com.example.icetea.auth.AuthActivity;
import com.example.icetea.auth.CurrentUser;
import com.example.icetea.models.UserDB;
import com.example.icetea.util.Callback;
import com.example.icetea.util.ImageUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class ProfileFragment extends Fragment {
    private Runnable checkInputChanged;
    private ImageView avatarImageView;
    private boolean avatarChanged = false;
    private String newAvatarBase64;
    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    String uriToBase64;
                    try {
                        uriToBase64 = ImageUtil.uriToBase64(requireContext(), uri);
                        avatarImageView.setImageURI(uri);
                        avatarChanged = true;
                        checkInputChanged.run();
                        newAvatarBase64 = uriToBase64;
                    } catch (ImageUtil.ImageTooLargeException e) {
                        Toast.makeText(getContext(), "Image too large. Please select a smaller image.", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Failed to process image.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = new ProfileController();
        avatarImageView = view.findViewById(R.id.imageViewAvatar);
        MaterialButton uploadButton = view.findViewById(R.id.buttonChangeAvatar);

        uploadButton.setOnClickListener(v -> {
            pickImageLauncher.launch("image/*");
        });

        avatarImageView.setOnClickListener(v -> {
            pickImageLauncher.launch("image/*");
        });

        MaterialButton adminPanelButton = view.findViewById(R.id.buttonAdminPanel);
        adminPanelButton.setVisibility(View.GONE);
        FirebaseFirestore.getInstance().collection("admin")
                .whereEqualTo("userId", CurrentUser.getInstance().getFid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot result = task.getResult();
                        if (result != null && !result.isEmpty()) {
                            adminPanelButton.setVisibility(View.VISIBLE);
                            adminPanelButton.setOnClickListener(v -> {
                                FragmentManager fm = requireActivity().getSupportFragmentManager();
                                FragmentTransaction transaction = fm.beginTransaction();
                                transaction.setReorderingAllowed(true);
                                transaction.setCustomAnimations(
                                        R.anim.slide_in_right,
                                        R.anim.slide_out_left,
                                        R.anim.slide_in_left,
                                        R.anim.slide_out_right
                                );
                                transaction.replace(R.id.main_fragment_container, AdminHomeFragment.newInstance());
                                transaction.addToBackStack(null);
                                transaction.commit();
                            });
                        }
                    }
                });


        TextInputLayout nameTextLayout = view.findViewById(R.id.inputLayoutNameProfile);
        TextInputLayout emailTextLayout = view.findViewById(R.id.inputLayoutEmailProfile);
        TextInputLayout phoneTextLayout = view.findViewById(R.id.inputLayoutPhoneProfile);

        TextInputEditText nameEditText = view.findViewById(R.id.inputEditTextNameProfile);
        TextInputEditText emailEditText = view.findViewById(R.id.inputEditTextEmailProfile);
        TextInputEditText phoneEditText = view.findViewById(R.id.inputEditTextPhoneProfile);

        MaterialButton saveButton = view.findViewById(R.id.buttonSaveProfile);
        MaterialButton cancelButton = view.findViewById(R.id.buttonCancelProfile);
        MaterialButton deleteButton = view.findViewById(R.id.buttonDeleteAccount);

        nameEditText.setText(CurrentUser.getInstance().getName());
        emailEditText.setText(CurrentUser.getInstance().getEmail());
        phoneEditText.setText(CurrentUser.getInstance().getPhone());
        if (CurrentUser.getInstance().getAvatar() != null) {
            avatarImageView.setImageBitmap(CurrentUser.getInstance().getAvatar());
        }

        saveButton.setEnabled(false);
        cancelButton.setEnabled(false);
        saveButton.setAlpha(0.0f);
        cancelButton.setAlpha(0.0f);

        checkInputChanged = () -> {
            boolean changed =
                    !String.valueOf(nameEditText.getText()).trim().equals(CurrentUser.getInstance().getName()) ||
                    !String.valueOf(emailEditText.getText()).trim().equals(CurrentUser.getInstance().getEmail()) ||
                    !String.valueOf(phoneEditText.getText()).trim().equals(CurrentUser.getInstance().getPhone() != null ? CurrentUser.getInstance().getPhone() : "") ||
                            avatarChanged;

            saveButton.setEnabled(changed);
            cancelButton.setEnabled(changed);
            saveButton.setAlpha(changed ? 1f : 0.0f);
            cancelButton.setAlpha(changed ? 1f : 0.0f);
        };

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputChanged.run();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        nameEditText.addTextChangedListener(watcher);
        emailEditText.addTextChangedListener(watcher);
        phoneEditText.addTextChangedListener(watcher);

        saveButton.setOnClickListener(v -> {

            nameTextLayout.setError(null);
            emailTextLayout.setError(null);
            phoneTextLayout.setError(null);

            String name = nameEditText.getText() != null ? nameEditText.getText().toString().trim() : "";
            String email = emailEditText.getText() != null ? emailEditText.getText().toString().trim() : "";
            String phone = phoneEditText.getText() != null ? phoneEditText.getText().toString().trim() : "";

            String nameError = controller.validateName(name);
            String emailError = controller.validateEmail(email);
            String phoneError = controller.validatePhone(phone);

            boolean hasError = false;

            if (nameError != null) {
                nameTextLayout.setError(nameError);
                hasError = true;
            }

            if (emailError != null) {
                emailTextLayout.setError(emailError);
                hasError = true;
            }

            if (phoneError != null) {
                phoneTextLayout.setError(phoneError);
                hasError = true;
            }

            HashMap<String, Object> updates = new HashMap<>();

            if (avatarChanged) {
                updates.put("avatar", newAvatarBase64);
            } else {
                newAvatarBase64 = null;
            }

            if (hasError) return;

            updates.put("name", name);
            updates.put("email", email);
            updates.put("phone", phone.isEmpty() ? null : phone);

            controller.updateProfile(CurrentUser.getInstance().getFid(), updates, new Callback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    // better UI response
                    CurrentUser.getInstance().setName(name);
                    CurrentUser.getInstance().setEmail(email);
                    CurrentUser.getInstance().setPhone(phone);

                    if (avatarChanged) {
                        CurrentUser.getInstance().setAvatar(ImageUtil.base64ToBitmap(newAvatarBase64));
                        avatarChanged = false;
                        newAvatarBase64 = null;
                    }

                    View root = getView();
                    if (root != null) {
                        View currentFocus = root.findFocus();
                        if (currentFocus != null) {
                            currentFocus.clearFocus();
                        }
                    }
                    saveButton.setEnabled(false);
                    cancelButton.setEnabled(false);
                    saveButton.setAlpha(0.0f);
                    cancelButton.setAlpha(0.0f);
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        cancelButton.setOnClickListener(v -> {
            nameEditText.setText(CurrentUser.getInstance().getName());
            emailEditText.setText(CurrentUser.getInstance().getEmail());
            phoneEditText.setText(CurrentUser.getInstance().getPhone());
            nameTextLayout.setError(null);
            emailTextLayout.setError(null);
            phoneTextLayout.setError(null);
            newAvatarBase64 = null;
            avatarChanged = false;
            if (CurrentUser.getInstance().getAvatar() == null) {
                avatarImageView.setImageResource(R.drawable.default_avatar);
            } else {
                avatarImageView.setImageBitmap(CurrentUser.getInstance().getAvatar());
            }

            View root = getView();
            if (root != null) {
                View currentFocus = root.findFocus();
                if (currentFocus != null) {
                    currentFocus.clearFocus();
                }
            }
            saveButton.setEnabled(false);
            cancelButton.setEnabled(false);
            saveButton.setAlpha(0.0f);
            cancelButton.setAlpha(0.0f);
        });

        deleteButton.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Delete", (dialog, which) -> {
                        String userId = CurrentUser.getInstance().getFid();

                        controller.deleteProfile(userId, new Callback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                Toast.makeText(requireContext(), "Account deleted", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(requireActivity(), AuthActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                requireActivity().finish();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(requireContext(), "Delete failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .show();
        });

        SwitchCompat notificationsSwitch = view.findViewById(R.id.switchNotifications);

        notificationsSwitch.setChecked(CurrentUser.getInstance().getNotifications());

        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            HashMap<String, Object> updates = new HashMap<>();
            updates.put("notifications", isChecked);

            UserDB.getInstance().updateUser(CurrentUser.getInstance().getFid(), updates, task -> {
                if (task.isSuccessful()) {
                    CurrentUser.getInstance().setNotifications(isChecked);
                    String message = isChecked ? "Notifications enabled" : "Notifications disabled";
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to update notifications", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

}

