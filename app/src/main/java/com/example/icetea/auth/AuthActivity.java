package com.example.icetea.auth;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.icetea.R;

/**
 * AuthActivity serves as the entry point for authentication.
 *
 * It sets up the layout, applies edge-to-edge system bar insets, and initializes
 * the landing page fragment.
 */
public class AuthActivity extends AppCompatActivity {
    /**
     * Called when the activity is starting. Initializes the UI and loads the landing page fragment.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down then this Bundle contains the data it most recently
     *                           supplied; otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);

        // Load the landing page fragment
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.auth_fragment_container, LandingPageFragment.newInstance())
                .commit();
    }
}