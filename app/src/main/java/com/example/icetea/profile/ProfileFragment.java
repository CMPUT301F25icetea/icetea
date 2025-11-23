package com.example.icetea.profile;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.icetea.R;
import com.example.icetea.auth.CurrentUser;
import com.example.icetea.util.Callback;
import com.example.icetea.util.ImageUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
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
    }
}
//        deleteButton.setOnClickListener(v -> {
//            String currentEmail = CurrentUser.getInstance().getEmail();
//
//            // --- Input EditText ---
//            EditText input = new EditText(requireContext());
//            input.setHint("Enter your email address");
//            input.setSingleLine(true);
//            input.setGravity(Gravity.CENTER);
//            input.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
//
//            GradientDrawable editBg = new GradientDrawable();
//            editBg.setColor(ContextCompat.getColor(requireContext(), R.color.darkerBeige));
//            editBg.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
//            input.setBackground(editBg);
//
//            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());
//            input.setPadding(padding, padding, padding, padding);
//
//            // Wrap EditText in a container to prevent clipping
//            LinearLayout inputContainer = new LinearLayout(requireContext());
//            inputContainer.setOrientation(LinearLayout.VERTICAL);
//            inputContainer.setGravity(Gravity.CENTER_HORIZONTAL);
//            int containerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
//            inputContainer.setPadding(0, containerPadding, 0, containerPadding);
//            inputContainer.setClipToPadding(false);
//
//            LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(
//                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics()),
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//            );
//            inputParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
//            input.setLayoutParams(inputParams);
//
//            inputContainer.addView(input);
//
//            // --- Spannable message ---
//            String messageText = "Deleting your account will delete all your data.\nThis action cannot be undone.\nPlease enter this account's email address to confirm deletion. (%s)";
//            SpannableString spannableMessage = new SpannableString(String.format(messageText, currentEmail));
//
//            // Bold "This action cannot be undone."
//            String boldPart = "This action cannot be undone.";
//            int boldStart = spannableMessage.toString().indexOf(boldPart);
//            int boldEnd = boldStart + boldPart.length();
//            spannableMessage.setSpan(new StyleSpan(Typeface.BOLD), boldStart, boldEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//            // Color the email
//            int emailStart = spannableMessage.toString().indexOf(currentEmail);
//            int emailEnd = emailStart + currentEmail.length();
//            spannableMessage.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.emeraldGreen)),
//                    emailStart, emailEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//            // --- Build dialog ---
//            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
//            builder.setTitle("Delete Account");
//            builder.setMessage(spannableMessage);
//            builder.setView(inputContainer);
//
//            GradientDrawable bg = new GradientDrawable();
//            bg.setColor(ContextCompat.getColor(requireContext(), R.color.beige));
//            bg.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()));
//            builder.setBackground(bg);
//
//            builder.setNegativeButton("Cancel", null);
//            builder.setPositiveButton("Delete", null);
//
//            AlertDialog dialog = builder.create();
//
//            dialog.setOnShowListener(dialogInterface -> {
//                Button cancelBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//                Button deleteBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//
//                MaterialShapeDrawable deleteBg = new MaterialShapeDrawable(
//                        ShapeAppearanceModel.builder()
//                                .setAllCornerSizes(
//                                        TypedValue.applyDimension(
//                                                TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()))
//                                .build()
//                );
//                deleteBg.setFillColor(ColorStateList.valueOf(Color.RED));
//                deleteBtn.setBackground(deleteBg);
//                deleteBtn.setTextColor(Color.WHITE);
//
//
//                // Cancel button background with stroke
//                MaterialShapeDrawable cancelBg = new MaterialShapeDrawable(
//                        ShapeAppearanceModel.builder()
//                                .setAllCornerSizes(
//                                        TypedValue.applyDimension(
//                                                TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()))
//                                .build()
//                );
//                cancelBg.setFillColor(ColorStateList.valueOf(Color.TRANSPARENT));
//                cancelBg.setStroke(
//                        (int)TypedValue.applyDimension(
//                                TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()),
//                        Color.BLACK
//                );
//                cancelBtn.setBackground(cancelBg);
//                cancelBtn.setTextColor(Color.BLACK);
//
//
//                // --- Button actions ---
//                deleteBtn.setOnClickListener(v1 -> {
//                    String typedEmail = input.getText().toString().trim();
//                    if (typedEmail.isEmpty()) {
//                        input.setError("Please enter your email");
//                    } else if (!typedEmail.equals(currentEmail)) {
//                        input.setError("Email does not match your account");
//                    } else {
//                        // Email matches → delete profile
//                        // deleteUserProfile();
//                        dialog.dismiss();
//                    }
//                });
//
//                cancelBtn.setOnClickListener(v1 -> dialog.dismiss());
//            });
//
//            dialog.show();
//        });
