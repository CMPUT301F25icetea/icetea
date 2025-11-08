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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.icetea.MainActivity;
import com.example.icetea.util.Callback;
import com.example.icetea.util.NavigationHelper;
import com.example.icetea.R;

/**
 * Fragment responsible for handling the user sign-up UI and interactions.
 *
 * Provides fields for email, password, and role selection. Delegates validation
 * and account creation to {@link SignUpController}. On successful sign-up, navigates
 * the user to {@link MainActivity}.
 */
public class SignUpFragment extends Fragment {
    private SignUpController controller;

    /**
     * Default empty public constructor.
     */
    public SignUpFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment.
     *
     * @return A new instance of SignUpFragment.
     */
    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the controller
        controller = new SignUpController();

        // UI elements
        Button backButton = view.findViewById(R.id.buttonSignInBack);
        Button signUpButton = view.findViewById(R.id.buttonSignUp);
        EditText emailEditText = view.findViewById(R.id.signUpEmailAddress);
        EditText passwordEditText = view.findViewById(R.id.signUpPassword);
        RadioGroup roleRadioGroup = view.findViewById(R.id.roleRadioGroup);

        // Back button navigates to previous screen
        backButton.setOnClickListener(v -> {
            NavigationHelper.goBack(this);
        });

        // Sign-up button handles validation and account creation
        signUpButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            int selectedId = roleRadioGroup.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = view.findViewById(selectedId);
            String role = selectedRadioButton.getText().toString().toLowerCase();

            // Validate input
            String inputErrorMessage = controller.validateInput(email, password, role);
            if (inputErrorMessage != null) {
                Toast.makeText(getContext(), inputErrorMessage, Toast.LENGTH_SHORT).show();
                return;
            }

            // Perform sign-up
            controller.signUp(email, password, role, new Callback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    // Navigate to MainActivity on successful sign-up
                    NavigationHelper.openActivity(SignUpFragment.this, MainActivity.class);
                }

                @Override
                public void onFailure(Exception e) {
                    // Show error message if sign-up fails
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
