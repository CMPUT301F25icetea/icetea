package com.example.icetea.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.icetea.R;
import com.example.icetea.auth.AuthActivity;
import com.example.icetea.auth.CurrentUser;
import com.example.icetea.util.Callback;
import com.example.icetea.util.ImageUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class ProfileFragment extends Fragment {
    private Runnable checkInputChanged;
    private ImageView avatarImageView;
    private boolean avatarChanged = false;
    private Uri newAvatarUri;
    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    avatarImageView.setImageURI(uri);
                    avatarChanged = true;
                    checkInputChanged.run();
                    newAvatarUri = uri;
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

        // Initially disable buttons
        saveButton.setEnabled(false);
        cancelButton.setEnabled(false);
        saveButton.setAlpha(0.5f);
        cancelButton.setAlpha(0.5f);

        checkInputChanged = () -> {
            boolean changed =
                    !String.valueOf(nameEditText.getText()).trim().equals(CurrentUser.getInstance().getName()) ||
                    !String.valueOf(emailEditText.getText()).trim().equals(CurrentUser.getInstance().getEmail()) ||
                    !String.valueOf(phoneEditText.getText()).trim().equals(CurrentUser.getInstance().getPhone() != null ? CurrentUser.getInstance().getPhone() : "") ||
                            avatarChanged;

            saveButton.setEnabled(changed);
            cancelButton.setEnabled(changed);
            saveButton.setAlpha(changed ? 1f : 0.5f);
            cancelButton.setAlpha(changed ? 1f : 0.5f);
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

            // Validate
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
//            if (uriToBase64 == null) {
//                Toast.makeText(getContext(), "Error uploading avatar", Toast.LENGTH_SHORT).show();
//                hasError = true;
//            }
            if (hasError) return;

            HashMap<String, Object> updates = new HashMap<>();
            updates.put("name", name);
            updates.put("email", email);
            updates.put("phone", phone.isEmpty() ? null : phone);

            // todo:differentiate between newUri == null vs uritobase64 returning null (error)
            String uriToBase64 = ImageUtil.uriToBase64(requireContext(), newAvatarUri);
            if (avatarChanged) {
                updates.put("avatar", uriToBase64);
            }

            controller.updateProfile(CurrentUser.getInstance().getFid(), updates, new Callback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    // better UI response
                    CurrentUser.getInstance().setName(name);
                    CurrentUser.getInstance().setEmail(email);
                    CurrentUser.getInstance().setPhone(phone);

                    if (avatarChanged) {
                        CurrentUser.getInstance().setAvatar(ImageUtil.base64ToBitmap(uriToBase64));
                        avatarChanged = false;
                        newAvatarUri = null;
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
                    saveButton.setAlpha(0.5f);
                    cancelButton.setAlpha(0.5f);
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
            newAvatarUri = null;
            avatarChanged = false;
            if (CurrentUser.getInstance().getAvatar() == null) {
                avatarImageView.setImageResource(R.drawable.profile);
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
            saveButton.setAlpha(0.5f);
            cancelButton.setAlpha(0.5f);
        });
        // Add this code at the END of your onViewCreated method, right after the cancelButton.setOnClickListener block

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
                                CurrentUser.getInstance().clearSession();
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

    }

}

