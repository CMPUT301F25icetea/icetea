package com.example.icetea.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icetea.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

/**
 * Fragment that displays the final entrants of a specific event.
 * <p>
 * This fragment shows a list of accepted entrants in a {@link RecyclerView}
 * and provides a button to download the entrant list as a CSV file.
 * <p>
 * The fragment observes a {@link FinalEntrantsViewModel} to receive real-time
 * updates of the accepted entrants from the Firestore waitlist.
 */
public class FinalEntrantsFragment extends Fragment {

    /** Argument key for the event ID */
    private static final String ARG_EVENT_ID = "eventId";

    /** ID of the event being displayed */
    private String eventId;

    /** RecyclerView adapter for displaying the final entrants */
    private FinalEntrantsAdapter adapter;

    /** ViewModel providing live data of accepted entrants */
    private FinalEntrantsViewModel viewModel;

    /**
     * Factory method to create a new instance of FinalEntrantsFragment
     * with a specific event ID.
     *
     * @param eventId The ID of the event
     * @return A new instance of FinalEntrantsFragment
     */
    public static FinalEntrantsFragment newInstance(String eventId) {
        FinalEntrantsFragment fragment = new FinalEntrantsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called when the fragment is being created.
     * Retrieves the event ID from the fragment arguments if available.
     *
     * @param savedInstanceState If non-null, this fragment is being re-created from a previous saved state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
        }
    }

    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater The LayoutInflater object to inflate views
     * @param container If non-null, the parent view the fragment's UI should be attached to
     * @param savedInstanceState If non-null, this fragment is being re-created from a previous state
     * @return The root view of the inflated layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_final_entrants, container, false);
    }

    /**
     * Called immediately after onCreateView. Sets up the RecyclerView, ViewModel,
     * and buttons. Observes the ViewModel for updates to the accepted entrants list.
     *
     * @param view The view returned by onCreateView
     * @param savedInstanceState If non-null, this fragment is being re-created from a previous state
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Back button to pop the fragment from the stack
        ImageButton backButton = view.findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        // RecyclerView setup
        RecyclerView recyclerView = view.findViewById(R.id.recyclerFinalEntrants);
        adapter = new FinalEntrantsAdapter(requireContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // ViewModel setup
        viewModel = new FinalEntrantsViewModel();
        viewModel.getEntrants().observe(getViewLifecycleOwner(), entries -> {
            adapter.updateList(entries);
        });

        // Start listening to live updates of entrants
        viewModel.startListening(eventId);

        // CSV export button
        MaterialButton downloadCsvBtn = view.findViewById(R.id.buttonDownloadCsv);
        downloadCsvBtn.setOnClickListener(v -> {
            FinalEntrantsCsvExporter.export(getContext(), eventId);
        });
    }
}