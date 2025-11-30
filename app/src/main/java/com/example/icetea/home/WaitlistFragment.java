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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Fragment that displays the waitlist for a specific event.
 * Allows organizers to view entrants, filter by status,
 * replace or revoke winners, and send notifications to selected entrants.
 *
 * <p>This fragment is lifecycle-aware and uses {@link WaitlistViewModel} to observe
 * real-time updates from Firestore.</p>
 */
public class WaitlistFragment extends Fragment {

    /** Argument key used to pass the event ID to this fragment */
    private static final String ARG_EVENT_ID = "eventId";

    /** ViewModel responsible for managing waitlist data */
    private WaitlistViewModel viewModel;

    /** Adapter for displaying waitlist entries in a RecyclerView */
    private WaitlistAdapter adapter;

    /** ID of the event whose waitlist is being displayed */
    private String eventId;

    /** Filter chips for selecting waitlist statuses */
    Chip chipWaiting;
    Chip chipSelected;
    Chip chipAccepted;
    Chip chipDeclined;
    Chip chipCancelled;

    /** Required empty public constructor */
    public WaitlistFragment() {}

    /**
     * Factory method to create a new instance of this fragment
     * using the provided event ID.
     *
     * @param eventId the ID of the event
     * @return A new instance of fragment WaitlistFragment
     */
    public static WaitlistFragment newInstance(String eventId) {
        WaitlistFragment fragment = new WaitlistFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called to do initial creation of the fragment. Retrieves
     * the event ID from fragment arguments if available.
     *
     * @param savedInstanceState The saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
        }
    }

    /**
     * Inflates the fragment layout.
     *
     * @param inflater LayoutInflater object
     * @param container ViewGroup container
     * @param savedInstanceState Saved instance state
     * @return The root View of the inflated layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_waitlist, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView}. Sets up UI components,
     * RecyclerView, observers, and event handlers.
     *
     * @param view The fragment's root view
     * @param savedInstanceState Saved instance state
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup back button
        ImageButton backButton = view.findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        // Setup show map button
        View showMapBtn = view.findViewById(R.id.buttonShowGeo);
        showMapBtn.setOnClickListener(v -> {
            if (eventId == null || eventId.isEmpty()) {
                Toast.makeText(requireContext(), "No event selected", Toast.LENGTH_SHORT).show();
                return;
            }
            EntrantsMapFragment mapFrag = EntrantsMapFragment.newInstance(eventId);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment_container, mapFrag)
                    .addToBackStack(null)
                    .commit();
        });

        // Initialize status filters
        Set<String> selectedStatuses = new HashSet<>();
        selectedStatuses.add(Waitlist.STATUS_WAITING);

        chipWaiting = view.findViewById(R.id.chipWaiting);
        chipSelected = view.findViewById(R.id.chipSelected);
        chipAccepted = view.findViewById(R.id.chipAccepted);
        chipDeclined = view.findViewById(R.id.chipDeclined);
        chipCancelled = view.findViewById(R.id.chipCancelled);

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

        // Setup RecyclerView
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

        // Observe changes to waitlist entries
        viewModel.getWaitlist().observe(getViewLifecycleOwner(), entries -> {
            filterWaitlist(selectedStatuses);
        });

        // Start listening for real-time updates
        viewModel.startListening(eventId);

        // Observe toast messages
        viewModel.getToastMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });

        // Setup send notifications button
        MaterialButton sendBtn = view.findViewById(R.id.buttonSendNotifications);
        sendBtn.setOnClickListener(v -> {
            SendNotificationDialog dialog = new SendNotificationDialog();
            dialog.setListener((title, message) -> sendNotificationsToSelectedStatuses(title, message));
            dialog.show(getParentFragmentManager(), "SendNotificationDialog");
        });
    }

    /**
     * Filters the waitlist based on the currently selected statuses.
     *
     * @param selectedStatuses Set of statuses to filter by
     */
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

    /**
     * Sends notifications to all waitlist entrants whose statuses are currently selected.
     * Updates Firestore and logs notifications for the event.
     *
     * @param title   The notification title
     * @param message The notification message
     */
    private void sendNotificationsToSelectedStatuses(String title, String message) {
        if (viewModel.getWaitlist().getValue() == null) return;

        List<Waitlist> entries = viewModel.getWaitlist().getValue();

        Set<String> selectedStatuses = new HashSet<>();
        for (Chip chip : new Chip[]{chipWaiting, chipSelected, chipAccepted, chipDeclined, chipCancelled}) {
            if (chip.isChecked()) {
                selectedStatuses.add(chip.getText().toString().toLowerCase(Locale.ROOT));
            }
        }

        if (selectedStatuses.isEmpty()) {
            Toast.makeText(requireContext(), "No statuses selected.", Toast.LENGTH_SHORT).show();
            return;
        }

        ManageEventController controller = new ManageEventController();

        List<String> recipients = new ArrayList<>();

        for (Waitlist entry : entries) {
            if (selectedStatuses.contains(entry.getStatus())) {
                String uid = entry.getUserId();
                if (uid != null && !recipients.contains(uid)) {
                    recipients.add(uid);
                }
                controller.sendNotificationIfEnabled(uid, title, message, eventId);
            }
        }

        if (!recipients.isEmpty()) {
            List<String> statusesList = new ArrayList<>(selectedStatuses);
            controller.addNotificationLogForEvent(eventId, title, message, recipients, statusesList);
        } else {
            Toast.makeText(requireContext(), "No entrants found for selected statuses.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(requireContext(), "Notifications sent!", Toast.LENGTH_SHORT).show();
    }

}