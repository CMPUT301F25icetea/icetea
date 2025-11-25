package com.example.icetea.home;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.icetea.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Shows the final entrants list for an event and lets you export it as CSV.
 */
public class FinalEntrantsFragment extends Fragment {

    private static final String ARG_EVENT_ID = "eventId";

    // event whose entrants we’re exporting
    private String eventId;

    public FinalEntrantsFragment() { }

    /**
     * Factory method so we always pass in the eventId.
     */
    public static FinalEntrantsFragment newInstance(String eventId) {
        FinalEntrantsFragment fragment = new FinalEntrantsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Read eventId from arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
        }
    }

    /**
     * Inflate layout that has the list + “Export CSV” button.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_final_entrants, container, false);
    }

    /**
     * Wire up the Export button.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button downloadCsvButton = view.findViewById(R.id.buttonDownloadCsv);
        downloadCsvButton.setOnClickListener(v -> exportFinalEntrantsToCSV());
    }

    /**
     * 1) Query waitlist for this event with status == "accepted"
     * 2) For each entry, load the user doc to get name + email
     * 3) Write Name,Email,Registration Date into a CSV in Downloads.
     */
    private void exportFinalEntrantsToCSV() {
        if (eventId == null) {
            Toast.makeText(getContext(), "No event selected.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Step 1: get all accepted entries for this event
        db.collection("waitlist")
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("status", "accepted")
                .get()
                .addOnSuccessListener(waitlistSnapshot -> {
                    if (waitlistSnapshot.isEmpty()) {
                        Toast.makeText(getContext(),
                                "No final entrants to export.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // We will collect user fetch tasks and the matching joinedAt timestamps
                    List<Task<DocumentSnapshot>> userTasks = new ArrayList<>();
                    List<Timestamp> joinedAtList = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : waitlistSnapshot) {
                        String userId = doc.getString("userId");
                        Timestamp joinedAt = doc.getTimestamp("joinedAt");

                        if (userId != null) {
                            userTasks.add(db.collection("users").document(userId).get());
                            joinedAtList.add(joinedAt);
                        }
                    }

                    if (userTasks.isEmpty()) {
                        Toast.makeText(getContext(),
                                "No valid entrants found.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Step 2: wait until all user docs are loaded
                    Tasks.whenAllSuccess(userTasks)
                            .addOnSuccessListener(results -> {
                                // Now we have user docs in the same order as joinedAtList
                                writeCsvToDownloads(results, joinedAtList);
                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                Toast.makeText(getContext(),
                                        "Failed to load entrant details.",
                                        Toast.LENGTH_SHORT).show();
                            });

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(getContext(),
                            "Failed to load final entrants.",
                            Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Helper that actually creates the CSV file in the Downloads folder.
     */
    private void writeCsvToDownloads(List<Object> userDocs, List<Timestamp> joinedAtList) {
        try {
            // Decide the file name (includes eventId as requested)
            String fileName = "final_entrants_" + eventId + ".csv";

            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/");

            Uri uri = requireContext()
                    .getContentResolver()
                    .insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

            if (uri == null) {
                Toast.makeText(getContext(),
                        "Could not create download file.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            OutputStream outputStream =
                    requireContext().getContentResolver().openOutputStream(uri);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);

            // CSV header line
            writer.write("Name,Email,Registration Date\n");

            SimpleDateFormat dateFormat =
                    new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

            // For each entrant, pull name/email from user doc + date from joinedAt
            for (int i = 0; i < userDocs.size(); i++) {
                DocumentSnapshot userDoc = (DocumentSnapshot) userDocs.get(i);
                Timestamp joinedAt = joinedAtList.get(i);

                String name = userDoc != null ? userDoc.getString("name") : null;
                String email = userDoc != null ? userDoc.getString("email") : null;

                String dateString = "N/A";
                if (joinedAt != null) {
                    dateString = dateFormat.format(joinedAt.toDate());
                }

                // Write one CSV line
                writer.write(
                        (name != null ? name : "N/A") + "," +
                                (email != null ? email : "N/A") + "," +
                                dateString +
                                "\n"
                );
            }

            writer.flush();
            writer.close();

            Toast.makeText(getContext(),
                    "CSV saved in Downloads as " + fileName,
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(),
                    "Error creating CSV file.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
