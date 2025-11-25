package com.example.icetea.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.icetea.R;

public class FinalEntrantsFragment extends Fragment {

    private static final String ARG_EVENT_ID = "eventId";

    private String eventId;

    public FinalEntrantsFragment() {
        // Required empty public constructor
    }

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_final_entrants, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Button downloadCsvButton = view.findViewById(R.id.buttonDownloadCsv);

        downloadCsvButton.setOnClickListener(v -> {
            //eventId;
        });

    }
}