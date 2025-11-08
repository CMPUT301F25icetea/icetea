package com.example.icetea.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.icetea.MainActivity;
import com.example.icetea.util.Callback;
import com.example.icetea.util.NavigationHelper;
import com.example.icetea.R;

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
        // Required empty public constructor
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
     * @param inflater The LayoutInflater object that can be used to inflate views.
     * @param container The parent view that the fragment's UI should be attached to.
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
     * @param view The root view returned by onCreateView().
     * @param savedInstanceState Saved state information.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = new LoginController();
        Button backButton = view.findViewById(R.id.buttonLoginBack);
        Button loginButton = view.findViewById(R.id.buttonLogin);

        EditText emailEditText = view.findViewById(R.id.loginEmailAddress);
        EditText passwordEditText = view.findViewById(R.id.loginPassword);

        // Handle back navigation
        backButton.setOnClickListener(v -> {
            NavigationHelper.goBack(this);
        });

        // Handle login button click
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Validate user input
            String inputErrorMessage = controller.validateInput(email, password);
            if (inputErrorMessage != null) {
                Toast.makeText(getContext(), inputErrorMessage, Toast.LENGTH_SHORT).show();
                return;
            }

            // Attempt login
            controller.login(email, password, new Callback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    NavigationHelper.openActivity(LoginFragment.this, MainActivity.class);
                }
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
