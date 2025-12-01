package com.example.icetea.history;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
 * Fragment that displays a tabbed history view of events.
 * <p>
 * Contains two tabs: "Completed" and "Active" events.
 * Uses a ViewPager2 with a FragmentStateAdapter to manage the fragments for each tab.
 */
public class HistoryFragment extends Fragment {

    /**
     * Required empty public constructor.
     */
    public HistoryFragment() {
    }

    /**
     * Factory method to create a new instance of HistoryFragment.
     *
     * @return A new instance of HistoryFragment.
     */
    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Inflates the fragment layout and sets up the ViewPager2 and TabLayout for tabs.
     *
     * @param inflater           LayoutInflater to inflate views
     * @param container          Optional parent container
     * @param savedInstanceState Saved state bundle
     * @return The root view of the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Initialize ViewPager2 and TabLayout
        ViewPager2 viewPager = view.findViewById(R.id.viewPagerHistory);
        TabLayout tabs = view.findViewById(R.id.tabsHistory);

        // Adapter to manage fragments for each tab
        FragmentStateAdapter adapter = new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                // Position 0 -> Completed Events, Position 1 -> Active Events
                if (position == 0) return new CompletedEventsFragment();
                else return new ActiveEventsFragment();
            }

            @Override
            public int getItemCount() {
                return 2; // Two tabs: Completed and Active
            }
        };

        viewPager.setAdapter(adapter);

        // Link the TabLayout and ViewPager2
        new TabLayoutMediator(tabs, viewPager,
                (tab, position) -> tab.setText(position == 0 ? "Completed" : "Active")
        ).attach();

        return view;
    }
}