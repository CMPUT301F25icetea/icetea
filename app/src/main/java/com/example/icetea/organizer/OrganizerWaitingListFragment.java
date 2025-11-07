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
    private final List<WaitingListEntry> waitingList = new ArrayList<>();
    TextView emptyMessage;
    private static final String ARG_EVENT_ID = "eventId";
    private String eventId;

    public static OrganizerWaitingListFragment newInstance(String eventId) {
        OrganizerWaitingListFragment fragment = new OrganizerWaitingListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
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

        recyclerView = view.findViewById(R.id.organizerWaitingList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //waitingList.add(new WaitingListEntry("user1@example.com", "Pending", "2025-11-07 10:00", false));
        //waitingList.add(new WaitingListEntry("user2@example.com", "Pending", "2025-11-07 11:00", false));

        loadWaitingList(view);

        adapter = new WaitingListAdapter(waitingList);
        recyclerView.setAdapter(adapter);

    }


    private void loadWaitingList(View view) {
        controller.getWaitingList(eventId, new Callback<List<WaitingListEntry>>() {
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