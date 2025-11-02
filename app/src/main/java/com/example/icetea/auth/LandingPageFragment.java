package com.example.icetea.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.icetea.util.NavigationHelper;
import com.example.icetea.R;

public class LandingPageFragment extends Fragment {

    public LandingPageFragment() {
        // Required empty public constructor
    }

    public static LandingPageFragment newInstance() {
        return new LandingPageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_landing_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button loginButton = view.findViewById(R.id.buttonGoToLogin);
        Button signUpButton = view.findViewById(R.id.buttonGoToSignUp);

        loginButton.setOnClickListener(v -> {
            NavigationHelper.replaceFragment(getParentFragmentManager(), R.id.entry_fragment_container, LoginFragment.newInstance(), true);
        });

        signUpButton.setOnClickListener(v -> {
            NavigationHelper.replaceFragment(getParentFragmentManager(), R.id.entry_fragment_container, SignUpFragment.newInstance(), true);
        });

    }
}