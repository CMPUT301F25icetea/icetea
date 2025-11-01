package com.example.icetea.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.icetea.FBAuthenticator;
import com.example.icetea.MainActivity;
import com.example.icetea.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    private EditText emailEditText, passwordEditText;
    private Button loginButton, backButton;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        FBAuthenticator.logout();
        backButton = view.findViewById(R.id.buttonLoginBack);
        emailEditText = view.findViewById(R.id.loginEmailAddress);
        passwordEditText = view.findViewById(R.id.loginPassword);
        loginButton = view.findViewById(R.id.buttonLogin);

        backButton.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getActivity(), "Email and/or password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(getActivity(), "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            }

            FBAuthenticator.loginUser(email, password, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful() && getActivity() != null) {
                        startMainActivity();
                    } else if (getActivity() != null) {
                        Toast.makeText(getActivity(), "Authentication failed", Toast.LENGTH_SHORT).show();
                        Exception e = task.getException();
                        e.printStackTrace();
                    }
                }
            });
        });

    }

    //bad code since repeated in entryactivity and signupfragment
    // will try to figure out how to implement this without breaking encapsulation
    private void startMainActivity() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }


}