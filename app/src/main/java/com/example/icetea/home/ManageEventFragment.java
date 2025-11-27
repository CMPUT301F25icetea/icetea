package com.example.icetea.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.icetea.R;
import com.example.icetea.auth.CurrentUser;
import com.example.icetea.models.Event;
import com.example.icetea.util.Callback;

public class ManageEventFragment extends Fragment {

    private static final String ARG_EVENT_ID = "event_id";

    private String eventId;
    private EventDetailsController controller;

    private TextView eventNameTextView;

    public ManageEventFragment() {
        // Required empty public constructor
    }

    public static ManageEventFragment newInstance(String eventId) {
        ManageEventFragment fragment = new ManageEventFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
        }

        controller = new EventDetailsController();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventNameTextView = view.findViewById(R.id.textManageEventName);

        loadEvent();
    }

    private void loadEvent() {
        if (eventId == null) {
            Toast.makeText(getContext(), "Invalid event id", Toast.LENGTH_SHORT).show();
            return;
        }

        controller.getEventObject(eventId, new Callback<Event>() {
            @Override
            public void onSuccess(Event event) {
                if (!isAdded()) return;
                eventNameTextView.setText(event.getName());
            }

            @Override
            public void onFailure(Exception e) {
                if (!isAdded()) return;
                Toast.makeText(getContext(),
                        "Failed to load event: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
