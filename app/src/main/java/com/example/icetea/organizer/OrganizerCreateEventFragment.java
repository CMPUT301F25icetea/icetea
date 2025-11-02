package com.example.icetea.organizer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.icetea.R;
import com.example.icetea.event.Event;
import com.example.icetea.event.EventController;
import com.example.icetea.util.Callback;

public class OrganizerCreateEventFragment extends Fragment {

    private EventController controller;

    public OrganizerCreateEventFragment() {
        // Required empty public constructor
    }

    public static OrganizerCreateEventFragment newInstance() {
        return new OrganizerCreateEventFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_organizer_create_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = new EventController();
        Button createTestEvent = view.findViewById(R.id.buttonCreateEvent);

        createTestEvent.setOnClickListener(v -> {
            Event event = new Event("4ZrhNvOe3KtWTy1A4Sda", "newname", "to test", "testID");
            controller.createEvent(event, new Callback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Toast.makeText(getContext(), "Event created!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
//            controller.getEventById("4ZrhNvOe3KtWTy1A4Sda", new Callback<Event>() {
//                @Override
//                public void onSuccess(Event result) {
//                    Toast.makeText(getContext(), result.toString(), Toast.LENGTH_SHORT).show();
//
//                }
//
//                @Override
//                public void onFailure(Exception e) {
//                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//
//                }
//            });
        });

    }

}