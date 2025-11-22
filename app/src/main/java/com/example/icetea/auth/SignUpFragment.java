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
import androidx.fragment.app.FragmentTransaction;

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
 * Fragment responsible for handling the user sign-up UI and interactions.
 *
 * Provides fields for email, password, and role selection. Delegates validation
 * and account creation to {@link SignUpController}. On successful sign-up, navigates
 * the user to {@link MainActivity}.
 */
public class SignUpFragment extends Fragment {
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
        controller = new AuthController();

        ImageButton backButton = view.findViewById(R.id.imageButtonSignUpBack);
        Button continueButton = view.findViewById(R.id.buttonContinueSignUp);
        TextInputLayout nameInputLayout = view.findViewById(R.id.inputLayoutNameSignUp);
        TextInputLayout emailInputLayout = view.findViewById(R.id.inputLayoutEmailSignUp);
        TextInputEditText nameEditText = view.findViewById(R.id.inputEditTextNameSignUp);
        TextInputEditText emailEditText = view.findViewById(R.id.inputEditTextEmailSignUp);


        // Back button bounds
        ViewCompat.setOnApplyWindowInsetsListener(backButton, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) v.getLayoutParams();
            params.topMargin = statusBarHeight;
            v.setLayoutParams(params);
            return insets;
        });

        // Back button navigates to previous screen
        backButton.setOnClickListener(v -> {
            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.setReorderingAllowed(true);
            transaction.setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
            );
            transaction.replace(R.id.auth_fragment_container, LandingPageFragment.newInstance());
            transaction.commit();
        });



        continueButton.setOnClickListener(v -> {
            nameInputLayout.setError(null);
            emailInputLayout.setError(null);

            String name = String.valueOf(nameEditText.getText()).trim();
            String email = String.valueOf(emailEditText.getText()).trim();

            // Validate
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