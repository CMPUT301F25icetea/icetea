package com.example.icetea.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.icetea.MainActivity;
import com.example.icetea.util.Callback;
import com.example.icetea.util.NavigationHelper;
import com.example.icetea.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
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

        ImageButton backButton = view.findViewById(R.id.imageButtonSignUpBack);
        Button continueButton = view.findViewById(R.id.buttonContinueSignUp);
        TextInputLayout nameInputLayout = view.findViewById(R.id.inputLayoutNameSignUp);
        TextInputLayout emailInputLayout = view.findViewById(R.id.inputLayoutEmailSignUp);
        TextInputEditText nameEditText = view.findViewById(R.id.inputEditTextNameSignUp);
        TextInputEditText emailEditText = view.findViewById(R.id.inputEditTextEmailSignUp);
        Button entrantRoleButton = view.findViewById(R.id.buttonEntrantSignUp);
        Button organizerRoleButton = view.findViewById(R.id.buttonOrganizerSignUp);

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
            transaction.replace(R.id.entry_fragment_container, LandingPageFragment.newInstance());
            transaction.commit();
        });

        // Track selected role
        final MaterialButton[] selectedButton = new MaterialButton[1]; // Initially null

        // Utility to update selection
        View.OnClickListener roleClickListener = v -> {
            MaterialButton clicked = (MaterialButton) v;

            // Reset previous selection
            if (selectedButton[0] != null) {
                selectedButton[0].setBackgroundTintList(
                        ContextCompat.getColorStateList(requireContext(), R.color.button_role_unselected)
                );
                selectedButton[0].setScaleX(1f);
                selectedButton[0].setScaleY(1f);
            }

            // Set new selection
            clicked.setBackgroundTintList(
                    ContextCompat.getColorStateList(requireContext(), R.color.darkerBeige)
            );
            clicked.setScaleX(1.1f); // 10% bigger
            clicked.setScaleY(1.1f);

            selectedButton[0] = clicked;
        };

        // Set click listeners
        entrantRoleButton.setOnClickListener(roleClickListener);
        organizerRoleButton.setOnClickListener(roleClickListener);


        // Sign-up button handles validation and account creation
        continueButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = "123456";//passwordEditText.getText().toString().trim();
            String role = "";

            if (selectedButton[0] != null) {
                int id = selectedButton[0].getId();
                if (id == R.id.buttonEntrantSignUp) {
                    // Entrant is selected
                    role = "entrant";
                    Log.d("Role", "Entrant selected");
                } else if (id == R.id.buttonOrganizerSignUp) {
                    // Organizer is selected
                    Log.d("Role", "Organizer selected");
                    role = "organizer";
                }
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
