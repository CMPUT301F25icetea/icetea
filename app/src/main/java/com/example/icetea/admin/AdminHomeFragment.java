package com.example.icetea.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.icetea.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Fragment representing the Admin Home screen with a tab layout.
 * <p>
 * Provides four tabs: Profiles, Events, Images, and Notifications.
 * Each tab hosts a separate fragment. Also includes a back button to return to the previous screen.
 */
public class AdminHomeFragment extends Fragment {

    /**
     * Default public constructor (required).
     */
    public AdminHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of AdminHomeFragment.
     *
     * @return a new instance of AdminHomeFragment
     */
    public static AdminHomeFragment newInstance() {
        return new AdminHomeFragment();
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           LayoutInflater object to inflate views
     * @param container          parent view that the fragment's UI should attach to
     * @param savedInstanceState saved state of the fragment
     * @return the root view for the fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_home, container, false);
    }

    /**
     * Called immediately after onCreateView() has returned.
     * Sets up the back button, ViewPager2, and TabLayout with four tabs and their respective fragments.
     *
     * @param view               the View returned by onCreateView
     * @param savedInstanceState saved state of the fragment
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Back button listener to pop the fragment back stack
        ImageButton backButton = view.findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        // Setup ViewPager2 and TabLayout
        ViewPager2 viewPager = view.findViewById(R.id.viewPagerHome);
        TabLayout tabLayout = view.findViewById(R.id.tabsHome);

        FragmentStateAdapter adapter = new FragmentStateAdapter(this) {
            /**
             * Creates the fragment associated with a specified position.
             *
             * @param position the position of the tab/page
             * @return the fragment corresponding to the position
             */
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0: return AdminProfilesFragment.newInstance();
                    case 1: return AdminEventsFragment.newInstance();
                    case 2: return AdminImagesFragment.newInstance();
                    case 3: return AdminNotificationsFragment.newInstance();
                    default: return new Fragment();
                }
            }

            /**
             * Returns the total number of pages/tabs.
             *
             * @return total number of tabs (4)
             */
            @Override
            public int getItemCount() {
                return 4;
            }
        };

        viewPager.setAdapter(adapter);

        // Attach TabLayout with ViewPager2 and set tab titles
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0: tab.setText("Profiles"); break;
                        case 1: tab.setText("Events"); break;
                        case 2: tab.setText("Images"); break;
                        case 3: tab.setText("Notifications"); break;
                    }
                }
        ).attach();
    }
}
