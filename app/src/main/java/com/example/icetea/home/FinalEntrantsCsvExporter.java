package com.example.icetea.home;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

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

public class FinalEntrantsCsvExporter {

    public static void export(Context context, String eventId) {
        if (eventId == null) {
            Toast.makeText(context, "No event selected.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("waitlist")
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("status", "accepted")
                .get()
                .addOnSuccessListener(waitlistSnapshot -> {
                    if (waitlistSnapshot.isEmpty()) {
                        Toast.makeText(context, "No final entrants to export.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<Task<DocumentSnapshot>> userTasks = new ArrayList<>();
                    List<Timestamp> joinedAtList = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : waitlistSnapshot) {
                        String userId = doc.getString("userId");
                        Timestamp joinedAt = doc.getTimestamp("timestamp");

                        if (userId != null) {
                            userTasks.add(db.collection("users").document(userId).get());
                            joinedAtList.add(joinedAt);
                        }
                    }

                    Tasks.whenAllSuccess(userTasks)
                            .addOnSuccessListener(results -> writeCsv(context, eventId, results, joinedAtList))
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                Toast.makeText(context, "Failed to load entrant details.", Toast.LENGTH_SHORT).show();
                            });

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(context, "Failed to load final entrants.", Toast.LENGTH_SHORT).show();
                });
    }

    private static void writeCsv(Context context, String eventId, List<Object> userDocs, List<Timestamp> joinedAtList) {
        try {
            String fileName = "final_entrants_" + eventId + ".csv";

            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/");

            Uri uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

            if (uri == null) {
                Toast.makeText(context, "Could not create download file.", Toast.LENGTH_SHORT).show();
                return;
            }

            OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);

            writer.write("Name,Email,Registration Date\n");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

            for (int i = 0; i < userDocs.size(); i++) {
                DocumentSnapshot userDoc = (DocumentSnapshot) userDocs.get(i);
                Timestamp joinedAt = joinedAtList.get(i);

                String name = userDoc != null ? userDoc.getString("name") : "N/A";
                String email = userDoc != null ? userDoc.getString("email") : "N/A";
                String dateString = joinedAt != null ? dateFormat.format(joinedAt.toDate()) : "N/A";

                writer.write(name + "," + email + "," + dateString + "\n");
            }

            writer.flush();
            writer.close();
            Toast.makeText(context, "CSV saved in Downloads as " + fileName, Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error creating CSV file.", Toast.LENGTH_SHORT).show();
        }
    }
}
