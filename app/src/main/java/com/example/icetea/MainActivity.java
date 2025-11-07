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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
