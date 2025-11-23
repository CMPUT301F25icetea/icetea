package com.example.icetea.organizer;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.icetea.profile.ProfileFragment;
import com.example.icetea.util.NavigationHelper;
import com.example.icetea.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * A container fragment that manages organizer navigation using a BottomNavigationView.
 * It switches between Home, Create Event, and Profile sections for the organizer.
 */
public class OrganizerContainerFragment extends Fragment {

    /**
     * Default empty constructor.
     */
    public OrganizerContainerFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of OrganizerContainerFragment.
     *
     * @return a new instance of OrganizerContainerFragment
     */
    public static OrganizerContainerFragment newInstance() {
        return new OrganizerContainerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Inflates the fragment layout, sets up the BottomNavigationView,
     * and handles navigation between organizer sections.
     *
     * @param inflater  the LayoutInflater object to inflate views in the fragment
     * @param container the parent view that the fragment’s UI should be attached to
     * @param savedInstanceState if non-null, this fragment is being re-created from a previous saved state
     * @return the root View for this fragment’s layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer, container, false);

        BottomNavigationView bottomNav = view.findViewById(R.id.organizer_bottom_nav);
        NavigationHelper.replaceFragment(getChildFragmentManager(), R.id.organizer_fragment_container, OrganizerHomeFragment.newInstance(), false);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_organizer_home) {
                NavigationHelper.replaceFragment(getChildFragmentManager(), R.id.organizer_fragment_container, OrganizerHomeFragment.newInstance(), false);
                return true;
            } else if (id == R.id.nav_organizer_create_event) {
                NavigationHelper.replaceFragment(getChildFragmentManager(), R.id.organizer_fragment_container, OrganizerCreateEventFragment.newInstance(), false);
                return true;
            } else if (id == R.id.nav_organizer_profile) {
                //NavigationHelper.replaceFragment(getChildFragmentManager(), R.id.organizer_fragment_container, ProfileFragment.newInstance(), false);
                return true;
            }
            return false;
        });

        return view;
    }
}
