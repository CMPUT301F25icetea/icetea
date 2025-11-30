package com.example.icetea.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.icetea.MainActivity;
import com.example.icetea.util.Callback;
import com.example.icetea.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Fragment representing the sign-up screen for new users.
 * <p>
 * Provides input fields for name and email, validates user input,
 * and creates a new user account using {@link AuthController}.
 * <p>
 * On successful sign-up, navigates the user to {@link MainActivity}.
 * The fragment also handles back navigation and adjusts the back button
 * position to account for the status bar using edge-to-edge insets.
 */
public class SignUpFragment extends Fragment {

    /**
     * Controller responsible for handling authentication logic.
     */
    private AuthController controller;

    /**
     * Default empty public constructor.
     */
    public SignUpFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment.
     *
     * @return A new instance of {@link SignUpFragment}.
     */
    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater           LayoutInflater object used to inflate views.
     * @param container          Parent ViewGroup.
     * @param savedInstanceState Saved instance state bundle.
     * @return Inflated {@link View}.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView}.
     * <p>
     * Initializes input fields, buttons, and authentication controller.
     * <p>
     * Sets up:
     * <ul>
     *     <li>Back button navigation with status bar insets handling</li>
     *     <li>Continue button click listener to validate input and perform sign-up</li>
     *     <li>Error display for invalid name or email</li>
     *     <li>Navigation to {@link MainActivity} on successful sign-up</li>
     * </ul>
     *
     * @param view               The {@link View} returned by {@link #onCreateView}.
     * @param savedInstanceState Saved state bundle.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = new AuthController();

        ImageButton backButton = view.findViewById(R.id.imageButtonSignUpBack);
        Button continueButton = view.findViewById(R.id.buttonContinueSignUp);
        TextInputLayout nameInputLayout = view.findViewById(R.id.inputLayoutNameSignUp);
        TextInputLayout emailInputLayout = view.findViewById(R.id.inputLayoutEmailSignUp);
        TextInputEditText nameEditText = view.findViewById(R.id.inputEditTextNameSignUp);
        TextInputEditText emailEditText = view.findViewById(R.id.inputEditTextEmailSignUp);

        // Adjust back button for status bar height
        ViewCompat.setOnApplyWindowInsetsListener(backButton, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) v.getLayoutParams();
            params.topMargin = statusBarHeight;
            v.setLayoutParams(params);
            return insets;
        });

        // Handle back navigation
        backButton.setOnClickListener(v -> {
            FragmentManager fm = getParentFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStack();
            }
        });

        // Handle continue button click
        continueButton.setOnClickListener(v -> {
            nameInputLayout.setError(null);
            emailInputLayout.setError(null);

            String name = nameEditText.getText() != null ? nameEditText.getText().toString().trim() : "";
            String email = emailEditText.getText() != null ? emailEditText.getText().toString().trim() : "";

            String nameError = controller.validateName(name);
            String emailError = controller.validateEmail(email);

            boolean hasError = false;

            if (nameError != null) {
                nameInputLayout.setError(nameError);
                hasError = true;
            }

            if (emailError != null) {
                emailInputLayout.setError(emailError);
                hasError = true;
            }

            if (hasError) return;

            controller.signUp(name, email, new Callback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    FragmentActivity activity = getActivity();
                    if (activity != null) {
                        startActivity(new Intent(activity, MainActivity.class));
                        activity.finish();
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}