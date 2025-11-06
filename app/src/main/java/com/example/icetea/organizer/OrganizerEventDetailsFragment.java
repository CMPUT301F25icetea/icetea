package com.example.icetea.organizer;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.icetea.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrganizerEventDetailsFragment extends Fragment {

    private TextView nameText, descText, locationText, dateRangeText, regRangeText, capacityText;
    private Button finalEntrantsButton;
    private String eventId;

    public OrganizerEventDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_event_details, container, false);

        nameText = view.findViewById(R.id.eventNameText);
        descText = view.findViewById(R.id.eventDescText);
        locationText = view.findViewById(R.id.eventLocationText);
        dateRangeText = view.findViewById(R.id.eventDateText);
        regRangeText = view.findViewById(R.id.eventRegText);
        capacityText = view.findViewById(R.id.eventCapacityText);
        finalEntrantsButton = view.findViewById(R.id.buttonFinalEntrants);

        Bundle args = getArguments();
        if (args != null) {
            eventId = args.getString("eventId");
            nameText.setText(args.getString("name", "Unnamed Event"));
            descText.setText(args.getString("description", "No description provided"));
            locationText.setText(args.getString("location", "No location provided"));
            capacityText.setText("Capacity: " + args.getInt("capacity", 0));

            long start = args.getLong("startDate", 0);
            long end = args.getLong("endDate", 0);
            long regOpen = args.getLong("regOpen", 0);
            long regClose = args.getLong("regClose", 0);

            dateRangeText.setText("Event: " + formatDate(start) + " → " + formatDate(end));
            regRangeText.setText("Registration: " + formatDate(regOpen) + " → " + formatDate(regClose));
        }

        finalEntrantsButton.setOnClickListener(v -> {
            if (eventId == null) {
                Toast.makeText(getContext(), "Event ID not found.", Toast.LENGTH_SHORT).show();
                return;
            }
            com.example.icetea.models.WaitlistDB.getInstance()
                    .getFinalEntrants(eventId, task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            if (task.getResult().isEmpty()) {
                                Toast.makeText(getContext(), "No final entrants yet.", Toast.LENGTH_SHORT).show();
                            } else {
                                OrganizerFinalEntrantsFragment fragment = new OrganizerFinalEntrantsFragment();

                                Bundle bundle = new Bundle();
                                bundle.putString("eventId", eventId);
                                bundle.putString("name", nameText.getText().toString());
                                fragment.setArguments(bundle);

                                getParentFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.organizer_fragment_container, fragment)
                                        .addToBackStack(null)
                                        .commit();
                            }
                        } else {
                            Toast.makeText(getContext(), "Failed to load entrants.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        return view;
    }

    private String formatDate(long millis) {
        if (millis == 0) return "N/A";
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(new Date(millis));
    }
}
