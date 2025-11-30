package com.example.icetea.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.icetea.R;
import com.example.icetea.home.ManageEventFragment;
import com.example.icetea.models.Event;
import com.example.icetea.models.EventDB;

import java.util.ArrayList;
import java.util.List;

public class AdminEventsFragment extends Fragment {

    private RecyclerView recyclerView;
    private AdminEventsAdapter adapter;
    private List<Event> events;

    public AdminEventsFragment() {
        // Required empty public constructor
    }

    public static AdminEventsFragment newInstance() {
        return new AdminEventsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_events, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerAdminEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        events = new ArrayList<>();
        adapter = new AdminEventsAdapter(requireContext(), events, event -> {
            FragmentManager fm = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.setReorderingAllowed(true);
            transaction.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left,
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
            );
            transaction.replace(R.id.main_fragment_container, ManageEventFragment.newInstance(event.getEventId()));
            transaction.addToBackStack(null);
            transaction.commit();
        });


        recyclerView.setAdapter(adapter);

        loadEvents();
    }

    private void loadEvents() {
        EventDB.getInstance().getAllEvents(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                events.clear();
                for (var doc : task.getResult().getDocuments()) {
                    Event event = doc.toObject(Event.class);
                    if (event != null) events.add(event);
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(requireContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }

}