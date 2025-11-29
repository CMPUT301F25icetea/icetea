package com.example.icetea.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.icetea.R;
import com.example.icetea.auth.CurrentUser;
import com.example.icetea.models.Event;
import com.example.icetea.models.Waitlist;
import com.example.icetea.util.Callback;
import com.example.icetea.util.ImageUtil;
import com.example.icetea.util.LocationCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;

import java.util.Objects;

public class EventDetailsFragment extends Fragment {

    private EventDetailsController controller;
    private static final String ARG_EVENT_ID = "eventId";
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<String> locationPermissionLauncher;
    private Event event;
    private LocationCallback pendingLocationCallback;
    private String eventId;
    private String status;


    public EventDetailsFragment() {
        // Required empty public constructor
    }

    public static EventDetailsFragment newInstance(String eventId) {
        EventDetailsFragment fragment = new EventDetailsFragment();
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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        if (pendingLocationCallback != null) {
                            getUserLocation(pendingLocationCallback);
                            pendingLocationCallback = null;
                        }
                    } else {
                        if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                            Toast.makeText(getContext(), "Location permission denied.", Toast.LENGTH_SHORT).show();
                        } else {
                            showLocationSettingsDialog();
                        }
                        pendingLocationCallback = null;
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        controller = new EventDetailsController();

        ImageButton backButton = view.findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        ImageView poster = view.findViewById(R.id.imageEventPosterDetail);
        TextView name = view.findViewById(R.id.textEventNameDetail);
        TextInputEditText description = view.findViewById(R.id.textEventDescriptionDetail);
        TextInputEditText criteria = view.findViewById(R.id.textEventCriteriaDetail);
        TextView startDate = view.findViewById(R.id.textEventStartDateDetail);
        TextView waitlist = view.findViewById(R.id.textWaitlist);
        TextView geolocation = view.findViewById(R.id.textGeolocation);

        MaterialButton actionButton = view.findViewById(R.id.buttonEventDetailsAction);
        MaterialButton declineButton = view.findViewById(R.id.buttonDecline);

        controller.getEventObject(eventId, new Callback<Event>() {
            @Override
            public void onSuccess(Event result) {

                event = result;
                if (event.getPosterBase64() != null && !event.getPosterBase64().isEmpty()) {
                    Bitmap bitmap = ImageUtil.base64ToBitmap(event.getPosterBase64());
                    poster.setImageBitmap(bitmap);
                } else {
                    poster.setImageResource(R.drawable.default_poster);
                }

                name.setText(event.getName());
                description.setText(event.getDescription());
                if (event.getCriteria() == null || event.getCriteria().isEmpty()) {
                    criteria.setText("No criteria specified; Anyone can join!");
                } else {
                    criteria.setText(event.getCriteria());
                }

                StringBuilder sb = new StringBuilder();

                sb.append("\uD83D\uDCC5 Start: ").append(controller.timestampToString(event.getEventStartDate()));
                if (event.getEventEndDate() != null) {
                    sb.append("\n\uD83D\uDCC5 End: ").append(controller.timestampToString(event.getEventEndDate()));
                }
                if (event.getLocation() != null && !event.getLocation().isEmpty()) {
                    sb.append("\n\uD83D\uDCCD Location: ").append(event.getLocation());
                }

                startDate.setText(sb.toString());


                geolocation.setText(event.getGeolocationRequirement() ? "Geolocation Required" : "");

                int current = event.getCurrentEntrants() != null ? event.getCurrentEntrants() : 0;
                int max = event.getMaxEntrants() != null ? event.getMaxEntrants() : 0;

                if (max == 0) {
                    waitlist.setText("👤 " + current);
                } else {
                    waitlist.setText("👤 " + current + " / " + max);
                }

                controller.getEntrantStatus(CurrentUser.getInstance().getFid(), eventId, new Callback<String>() {
                    @Override
                    public void onSuccess(String result) {

                        status = result;
                        boolean waitlistFull = event.getMaxEntrants() != null && event.getCurrentEntrants() != null
                                && event.getCurrentEntrants() >= event.getMaxEntrants();

                        if (status == null) {
                            if (waitlistFull) {
                                actionButton.setText("Waitlist Full");
                                actionButton.setEnabled(false);
                                actionButton.setAlpha(0.5f);
                            } else {
                                actionButton.setText("Join Waitlist");
                                actionButton.setEnabled(true);
                                actionButton.setAlpha(1.0f);
                            }
                            return;
                        }

                        switch (result) {
                            case Waitlist.STATUS_WAITING:
                                actionButton.setText("Leave Waitlist");
                                actionButton.setEnabled(true);
                                actionButton.setAlpha(1.0f);
                                break;

                            case Waitlist.STATUS_SELECTED:
                                actionButton.setText("Accept Invite");
                                actionButton.setEnabled(true);
                                actionButton.setAlpha(1.0f);
                                declineButton.setEnabled(true);
                                declineButton.setVisibility(View.VISIBLE);
                                break;

                            case Waitlist.STATUS_CANCELLED:
                                actionButton.setText("Invitation Revoked");
                                actionButton.setEnabled(false);
                                actionButton.setAlpha(0.5f);
                                break;

                            case Waitlist.STATUS_ACCEPTED:
                                actionButton.setText("Invitation Accepted");
                                actionButton.setEnabled(false);
                                actionButton.setAlpha(0.5f);
                                break;

                            case Waitlist.STATUS_DECLINED:
                                actionButton.setText("Invitation Declined");
                                actionButton.setEnabled(false);
                                actionButton.setAlpha(0.5f);
                                break;

                        }

                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        actionButton.setOnClickListener(v -> {
            actionButton.setEnabled(false);
            declineButton.setEnabled(false);
            String userId = CurrentUser.getInstance().getFid();

            if (status == null && !event.getGeolocationRequirement()) {
                Waitlist waitlistObj = new Waitlist();
                waitlistObj.setUserId(userId);
                waitlistObj.setEventId(eventId);
                waitlistObj.setTimestamp(Timestamp.now());
                waitlistObj.setLatitude(null);
                waitlistObj.setLongitude(null);
                waitlistObj.setStatus(Waitlist.STATUS_WAITING);
                waitlistObj.setReplaced(false);

                controller.addToWaitlist(waitlistObj, new Callback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                actionButton.setText("Leave Waitlist");

                                int current = (event.getCurrentEntrants() != null ? event.getCurrentEntrants() : 0) + 1;
                                event.setCurrentEntrants(current);
                                int max = event.getMaxEntrants() != null ? event.getMaxEntrants() : 0;
                                waitlist.setText("👤 " + current + (max > 0 ? " / " + max : ""));

                                actionButton.setEnabled(true);
                                actionButton.setAlpha(1.0f);
                                status = Waitlist.STATUS_WAITING;
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                actionButton.setEnabled(true);
                                declineButton.setEnabled(true);
                            }
                        }
                );

            } else if (status == null && event.getGeolocationRequirement()) {
                actionButton.setText("Locating... (this may take a few seconds)");

                checkLocationPermission(new LocationCallback() {
                    @Override
                    public void onLocationResult(double latitude, double longitude) {
                        Waitlist waitlistObj = new Waitlist();
                        waitlistObj.setUserId(userId);
                        waitlistObj.setEventId(eventId);
                        waitlistObj.setTimestamp(Timestamp.now());
                        waitlistObj.setLatitude(latitude);
                        waitlistObj.setLongitude(longitude);
                        waitlistObj.setStatus(Waitlist.STATUS_WAITING);
                        waitlistObj.setReplaced(false);

                        controller.addToWaitlist(waitlistObj, new Callback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                actionButton.setText("Leave Waitlist");
                                int current = (event.getCurrentEntrants() != null ? event.getCurrentEntrants() : 0) + 1;
                                event.setCurrentEntrants(current);
                                int max = event.getMaxEntrants() != null ? event.getMaxEntrants() : 0;
                                waitlist.setText("👤 " + current + (max > 0 ? " / " + max : ""));

                                actionButton.setEnabled(true);
                                actionButton.setAlpha(1.0f);
                                status = Waitlist.STATUS_WAITING;
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                actionButton.setEnabled(true);
                                declineButton.setEnabled(true);
                            }
                        });
                    }

                    @Override
                    public void onLocationFailed(Exception e) {
                        Toast.makeText(getContext(), "Could not get location", Toast.LENGTH_SHORT).show();
                        actionButton.setEnabled(true);
                        declineButton.setEnabled(true);
                    }
                });

            } else if (Waitlist.STATUS_WAITING.equals(status)) {

                controller.removeFromWaitlist(userId, eventId, new Callback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        actionButton.setText("Join Waitlist");
                        int current = Math.max((event.getCurrentEntrants() != null ? event.getCurrentEntrants() : 0) - 1, 0);
                        event.setCurrentEntrants(current);
                        int max = event.getMaxEntrants() != null ? event.getMaxEntrants() : 0;
                        waitlist.setText("👤 " + current + (max > 0 ? " / " + max : ""));

                        actionButton.setEnabled(true);
                        actionButton.setAlpha(1.0f);
                        status = null;
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        actionButton.setEnabled(true);
                        declineButton.setEnabled(true);
                    }
                });

            } else if (Waitlist.STATUS_SELECTED.equals(status)) {
                controller.updateEntrantStatus(CurrentUser.getInstance().getFid(), eventId, Waitlist.STATUS_ACCEPTED, new Callback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        actionButton.setText("Invitation Accepted");
                        actionButton.setEnabled(false);
                        declineButton.setVisibility(View.GONE);
                        actionButton.setAlpha(0.5f);
                        status = Waitlist.STATUS_ACCEPTED;
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        actionButton.setEnabled(true);
                        declineButton.setEnabled(true);
                    }
                });

            }

        });
        declineButton.setOnClickListener(v -> {
            actionButton.setEnabled(false);
            declineButton.setEnabled(false);

            controller.updateEntrantStatus(CurrentUser.getInstance().getFid(), eventId, Waitlist.STATUS_DECLINED, new Callback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    actionButton.setText("Invitation Declined");
                    declineButton.setVisibility(View.GONE);
                    actionButton.setEnabled(false);
                    actionButton.setAlpha(0.5f);
                    status = Waitlist.STATUS_DECLINED;
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    actionButton.setEnabled(true);
                    declineButton.setEnabled(true);
                }
            });

        });

    }
    private void checkLocationPermission(LocationCallback callback) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getUserLocation(callback);
        } else {
            pendingLocationCallback = callback;
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }
    private void getUserLocation(LocationCallback callback) {
        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, new CancellationTokenSource().getToken())
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            callback.onLocationResult(location.getLatitude(), location.getLongitude());
                        } else {
                            callback.onLocationFailed(new Exception("Error finding your location"));
                        }
                    });
        } catch (SecurityException e) {
            callback.onLocationFailed(new Exception("Error finding your location"));
        }
    }

    private void showLocationSettingsDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Location Permission Needed")
                .setMessage("Please enable location permission in settings to join this event.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", requireContext().getPackageName(), null));
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}