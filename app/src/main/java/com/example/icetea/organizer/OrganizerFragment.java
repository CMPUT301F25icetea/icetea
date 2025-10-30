package com.example.icetea.organizer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.icetea.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrganizerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrganizerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrganizerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrganizerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrganizerFragment newInstance(String param1, String param2) {
        OrganizerFragment fragment = new OrganizerFragment();
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

        View view = inflater.inflate(R.layout.fragment_organizer, container, false);
        BottomNavigationView bottomNav = view.findViewById(R.id.organizer_bottom_nav);
        loadFragment(OrganizerHomeFragment.newInstance());

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_organizer_home) {
                loadFragment(OrganizerHomeFragment.newInstance());
                return true;
            } else if (id == R.id.nav_organizer_create_event) {
                loadFragment(OrganizerCreateEventFragment.newInstance());
                return true;
            } else if (id == R.id.nav_organizer_profile) {
                loadFragment(OrganizerProfileFragment.newInstance());
                return true;
            }
            return false;
        });
        return view;
    }
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.organizer_fragment_container, fragment);
        transaction.commit();
    }
}