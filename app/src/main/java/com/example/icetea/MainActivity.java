package com.example.icetea;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.icetea.auth.LandingPageFragment;
import com.example.icetea.main.HistoryFragment;
import com.example.icetea.main.HomeFragment;
import com.example.icetea.main.NotificationsFragment;
import com.example.icetea.main.ProfileFragment;
import com.example.icetea.main.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Main entry point for the IceTea application. Determines the current user's role
 * and navigates to the appropriate container fragment (entrant or organizer).
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Called when the activity is starting. Sets up edge-to-edge layout and determines
     * the user's role to navigate to the correct container fragment.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                           being shut down, this contains the data it most recently
     *                           supplied. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        if (savedInstanceState == null) {
            loadFragment(HomeFragment.newInstance("a", "a"));
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();
            if (id == R.id.nav_home) {
                selectedFragment = HomeFragment.newInstance("a", "a");
            } else if (id == R.id.nav_history) {
                selectedFragment = HistoryFragment.newInstance("a", "a");
            } else if (id == R.id.nav_notifications) {
                selectedFragment = NotificationsFragment.newInstance("a", "a");
            } else if (id == R.id.nav_profile) {
                selectedFragment = ProfileFragment.newInstance("a", "a");
            } else if (id == R.id.nav_settings) {
                selectedFragment = SettingsFragment.newInstance("a", "a");
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
}
        private void loadFragment(Fragment fragment) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment_container, fragment)
                    .commit();
        }

}