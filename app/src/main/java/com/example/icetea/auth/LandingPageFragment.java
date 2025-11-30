package com.example.icetea.auth;

import android.content.Intent;
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
import com.example.icetea.R;

/**
 * Fragment representing the landing page of the app.
 * <p>
 * Displays login and sign-up buttons for users who are not authenticated.
 * If a user is already logged in, the fragment can redirect them to {@link MainActivity}.
 * <p>
 * Provides navigation to the sign-up screen with animated fragment transitions.
 */
public class LandingPageFragment extends Fragment {

    /**
     * Required empty public constructor.
     */
    public LandingPageFragment() {
        // Required empty constructor
    }

    /**
     * Factory method to create a new instance of this fragment.
     *
     * @return A new instance of {@link LandingPageFragment}.
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
     * @param inflater           The LayoutInflater object that can be used to inflate any views.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState Saved state bundle.
     * @return The inflated {@link View}.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_landing_page, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView}.
     * <p>
     * Sets up button click listeners for navigation to other authentication fragments.
     * Specifically, clicking the sign-up button navigates to {@link SignUpFragment}
     * using animated fragment transitions.
     *
     * @param view               The View returned by {@link #onCreateView}.
     * @param savedInstanceState Saved state bundle.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button signUpButton = view.findViewById(R.id.buttonGoToSignUpFromLandingPage);

        signUpButton.setOnClickListener(v -> {
            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.setReorderingAllowed(true);
            transaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
            );
            transaction.replace(R.id.auth_fragment_container, SignUpFragment.newInstance());
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }
}