package com.example.icetea.organizer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.icetea.EventListAdapter;
import com.example.icetea.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrganizerHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrganizerHomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrganizerHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrganizerHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrganizerHomeFragment newInstance() {
        OrganizerHomeFragment fragment = new OrganizerHomeFragment();
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_organizer_home, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.organizerEventList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        String[] testEvents = {"Event 1", "Event 2", "Event 3", "Event 4", "Event 5", "Event 6"};
        EventListAdapter adapter = new EventListAdapter(testEvents);
        recyclerView.setAdapter(adapter);

        return view;
    }
}