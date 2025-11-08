package com.example.icetea.organizer;

import android.app.NotificationManager;
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
import com.example.icetea.auth.FBAuthenticator;
import com.example.icetea.event.Event;
import com.example.icetea.event.EventAdapter;
import com.example.icetea.event.EventController;
import com.example.icetea.models.WaitingListAdapter;
import com.example.icetea.models.WaitingListController;
import com.example.icetea.models.WaitingListEntry;
import com.example.icetea.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class OrganizerWaitingListFragment extends Fragment {
    private RecyclerView recyclerView;
    private WaitingListAdapter adapter;
    private WaitingListController controller;
    private OrganizerNotificationManager notificationController;
    private final List<WaitingListEntry> waitingList = new ArrayList<>();
    Button selectAll, selectCancelled, sendNotification;
    TextView emptyMessage;
    EditText notificationMessageInput;
    private static final String ARG_EVENT_ID = "eventId", ARG_EVENT_NAME = "eventName";
    private String eventId, eventName;

    public static OrganizerWaitingListFragment newInstance(String eventId, String eventName) {
        OrganizerWaitingListFragment fragment = new OrganizerWaitingListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        args.putString(ARG_EVENT_NAME, eventName);
        fragment.setArguments(args);
        return fragment;
    }

    public OrganizerWaitingListFragment() {
        // Required empty public constructor
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