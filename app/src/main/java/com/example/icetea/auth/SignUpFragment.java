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
import com.example.icetea.util.NavigationHelper;
import com.example.icetea.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {
    private SignUpController controller;

    public SignUpFragment() {
        // Required empty public constructor
    }

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
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = new SignUpController();

        Button backButton = view.findViewById(R.id.buttonSignInBack);
        Button signUpButton = view.findViewById(R.id.buttonSignUp);

        EditText emailEditText = view.findViewById(R.id.signUpEmailAddress);
        EditText passwordEditText = view.findViewById(R.id.signUpPassword);

        RadioGroup roleRadioGroup = view.findViewById(R.id.roleRadioGroup);

        backButton.setOnClickListener(v -> {
            NavigationHelper.goBack(this);
        });

        signUpButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            int selectedId = roleRadioGroup.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = view.findViewById(selectedId);
            String role = selectedRadioButton.getText().toString();

            String inputErrorMessage = controller.validateInput(email, password, role);
            if (inputErrorMessage != null) {
                Toast.makeText(getContext(), inputErrorMessage, Toast.LENGTH_SHORT).show();
                return;
            }

            controller.signUp(email, password, role, new SignUpController.SignUpCallback() {
                @Override
                public void onSuccess() {
                    //Toast.makeText(getContext(), "Signed up as " + role, Toast.LENGTH_SHORT).show();
                    NavigationHelper.openActivity(SignUpFragment.this, MainActivity.class);
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}