package com.example.icetea.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.icetea.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

public class EntrantsMapFragment extends Fragment {

    private static final String ARG_EVENT_ID = "arg_event_id";

    private String eventId;
    private MapView mapView;
    private FirebaseFirestore db;

    private ActivityResultLauncher<String[]> locationPermissionLauncher;

    public EntrantsMapFragment() {
        // Required empty constructor
    }

    public static EntrantsMapFragment newInstance(String eventId) {
        EntrantsMapFragment fragment = new EntrantsMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
        }

        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());

        locationPermissionLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.RequestMultiplePermissions(),
                        result -> {
                            Boolean fine = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarse = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

                            if (fine || coarse) {
                                enableMyLocation(); // Permission granted → turn on location
                            } else {
                                Toast.makeText(getContext(),
                                        "Location permission denied",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_entrants_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton backBtn = view.findViewById(R.id.buttonBackQR);
        backBtn.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        mapView = view.findViewById(R.id.osmMap);
        mapView.setMultiTouchControls(true);

        mapView.getController().setZoom(10.0);
        mapView.getController().setCenter(new GeoPoint(53.5462, -113.4937));

        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(getContext(), "No event ID provided", Toast.LENGTH_SHORT).show();
            return;
        }

        loadEntrantsForEvent(eventId);
        enableMyLocation();
    }

    private void loadEntrantsForEvent(String eventId) {
        db.collection("waitlist")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getContext(),
                                "Failed to load entrants",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    QuerySnapshot snap = task.getResult();
                    if (snap == null || snap.isEmpty()) {
                        Toast.makeText(getContext(),
                                "No entrants for this event",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (DocumentSnapshot doc : snap.getDocuments()) {
                        String docId = doc.getId();
                        String name = doc.getString("name");

                        Double lat = doc.getDouble("latitude");
                        Double lng = doc.getDouble("longitude");

                        if (lat == null || lng == null) {
                            Toast.makeText(
                                    getContext(),
                                    "No lat/lng for doc " + docId,
                                    Toast.LENGTH_SHORT
                            ).show();
                            continue;
                        }

                        GeoPoint pos = new GeoPoint(lat, lng);

                        Marker marker = new Marker(mapView);
                        marker.setPosition(pos);
                        marker.setTitle(name != null ? name : ("Entrant " + docId));
                        marker.setOnMarkerClickListener((m, map) -> {
                            Toast.makeText(getContext(),
                                    m.getTitle(),
                                    Toast.LENGTH_SHORT).show();
                            return false;
                        });

                        mapView.getOverlays().add(marker);
                    }

                    mapView.invalidate();
                });
    }


    private void enableMyLocation() {
        boolean fine = ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;

        boolean coarse = ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;

        if (fine || coarse) {
            MyLocationNewOverlay myLocationOverlay =
                    new MyLocationNewOverlay(
                            new GpsMyLocationProvider(requireContext()),
                            mapView);
            myLocationOverlay.enableMyLocation();
            mapView.getOverlays().add(myLocationOverlay);
        } else {
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }
}
