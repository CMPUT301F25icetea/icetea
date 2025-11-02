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

public class LoginFragment extends Fragment {
    private LoginController controller;

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

        controller = new LoginController();
        Button backButton = view.findViewById(R.id.buttonLoginBack);
        Button loginButton = view.findViewById(R.id.buttonLogin);

        EditText emailEditText = view.findViewById(R.id.loginEmailAddress);
        EditText passwordEditText = view.findViewById(R.id.loginPassword);

        backButton.setOnClickListener(v -> {
            NavigationHelper.goBack(this);
        });

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            String inputErrorMessage = controller.validateInput(email, password);
            if (inputErrorMessage != null) {
                Toast.makeText(getContext(), inputErrorMessage, Toast.LENGTH_SHORT).show();
                return;
            }

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