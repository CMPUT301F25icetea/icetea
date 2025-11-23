package com.example.icetea.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.example.icetea.MainActivity;
import com.example.icetea.R;
import com.example.icetea.models.UserDB;
import com.example.icetea.util.Callback;
import com.example.icetea.util.ImageUtil;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

/**
 * AuthActivity serves as the entry point for authentication.
 *
 * It sets up the layout, applies edge-to-edge system bar insets, and initializes
 * the landing page fragment.
 */
public class AuthActivity extends AppCompatActivity {
    AuthController controller;
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

        controller = new AuthController();

        controller.userExists(new Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean exists) {
                if (exists) {
                    String fid = FBInstallations.getFid();
                    UserDB.getInstance().getUser(FBInstallations.getFid(), task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot doc = task.getResult();

                            String name = doc.getString("name");
                            String email = doc.getString("email");
                            String phone = doc.getString("phone");
                            boolean notifications = Boolean.TRUE.equals(doc.getBoolean("notifications"));
                            String avatar = doc.getString("avatar");
                            CurrentUser user = CurrentUser.getInstance();
                            user.setFid(fid);
                            user.setName(name);
                            user.setEmail(email);
                            user.setPhone(phone);
                            user.setNotifications(notifications);
                            user.setAvatar(ImageUtil.base64ToBitmap(avatar));
                            startActivity(new Intent(AuthActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(AuthActivity.this, "Failed to query user data", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
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