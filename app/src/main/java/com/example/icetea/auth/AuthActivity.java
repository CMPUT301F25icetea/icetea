package com.example.icetea.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.icetea.MainActivity;
import com.example.icetea.R;
import com.example.icetea.models.UserDB;
import com.example.icetea.util.Callback;
import com.example.icetea.util.ImageUtil;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * AuthActivity serves as the entry point for user authentication.
 * <p>
 * Responsibilities:
 * <ul>
 *     <li>Set up the layout for authentication.</li>
 *     <li>Enable edge-to-edge system bar insets.</li>
 *     <li>Check if a user already exists on this device.</li>
 *     <li>Redirect existing users to {@link MainActivity}.</li>
 *     <li>Show landing page fragment for new users.</li>
 * </ul>
 */
public class AuthActivity extends AppCompatActivity {

    /** Controller responsible for authentication logic */
    AuthController controller;

    /**
     * Called when the activity is starting.
     * <p>
     * Sets up the layout, applies edge-to-edge insets, checks if a user exists, and
     * either launches the main activity for existing users or shows the landing page fragment.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down then this Bundle contains the data it most recently
     *                           supplied; otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge system bar insets
        EdgeToEdge.enable(this);

        // Set the activity layout
        setContentView(R.layout.activity_auth);

        controller = new AuthController();

        // Check if user exists on this device
        controller.userExists(new Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean exists) {
                if (exists) {
                    // Load existing user data from Firestore
                    String fid = FBInstallations.getFid();
                    UserDB.getInstance().getUser(fid, task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot doc = task.getResult();

                            String name = doc.getString("name");
                            String email = doc.getString("email");
                            String phone = doc.getString("phone");
                            boolean notifications = Boolean.TRUE.equals(doc.getBoolean("notifications"));
                            String avatar = doc.getString("avatar");

                            // Populate CurrentUser singleton
                            CurrentUser user = CurrentUser.getInstance();
                            user.setFid(fid);
                            user.setName(name);
                            user.setEmail(email);
                            user.setPhone(phone);
                            user.setNotifications(notifications);
                            user.setAvatar(ImageUtil.base64ToBitmap(avatar));

                            // Launch MainActivity and clear back stack
                            Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(AuthActivity.this, "Failed to query user data", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Show landing page fragment for new users
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.auth_fragment_container, LandingPageFragment.newInstance())
                            .commit();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(AuthActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}