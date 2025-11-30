package com.example.icetea;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.icetea.auth.CurrentUser;
import com.example.icetea.history.HistoryFragment;
import com.example.icetea.home.HomeFragment;
import com.example.icetea.notifications.NotificationsFragment;
import com.example.icetea.notifications.NotificationsViewModel;
import com.example.icetea.profile.ProfileFragment;
import com.example.icetea.scanner.QRScannerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


/**
 * Main entry point for the IceTea application. Determines the current user's role
 * and navigates to the appropriate container fragment (entrant or organizer).
 */
public class MainActivity extends AppCompatActivity {
    NotificationsViewModel viewModel;
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
            loadFragment(HomeFragment.newInstance());
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int id = item.getItemId();
            if (id == R.id.nav_home) {
                selectedFragment = HomeFragment.newInstance();
            } else if (id == R.id.nav_history) {
                selectedFragment = HistoryFragment.newInstance();
            } else if (id == R.id.nav_notifications) {
                selectedFragment = NotificationsFragment.newInstance();
            } else if (id == R.id.nav_profile) {
                selectedFragment = ProfileFragment.newInstance();
            } else if (id == R.id.nav_scanner) {
                selectedFragment = QRScannerFragment.newInstance();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });

        viewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);
        String userId = CurrentUser.getInstance().getFid();
        viewModel.startListening(userId);

        viewModel.getNewNotificationEvent().observe(this, notification -> {
            if (notification != null) {
                showBanner(notification.getTitle() + ": " + notification.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.stopListening();
    }
    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setReorderingAllowed(true);

        transaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
        );

        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.commit();
    }

    private void showBanner(String message) {
        LinearLayout banner = findViewById(R.id.banner_notification);
        TextView bannerText = findViewById(R.id.banner_text);
        ImageView bannerClose = findViewById(R.id.banner_close);

        bannerText.setText(message);

        banner.setVisibility(View.VISIBLE);

        banner.animate()
                .translationY(0)
                .setDuration(300)
                .start();

        banner.postDelayed(this::hideBanner, 2000);

        bannerClose.setOnClickListener(v -> hideBanner());
    }
    private void hideBanner() {
        LinearLayout banner = findViewById(R.id.banner_notification);
        banner.animate()
                .translationY(-banner.getHeight())
                .setDuration(300)
                .withEndAction(() -> banner.setVisibility(View.GONE))
                .start();
    }

}