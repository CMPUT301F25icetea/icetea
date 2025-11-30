package com.example.icetea.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.icetea.R;
import com.example.icetea.models.NotificationItemAdmin;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationDetailsFragment extends Fragment {

    private static final String ARG_EVENT_ID = "event_id";
    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_RECIPIENTS = "recipients";
    private static final String ARG_STATUSES = "statuses";
    private static final String ARG_TIMESTAMP = "timestamp";

    private String eventId;
    private String title;
    private String message;
    private ArrayList<String> recipients;
    private ArrayList<String> statuses;
    private long timestampMillis;

    public NotificationDetailsFragment() {
        // Required empty public constructor
    }

    public static NotificationDetailsFragment newInstance(String eventId,
                                                          String title,
                                                          String message,
                                                          List<String> recipients,
                                                          List<String> statuses,
                                                          Timestamp timestamp) {
        NotificationDetailsFragment fragment = new NotificationDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putStringArrayList(ARG_RECIPIENTS, new ArrayList<>(recipients));
        args.putStringArrayList(ARG_STATUSES, new ArrayList<>(statuses));
        if (timestamp != null) {
            args.putLong(ARG_TIMESTAMP, timestamp.toDate().getTime());
        }
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
            title = getArguments().getString(ARG_TITLE);
            message = getArguments().getString(ARG_MESSAGE);
            recipients = getArguments().getStringArrayList(ARG_RECIPIENTS);
            statuses = getArguments().getStringArrayList(ARG_STATUSES);
            timestampMillis = getArguments().getLong(ARG_TIMESTAMP);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textTitle = view.findViewById(R.id.textNotificationTitleDetail);
        TextView textMessage = view.findViewById(R.id.textNotificationMessageDetail);
        TextView textEventId = view.findViewById(R.id.textNotificationEventId);
        LinearLayout containerRecipients = view.findViewById(R.id.containerRecipients);
        LinearLayout containerStatuses = view.findViewById(R.id.containerStatuses);
        TextView textTime = view.findViewById(R.id.textNotificationTimeDetail);
        ImageButton buttonBack = view.findViewById(R.id.buttonBack);

        textTitle.setText("Title: " + title);
        textMessage.setText("Content: " + message);
        textEventId.setText("Event ID: " + eventId);

        containerRecipients.removeAllViews();
        if (recipients != null && !recipients.isEmpty()) {
            for (String r : recipients) {
                TextView tv = new TextView(requireContext());
                tv.setText(r);
                tv.setTextColor(getResources().getColor(R.color.black));
                tv.setTextSize(18f);
                containerRecipients.addView(tv);
            }
        }

        containerStatuses.removeAllViews();
        if (statuses != null && !statuses.isEmpty()) {
            for (String s : statuses) {
                TextView tv = new TextView(requireContext());
                tv.setText(s);
                tv.setTextColor(getResources().getColor(R.color.black));
                tv.setTextSize(18f);
                containerStatuses.addView(tv);
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        if (timestampMillis > 0) {
            Date date = new Date(timestampMillis);
            textTime.setText(sdf.format(date));
        } else {
            textTime.setText("Unknown time");
        }

        buttonBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
    }
}
