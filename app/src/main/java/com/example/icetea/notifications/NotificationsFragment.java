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
import android.widget.Toast;

import com.example.icetea.R;
import com.example.icetea.auth.CurrentUser;
import com.example.icetea.home.EventDetailsFragment;
import com.example.icetea.models.Notification;
import com.example.icetea.models.NotificationDB;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class NotificationsFragment extends Fragment {

    private NotificationsAdapter adapter;
    public NotificationsFragment() {
        // Required empty public constructor
    }

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

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