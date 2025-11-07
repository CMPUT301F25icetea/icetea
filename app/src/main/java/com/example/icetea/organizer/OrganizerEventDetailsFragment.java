package com.example.icetea.organizer;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.widget.LinearLayout;

import com.example.icetea.R;
import com.example.icetea.models.WaitlistDB;
import com.example.icetea.util.Callback;
import com.example.icetea.util.QRCode;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrganizerEventDetailsFragment extends Fragment {

    private TextView nameText, descText, locationText, dateRangeText, regRangeText, capacityText;
    private Button finalEntrantsButton, drawAttendeesButton;
    private Button waitingListButton;
    private LinearLayout waitingListContainer;
    private ListenerRegistration waitlistRegistration;
    private ImageView qrImageView;
    private String eventId, eventName;

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
        drawAttendeesButton = view.findViewById(R.id.buttonDrawAttendees);
        waitingListButton = view.findViewById(R.id.buttonWaitingList);
        waitingListContainer = view.findViewById(R.id.waitingListContainer);

        qrImageView = view.findViewById(R.id.qrImageView);

        Bundle args = getArguments();
        if (args != null) {
            eventId = args.getString("eventId");
            eventName = args.getString("name", "Unknown Event");
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


        QRCode.generateQRCode(eventId, qrImageView);

        qrImageView.setOnClickListener(v -> {QRCode.downloadQrCode(getContext(), qrImageView, new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(getContext(), "QR code saved to photos!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        });

        finalEntrantsButton.setOnClickListener(v -> {
            if (eventId == null) {
                Toast.makeText(getContext(), "Event ID not found.", Toast.LENGTH_SHORT).show();
                return;
            }
            WaitlistDB.getInstance()
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

        drawAttendeesButton.setOnClickListener(v -> {
            if (eventId == null) {
                Toast.makeText(getContext(), "Event ID not found.", Toast.LENGTH_SHORT).show();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Draw Attendees");

            final EditText input = new EditText(requireContext());
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setHint("Enter the number of attendees to draw");
            builder.setView(input);

            builder.setPositiveButton("Draw", (dialog, which) -> {
                String text = input.getText().toString().trim();
                if (text.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter a number.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int drawCount;
                try {
                    drawCount = Integer.parseInt(text);
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Invalid number entered.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (drawCount <= 0) {
                    Toast.makeText(getContext(), "Enter a positive number.", Toast.LENGTH_SHORT).show();
                    return;
                }

                OrganizerDrawManager drawManager = new OrganizerDrawManager();
                drawManager.drawEntrants(eventId, eventName, drawCount);
                Toast.makeText(getContext(), "Drawing " + drawCount + " attendees...", Toast.LENGTH_SHORT).show();

                OrganizerEntrantWinnersFragment fragment = new OrganizerEntrantWinnersFragment();

                Bundle bundle = new Bundle();
                bundle.putString("eventId", eventId);
                fragment.setArguments(bundle);

                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.organizer_fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.show();

            waitingListButton.setOnClickListener(btn -> {
                if (waitingListContainer.getVisibility() == View.GONE) {
                    waitingListContainer.setVisibility(View.VISIBLE);
                } else {
                    waitingListContainer.setVisibility(View.GONE);
                }
            });


        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        startWaitlistListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (waitlistRegistration != null) {
            waitlistRegistration.remove();
            waitlistRegistration = null;
        }
    }

    // Listen to waitlist entries for this event
    private void startWaitlistListener() {
        if (eventId == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // stop any old listener first
        if (waitlistRegistration != null) {
            waitlistRegistration.remove();
        }

        waitlistRegistration = db.collection("waitlist")
                .whereEqualTo("eventId", eventId)
                .orderBy("joinedAt")
                .addSnapshotListener((snap, e) -> {
                    if (e != null || snap == null) return;

                    waitingListContainer.removeAllViews();

                    if (snap.isEmpty()) {
                        addTextRow("No entrants on the waiting list yet.");
                        return;
                    }

                    for (QueryDocumentSnapshot doc : snap) {
                        String userId = doc.getString("userId");
                        Long joinedAt = doc.getLong("joinedAt");
                        addWaitlistRow(userId, joinedAt);
                    }
                });
    }


    private void addTextRow(String text) {
        TextView tv = new TextView(requireContext());
        tv.setText(text);
        waitingListContainer.addView(tv);
    }

    private void addWaitlistRow(String userId, Long joinedAt) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(8, 8, 8, 8);

        TextView nameView = new TextView(requireContext());
        nameView.setText("Loading name...");
        nameView.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView timeView = new TextView(requireContext());
        String timeText = (joinedAt != null)
                ? "Joined: " + formatDateTime(joinedAt)
                : "Joined: N/A";
        timeView.setText(timeText);

        TextView locationView = new TextView(requireContext());

        row.addView(nameView);
        row.addView(timeView);
        row.addView(locationView);

        waitingListContainer.addView(row);

        if (userId == null) {
            nameView.setText("Unknown user");
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        nameView.setText("Unknown user");
                        return;
                    }

                    String name = doc.getString("name");
                    String location = doc.getString("location");

                    if (name == null || name.isEmpty()) name = "Unknown user";
                    nameView.setText(name);
                    if (location != null && !location.isEmpty())
                        locationView.setText(location);
                })
                .addOnFailureListener(e -> nameView.setText("Unknown user"));
    }

    private String formatDateTime(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date(millis));
    }


    private String formatDate(long millis) {
        if (millis == 0) return "N/A";
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(new Date(millis));
    }
}
