package com.example.icetea.entrant;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.icetea.util.NavigationHelper;
import com.example.icetea.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EntrantContainerFragment extends Fragment {

    public EntrantContainerFragment() {
        // Required empty public constructor
    }

    public static EntrantContainerFragment newInstance() {
        return new EntrantContainerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrant, container, false);

        BottomNavigationView bottomNav = view.findViewById(R.id.entrant_bottom_nav);
        NavigationHelper.replaceFragment(getChildFragmentManager(), R.id.entrant_fragment_container, EntrantHomeFragment.newInstance(), false);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_entrant_home) {
                NavigationHelper.replaceFragment(getChildFragmentManager(), R.id.entrant_fragment_container, EntrantHomeFragment.newInstance(), false);
                return true;
            } else if (id == R.id.nav_entrant_history) {
                NavigationHelper.replaceFragment(getChildFragmentManager(), R.id.entrant_fragment_container, EntrantHistoryFragment.newInstance(), false);
                return true;
            } else if (id == R.id.nav_entrant_scanner) {
                NavigationHelper.replaceFragment(getChildFragmentManager(), R.id.entrant_fragment_container, EntrantScannerFragment.newInstance(), false);
                return true;
            } else if (id == R.id.nav_entrant_profile) {
                NavigationHelper.replaceFragment(getChildFragmentManager(), R.id.entrant_fragment_container, ProfileFragment.newInstance(), false);
                return true;
            } else if (id == R.id.nav_entrant_settings) {
                NavigationHelper.replaceFragment(getChildFragmentManager(), R.id.entrant_fragment_container, EntrantSettingsFragment.newInstance(), false);
                return true;
            }
            return false;
        });
        return view;
    }
}