package com.example.icetea.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.icetea.R;
import com.example.icetea.models.Waitlist;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class WaitlistFragment extends Fragment {

    private static final String ARG_EVENT_ID = "eventId";
    private WaitlistViewModel viewModel;
    private WaitlistAdapter adapter;
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

        Set<String> selectedStatuses = new HashSet<>();
        selectedStatuses.add(Waitlist.STATUS_WAITING);

        Chip chipWaiting = view.findViewById(R.id.chipWaiting);
        Chip chipSelected = view.findViewById(R.id.chipSelected);
        Chip chipAccepted = view.findViewById(R.id.chipAccepted);
        Chip chipDeclined = view.findViewById(R.id.chipDeclined);
        Chip chipCancelled = view.findViewById(R.id.chipCancelled);

        Chip[] chips = {chipWaiting, chipSelected, chipAccepted, chipDeclined, chipCancelled};

        for (Chip chip : chips) {
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String status = chip.getText().toString().toLowerCase(Locale.ROOT);
                if (isChecked) {
                    selectedStatuses.add(status);
                } else {
                    selectedStatuses.remove(status);
                }
                filterWaitlist(selectedStatuses);
            });
        }

        RecyclerView recyclerView = view.findViewById(R.id.recyclerWaitlist);

        viewModel = new ViewModelProvider(this).get(WaitlistViewModel.class);

        adapter = new WaitlistAdapter(requireContext(), new ArrayList<>(), new WaitlistAdapter.ActionListener() {
            @Override
            public void onReplace(Waitlist entry) {
                viewModel.replaceEntry(entry);
            }

            @Override
            public void onRevoke(Waitlist entry) {
                viewModel.revokeEntry(entry);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel.getWaitlist().observe(getViewLifecycleOwner(), entries -> {
            filterWaitlist(selectedStatuses);
        });

        viewModel.startListening(eventId);

        viewModel.getToastMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void filterWaitlist(Set<String> selectedStatuses) {
        if (viewModel.getWaitlist().getValue() == null) return;

        List<Waitlist> allEntries = viewModel.getWaitlist().getValue();
        List<Waitlist> filtered = new ArrayList<>();

        for (Waitlist entry : allEntries) {
            if (selectedStatuses.contains(entry.getStatus())) {
                filtered.add(entry);
            }
        }

        adapter.updateList(filtered);
    }


}