package com.example.icetea.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icetea.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class FinalEntrantsFragment extends Fragment {

    private static final String ARG_EVENT_ID = "eventId";
    private String eventId;

    private FinalEntrantsAdapter adapter;
    private FinalEntrantsViewModel viewModel;

    public static FinalEntrantsFragment newInstance(String eventId) {
        FinalEntrantsFragment fragment = new FinalEntrantsFragment();
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
        return inflater.inflate(R.layout.fragment_final_entrants, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton backButton = view.findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );
        RecyclerView recyclerView = view.findViewById(R.id.recyclerFinalEntrants);
        adapter = new FinalEntrantsAdapter(requireContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel = new FinalEntrantsViewModel();
        viewModel.getEntrants().observe(getViewLifecycleOwner(), entries -> {
            adapter.updateList(entries);
        });

        viewModel.startListening(eventId);

        MaterialButton downloadCsvBtn = view.findViewById(R.id.buttonDownloadCsv);
        downloadCsvBtn.setOnClickListener(v -> {
            FinalEntrantsCsvExporter.export(getContext(), eventId);
        });
    }
}
