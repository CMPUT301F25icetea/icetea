package com.example.icetea.entrant;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.icetea.R;
import com.example.icetea.organizer.OrganizerCreateEventFragment;
import com.example.icetea.organizer.OrganizerHomeFragment;
import com.example.icetea.organizer.OrganizerProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EntrantFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EntrantFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EntrantFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EntrantFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EntrantFragment newInstance(String param1, String param2) {
        EntrantFragment fragment = new EntrantFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entrant, container, false);
        BottomNavigationView bottomNav = view.findViewById(R.id.entrant_bottom_nav);
        loadFragment(EntrantHomeFragment.newInstance());

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_entrant_home) {
                loadFragment(EntrantHomeFragment.newInstance());
                return true;
            } else if (id == R.id.nav_entrant_history) {
                loadFragment(EntrantHistoryFragment.newInstance());
                return true;
            } else if (id == R.id.nav_entrant_scanner) {
                loadFragment(EntrantScannerFragment.newInstance());
                return true;
            } else if (id == R.id.nav_entrant_profile) {
                loadFragment(EntrantProfileFragment.newInstance());
                return true;
            } else if (id == R.id.nav_entrant_settings) {
                loadFragment(EntrantSettingsFragment.newInstance());
                return true;
            }
            return false;
        });

        return view;
    }
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.entrant_fragment_container, fragment);
        transaction.commit();
    }
}