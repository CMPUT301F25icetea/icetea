package com.example.icetea.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.icetea.MainActivity;
import com.example.icetea.util.NavigationHelper;
import com.example.icetea.R;

/**
 * Fragment representing the landing page of the app.
 *
 * Shows login and sign-up buttons for users who are not logged in.
 * If the user is already logged in, navigates directly to the MainActivity.
 */
public class LandingPageFragment extends Fragment {

    /**
     * Required empty public constructor.
     */
    public LandingPageFragment() {
    }

    /**
     * Factory method to create a new instance of this fragment.
     *
     * @return A new instance of LandingPageFragment.
     */
    public static LandingPageFragment newInstance() {
        return new LandingPageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater The LayoutInflater object.
     * @param container The parent ViewGroup.
     * @param savedInstanceState Saved state bundle.
     * @return The inflated View.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_landing_page, container, false);
    }

    /**
     * Called immediately after onCreateView().
     *
     * Checks if the user is logged in and navigates to MainActivity if so.
     * Otherwise, sets up the login and sign-up buttons to navigate to the
     * appropriate fragments.
     *
     * @param view The View returned by onCreateView.
     * @param savedInstanceState Saved state bundle.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Check if user is logged in
        if (FBAuthenticator.isLoggedIn()) {
            NavigationHelper.openActivity(this, MainActivity.class);
            return;
        }

        // Set up login and sign-up buttons
        Button loginButton = view.findViewById(R.id.buttonGoToLoginFromLandingPage);
        Button signUpButton = view.findViewById(R.id.buttonGoToSignUpFromLandingPage);

        loginButton.setOnClickListener(v -> {
            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.setReorderingAllowed(true);
            transaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
            );
            transaction.replace(R.id.entry_fragment_container, LoginFragment.newInstance());
            transaction.commit();
        });

        signUpButton.setOnClickListener(v -> {
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

        // add deviceonclicklistener

    }
}