package com.example.icetea.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.icetea.R;
import com.example.icetea.models.WaitlistEntry;

import java.util.ArrayList;
import java.util.List;

public class WaitlistFragment extends Fragment {

    private static final String ARG_EVENT_ID = "eventId";

    private String eventId;

    public WaitlistFragment() {
        // Required empty public constructor
    }

    public static WaitlistFragment newInstance(String eventId) {
        WaitlistFragment fragment = new WaitlistFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_waitlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton backButton = view.findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        RecyclerView recyclerView = view.findViewById(R.id.recyclerWaitlist);
        List<WaitlistEntry> entries = new ArrayList<>();
        entries.add(new WaitlistEntry("FirstName LastName", "normalsized@email.com", "Status: Waiting", null));
        WaitlistAdapter adapter = new WaitlistAdapter(requireContext(), entries);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter.notifyDataSetChanged();

    }
}