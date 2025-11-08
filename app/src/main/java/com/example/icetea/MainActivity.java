package com.example.icetea;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.icetea.auth.FBAuthenticator;
import com.example.icetea.auth.LoginFragment;
import com.example.icetea.entrant.EntrantContainerFragment;
import com.example.icetea.models.User;
import com.example.icetea.models.UserDB;
import com.example.icetea.organizer.OrganizerContainerFragment;
import com.example.icetea.util.Callback;
import com.example.icetea.util.NavigationHelper;
import com.google.firebase.firestore.DocumentSnapshot;

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

        // Adjust padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the current user's top role and navigate accordingly
        UserDB.getInstance().getUserTopRole(FBAuthenticator.getCurrentUserId(), new Callback<String>() {
            @Override
            public void onSuccess(String role) {
                if ("entrant".equals(role)) {
                    NavigationHelper.replaceFragment(
                            getSupportFragmentManager(),
                            R.id.main,
                            EntrantContainerFragment.newInstance(),
                            false
                    );
                } else if ("organizer".equals(role)) {
                    NavigationHelper.replaceFragment(
                            getSupportFragmentManager(),
                            R.id.main,
                            OrganizerContainerFragment.newInstance(),
                            false
                    );
                } else {
                    Toast.makeText(MainActivity.this, "Error retrieving role: " + role, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MainActivity.this, "Failed to get user role: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}