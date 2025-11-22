package com.example.icetea.auth;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.icetea.MainActivity;
import com.example.icetea.util.Callback;
import com.example.icetea.util.NavigationHelper;
import com.example.icetea.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Fragment responsible for displaying the login screen and handling user login.
 *
 * Handles input validation, user interaction, and calls LoginController for authentication.
 */
public class LoginFragment extends Fragment {
    private LoginController controller;

    /**
     * Default empty constructor.
     */
    public LoginFragment() {
    }

    /**
     * Factory method to create a new instance of this fragment.
     *
     * @return A new instance of LoginFragment.
     */
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Inflates the login fragment layout.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState Saved state information.
     * @return The root view of the inflated layout.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    /**
     * Called immediately after onCreateView().
     * Sets up UI components and event listeners for login and back navigation.
     *
     * @param view               The root view returned by onCreateView().
     * @param savedInstanceState Saved state information.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = new LoginController();
        ImageButton backButton = view.findViewById(R.id.imageButtonLoginBack);
        Button loginButton = view.findViewById(R.id.buttonLogin);
        TextInputLayout emailInputLayout = view.findViewById(R.id.inputLayoutEmailLogin);
        TextInputEditText emailEditText = view.findViewById(R.id.inputEditTextEmailLogin);
        TextInputLayout passwordInputLayout = view.findViewById(R.id.inputLayoutPasswordLogin);
        TextInputEditText passwordEditText = view.findViewById(R.id.inputEditTextPasswordLogin);
        TextView registerTextView = view.findViewById(R.id.textViewGoToSignUpFromLogin);

        // make sure back button doesn't go behind system bars
        ViewCompat.setOnApplyWindowInsetsListener(backButton, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) v.getLayoutParams();
            params.topMargin = statusBarHeight;
            v.setLayoutParams(params);
            return insets;
        });

        // edit the string at the very bottom
        String text = "Don't have an account? Register";
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(
                new StyleSpan(Typeface.BOLD),
                0, text.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        int start = text.indexOf("Register");
        int end = start + "Register".length();
        spannable.setSpan(
                new ForegroundColorSpan(Color.parseColor("#4A90E2")),
                start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        registerTextView.setText(spannable);

        // password visibility toggle
        final boolean[] isPasswordVisible = {false};
        passwordInputLayout.setEndIconOnClickListener(v -> {
            if (isPasswordVisible[0]) {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordInputLayout.setEndIconDrawable(R.drawable.visibility_off_icon);
                isPasswordVisible[0] = false;
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordInputLayout.setEndIconDrawable(R.drawable.visibility_icon);
                isPasswordVisible[0] = true;
            }

            if (passwordEditText.getText() != null) {
                passwordEditText.setSelection(passwordEditText.getText().length());
            }
        });

        // back button
        backButton.setOnClickListener(v -> {
            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.setReorderingAllowed(true);
            transaction.setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
            );
            transaction.replace(R.id.entry_fragment_container, LandingPageFragment.newInstance());
            transaction.commit();
        });

        // Handle login button click
        loginButton.setOnClickListener(v -> {
            String email = String.valueOf(emailEditText.getText()).trim();
            String password = String.valueOf(passwordEditText.getText()).trim();

            //validate input
            emailInputLayout.setError(null);
            passwordInputLayout.setError(null);

            boolean hasError = false;
            String emailError = controller.validateEmail(email);
            if (emailError != null) {
                emailInputLayout.setError(emailError);
                hasError = true;
            }

            String passwordError = controller.validatePasswordLogin(password);
            if (passwordError != null) {
                passwordInputLayout.setError(passwordError);
                hasError = true;
            }

            if (hasError) return;

            // Attempt login
            controller.login(email, password, new Callback<Void>() {
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
                    Log.e("LoginFragment", "Login failed", e);
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "Unknown error. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        registerTextView.setOnClickListener(v -> {
            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.setReorderingAllowed(true);
            transaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
            );
            transaction.replace(R.id.entry_fragment_container, SignUpFragment.newInstance());
            transaction.commit();
        });
    }
}