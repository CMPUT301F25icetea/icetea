package com.example.icetea.home;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailsFragment extends Fragment {

    private EventDetailsController controller;
    private static final String ARG_EVENT_ID = "eventId";

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
        TextView description = view.findViewById(R.id.textEventDescriptionDetail);
        TextView criteria = view.findViewById(R.id.textEventCriteriaDetail);
        TextView location = view.findViewById(R.id.textEventLocationDetail);
        TextView startDate = view.findViewById(R.id.textEventStartDateDetail);
        TextView waitlistCount = view.findViewById(R.id.textWaitlistCountDetail);
        TextView geolocation = view.findViewById(R.id.textGeolocationDetail);

        MaterialButton actionButton = view.findViewById(R.id.buttonEventDetailsAction);

        controller.getEventObject(eventId, new Callback<Event>() {
            @Override
            public void onSuccess(Event event) {

                if (event.getPosterBase64() != null && !event.getPosterBase64().isEmpty()) {
                    Bitmap bitmap = ImageUtil.base64ToBitmap(event.getPosterBase64());
                    poster.setImageBitmap(bitmap);
                } else {
                    poster.setImageResource(R.drawable.default_poster);
                }

                name.setText(event.getName());
                description.setText(event.getDescription());
                criteria.setText(event.getCriteria());
                location.setText(event.getLocation());

                startDate.setText("Start: " + controller.timestampToString(event.getEventStartDate()));

                geolocation.setText(event.getGeolocationRequirement() ? "Required" : "Not Required");

                if (event.getMaxEntrants() == null || event.getMaxEntrants() == 0) {
                    waitlistCount.setText("There are/is currently " + event.getCurrentEntrants() + " people on the waitlist");
                    //todo: fix english
                } else {
                    waitlistCount.setText(event.getCurrentEntrants() + " / " + event.getMaxEntrants());
                }

                controller.getEntrantStatus(CurrentUser.getInstance().getFid(), eventId, new Callback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        //todo: add more styling on each scenario

                        status = result;
                        boolean waitlistFull = event.getMaxEntrants() != null &&
                                Objects.equals(event.getMaxEntrants(), event.getCurrentEntrants());

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

        //todo: create "declined" button beside accept button is status = "selected"
        //todo: verify somewhere that regEnd has not passed
        actionButton.setOnClickListener(v -> {
            actionButton.setEnabled(false);
            String userId = CurrentUser.getInstance().getFid();
            if (status == null) {
                Waitlist waitlist = new Waitlist();
                waitlist.setUserId(userId);
                waitlist.setEventId(eventId);
                waitlist.setTimestamp(Timestamp.now());
                waitlist.setLatitude(null);
                waitlist.setLongitude(null);
                waitlist.setStatus(Waitlist.STATUS_WAITING);

                controller.addToWaitlist(waitlist, new Callback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                actionButton.setText("Leave Waitlist");
                                actionButton.setEnabled(true);
                                actionButton.setAlpha(1.0f);
                                status = Waitlist.STATUS_WAITING;
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                actionButton.setEnabled(true);
                            }
                        }
                );

            } else if (status.equals(Waitlist.STATUS_WAITING)) {

                controller.removeFromWaitlist(userId, eventId, new Callback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        actionButton.setText("Join Waitlist");
                        actionButton.setEnabled(true);
                        actionButton.setAlpha(1.0f);
                        status = null;
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        actionButton.setEnabled(true);
                    }
                });

            } else if (status.equals(Waitlist.STATUS_SELECTED)) {
                controller.updateEntrantStatus(CurrentUser.getInstance().getFid(), eventId, Waitlist.STATUS_ACCEPTED, new Callback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        actionButton.setText("Invitation Accepted");
                        actionButton.setEnabled(false);
                        actionButton.setAlpha(0.5f);
                        status = Waitlist.STATUS_ACCEPTED;
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        actionButton.setEnabled(true);
                    }
                });

            }

        });
    }

}