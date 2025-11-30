package com.example.icetea.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.icetea.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * HomeFragment serves as the main landing page of the app for the user.
 * It displays two tabs using a {@link ViewPager2} and {@link TabLayout}:
 * <ul>
 *     <li>All Events – displays all available events</li>
 *     <li>My Events – displays events created by the current user</li>
 * </ul>
 * The fragment uses a {@link FragmentStateAdapter} to manage the tab fragments.
 */
public class HomeFragment extends Fragment {

    /**
     * Required empty public constructor.
     */
    public HomeFragment() {
    }

    /**
     * Factory method to create a new instance of HomeFragment.
     *
     * @return A new instance of HomeFragment.
     */
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    /**
     * Called when the fragment is being created.
     *
     * @param savedInstanceState If non-null, this fragment is being re-created from a previous saved state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // No additional initialization required here
    }

    /**
     * Inflates the layout for the fragment.
     *
     * @param inflater The LayoutInflater object to inflate views
     * @param container If non-null, the parent view the fragment's UI should be attached to
     * @param savedInstanceState If non-null, the fragment is being re-created from a previous state
     * @return The root view of the inflated layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    /**
     * Called immediately after onCreateView. Sets up the {@link ViewPager2} and {@link TabLayout}.
     * The ViewPager uses a {@link FragmentStateAdapter} with two fragments: AllEventsFragment and MyEventsFragment.
     *
     * @param view The view returned by onCreateView
     * @param savedInstanceState If non-null, this fragment is being re-created from a previous state
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewPager2 and TabLayout
        ViewPager2 viewPager = view.findViewById(R.id.viewPagerHome);
        TabLayout tabs = view.findViewById(R.id.tabsHome);

        // Adapter for managing fragments in ViewPager
        FragmentStateAdapter adapter = new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                if (position == 0) return new AllEventsFragment();
                else return new MyEventsFragment();
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        };

        viewPager.setAdapter(adapter);

        // Connect TabLayout with ViewPager2 and set tab titles
        new TabLayoutMediator(tabs, viewPager,
                (tab, position) -> tab.setText(position == 0 ? "All Events" : "My Events")
        ).attach();
    }
}