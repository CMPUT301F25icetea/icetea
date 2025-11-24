package com.example.icetea.organizer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.icetea.R;
import com.example.icetea.models.WaitingListAdapter;
import com.example.icetea.models.WaitingListController;
import com.example.icetea.models.WaitingListEntry;
import com.example.icetea.util.Callback;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for organizers to manage the waiting list for a specific event.
 * Allows selection of users, sending notifications, and filtering by status.
 */
public class OrganizerWaitingListFragment extends Fragment {

    /** RecyclerView displaying the waiting list. */
    private RecyclerView recyclerView;

    /** Adapter for the waiting list RecyclerView. */
    private WaitingListAdapter adapter;

    /** Controller for managing waiting list operations. */
    private WaitingListController controller;

    /** Controller for sending notifications to users. */
    private OrganizerNotificationManager notificationController;

    /** List of waiting list entries for the event. */
    private final List<WaitingListEntry> waitingList = new ArrayList<>();

    /** Button to select all users in the waiting list. */
    Button selectAll;

    /** Button to select only cancelled users. */
    Button selectCancelled;

    /** Button to send notification to selected users. */
    Button sendNotification;

    /** TextView shown when the waiting list is empty. */
    TextView emptyMessage;

    /** Input field for notification message. */
    EditText notificationMessageInput;

    /** Argument key for event ID. */
    private static final String ARG_EVENT_ID = "eventId";

    /** Argument key for event name. */
    private static final String ARG_EVENT_NAME = "eventName";

    /** ID of the event associated with this waiting list. */
    private String eventId;

    /** Name of the event associated with this waiting list. */
    private String eventName;

    /**
     * Creates a new instance of OrganizerWaitingListFragment with event information.
     *
     * @param eventId   The ID of the event.
     * @param eventName The name of the event.
     * @return A new instance of OrganizerWaitingListFragment.
     */
    public static OrganizerWaitingListFragment newInstance(String eventId, String eventName) {
        OrganizerWaitingListFragment fragment = new OrganizerWaitingListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        args.putString(ARG_EVENT_NAME, eventName);
        fragment.setArguments(args);
        return fragment;
    }

    /** Required empty public constructor. */
    public OrganizerWaitingListFragment() {
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
        return inflater.inflate(R.layout.fragment_organizer_waiting_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = new WaitingListController();
        notificationController = new OrganizerNotificationManager();

        selectAll = view.findViewById(R.id.buttonSelectAll);
        selectCancelled = view.findViewById(R.id.buttonSelectCancelled);
        sendNotification = view.findViewById(R.id.buttonSendNotification);

        notificationMessageInput = view.findViewById(R.id.inputNotificationMessage);

        recyclerView = view.findViewById(R.id.organizerWaitingList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadWaitingList(view);
        adapter = new WaitingListAdapter(waitingList);
        recyclerView.setAdapter(adapter);

        selectAll.setOnClickListener(v -> {
            for (WaitingListEntry entry : waitingList) {
                entry.setSelected(true);
                recyclerView.setAdapter(adapter);
            }
        });

        selectCancelled.setOnClickListener(v -> {
            for (WaitingListEntry entry : waitingList) {
                entry.setSelected(entry.getStatus().equals("cancelled"));
                recyclerView.setAdapter(adapter);
            }
        });

        sendNotification.setOnClickListener(v -> {
            String message = notificationMessageInput.getText().toString().trim();
            for (WaitingListEntry entry : adapter.getSelectedItems()) {
                notificationController.sendNotification(entry.getUserId(), eventId, eventName, "generic", message);
            }
            notificationMessageInput.setText("");
            Toast.makeText(getContext(), "Sent notification!", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Loads the waiting list for the event and updates the UI.
     * Shows an empty message if there are no entries.
     *
     * @param view The root view of the fragment.
     */
    private void loadWaitingList(View view) {
        controller.getWaitingList(eventId, new Callback<>() {
            @Override
            public void onSuccess(List<WaitingListEntry> result) {
                if (result.isEmpty()) {
                    emptyMessage = view.findViewById(R.id.emptyMessageWaitingList);
                    emptyMessage.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    waitingList.clear();
                    waitingList.addAll(result);
                }
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
