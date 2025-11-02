package com.example.icetea;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.icetea.entrant.EntrantContainerFragment;
import com.example.icetea.organizer.OrganizerContainerFragment;
import com.example.icetea.util.NavigationHelper;

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
        //TODO: CHECK USER'S HIGHEST ROLE BEFORE SETTING FRAGMENT, SET IT TO THAT ROLE
        NavigationHelper.replaceFragment(getSupportFragmentManager(), R.id.main, OrganizerContainerFragment.newInstance(), false);


    }
}