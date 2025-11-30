package com.example.icetea.notifications;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.icetea.R;
import com.example.icetea.home.EventDetailsFragment;

import java.util.ArrayList;

/**
 * Fragment that displays a list of notifications for the current user.
 *
 * <p>Uses a {@link RecyclerView} with {@link NotificationsAdapter} to show notifications.
 * Clicking a notification navigates to the related event details.</p>
 *
 * <p>Observes {@link NotificationsViewModel} to update the list in real-time.</p>
 */
public class NotificationsFragment extends Fragment {

    private NotificationsAdapter adapter;

    /**
     * Required empty public constructor.
     */
    public NotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method to create a new instance of this fragment.
     *
     * @return A new instance of NotificationsFragment.
     */
    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Inflates the fragment layout.
     *
     * @param inflater           The LayoutInflater object.
     * @param container          The parent ViewGroup.
     * @param savedInstanceState Saved instance state bundle.
     * @return The inflated View.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    /**
     * Called immediately after onCreateView().
     *
     * <p>Sets up the RecyclerView, adapter, and observes the {@link NotificationsViewModel}
     * to update notifications in real-time.</p>
     *
     * <p>Handles navigation to {@link EventDetailsFragment} when a notification is clicked.</p>
     *
     * @param view               The root view of the fragment.
     * @param savedInstanceState Saved instance state bundle.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new NotificationsAdapter(new ArrayList<>(), eventId -> {
            FragmentManager fm = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.setReorderingAllowed(true);
            transaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
            );
            transaction.replace(R.id.main_fragment_container, EventDetailsFragment.newInstance(eventId));
            transaction.addToBackStack(null);
            transaction.commit();
        });

        recyclerView.setAdapter(adapter);

        NotificationsViewModel viewModel = new ViewModelProvider(requireActivity()).get(NotificationsViewModel.class);

        viewModel.getNotifications().observe(getViewLifecycleOwner(), notifications -> {
            adapter.updateList(notifications);
        });
    }
}