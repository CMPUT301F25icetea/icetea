package com.example.icetea.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.icetea.R;
import com.example.icetea.util.Callback;
import com.example.icetea.util.QRCode;
import com.google.android.material.button.MaterialButton;

/**
 * Fragment for displaying a QR code for a specific event.
 * Provides functionality to view and download the QR code.
 */
public class ViewQRcodeFragment extends Fragment {

    /** Argument key for the event ID */
    private static final String ARG_EVENT_ID = "eventId";

    /** ID of the event to display the QR code for */
    private String eventId;

    /**
     * Default constructor. Required empty public constructor.
     */
    public ViewQRcodeFragment() { }

    /**
     * Factory method to create a new instance of this fragment with a specific event ID.
     *
     * @param eventId The ID of the event
     * @return A new instance of ViewQRcodeFragment
     */
    public static ViewQRcodeFragment newInstance(String eventId) {
        ViewQRcodeFragment fragment = new ViewQRcodeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called when the fragment is created.
     * Retrieves the event ID from the arguments if provided.
     *
     * @param savedInstanceState Saved instance state
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
        }
    }

    /**
     * Inflates the fragment layout.
     *
     * @param inflater LayoutInflater to inflate the view
     * @param container Parent view group
     * @param savedInstanceState Saved instance state
     * @return The inflated view
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_qrcode, container, false);
    }

    /**
     * Called after the view is created.
     * Sets up UI components, back button, QR code generation, and download functionality.
     *
     * @param view The root view of the fragment
     * @param savedInstanceState Saved instance state
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Back button to pop the fragment from the back stack
        ImageButton backButton = view.findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        // Generate and display the QR code for the event
        ImageView qrImageView = view.findViewById(R.id.qrImageView);
        if (eventId != null && !eventId.isEmpty()) {
            QRCode.generateQRCode(eventId, qrImageView);
        }

        // Download QR code button
        MaterialButton downloadButton = view.findViewById(R.id.buttonDownloadQr);
        downloadButton.setOnClickListener(v ->
                QRCode.downloadQrCode(requireContext(), qrImageView, new Callback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Toast.makeText(getContext(), "QR Code downloaded successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getContext(), "Failed to download QR Code", Toast.LENGTH_SHORT).show();
                    }
                })
        );
    }
}