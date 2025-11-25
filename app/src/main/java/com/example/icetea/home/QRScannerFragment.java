package com.example.icetea.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.icetea.R;
import com.example.icetea.models.EventDB;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * Fragment for scanning QR codes to join event waitlists
 */
public class QRScannerFragment extends Fragment {

    private TextView tvScanStatus;

    public QRScannerFragment() {
        // Required empty public constructor
    }

    public static QRScannerFragment newInstance() {
        return new QRScannerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr_scanner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton backButton = view.findViewById(R.id.buttonBackQRScanner);
        tvScanStatus = view.findViewById(R.id.tvScanStatus);
        com.google.android.material.button.MaterialButton btnStartScan = view.findViewById(R.id.btnStartScan);

        backButton.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        btnStartScan.setOnClickListener(v -> startScanner());

        // Auto-start scanner when fragment opens
        startScanner();
    }

    /**
     * Initialize and start the QR code scanner
     */
    private void startScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);

        // Configure scanner
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan an Event QR Code");
        integrator.setCameraId(0);  // Use back camera
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.setOrientationLocked(false);

        // Start scanning
        integrator.initiateScan();
    }

    /**
     * Handle the result from the QR code scanner
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                // User cancelled the scan
                Toast.makeText(getContext(), "Scan cancelled", Toast.LENGTH_SHORT).show();
                updateScanStatus("Scan cancelled");
            } else {
                // Successfully scanned a QR code
                String scannedData = result.getContents();
                updateScanStatus("Verifying event...");

                // Verify it's a valid event ID and navigate
                verifyAndOpenEvent(scannedData);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Verify the scanned data is a valid event ID before navigating
     */
    private void verifyAndOpenEvent(String eventId) {
        if (eventId == null || eventId.trim().isEmpty()) {
            showInvalidQRCode();
            return;
        }

        if (!isAdded() || getActivity() == null) {
            return;
        }

        // Verify event exists in database
        EventDB.getInstance().getEvent(eventId.trim(), task -> {
            if (!isAdded()) return;

            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                // Valid event - navigate to details
                updateScanStatus("Event found! Opening...");
                openEventDetails(eventId.trim());
            } else {
                // Invalid event ID
                showInvalidQRCode();
            }
        });
    }

    /**
     * Navigate to the event details page
     */
    private void openEventDetails(String eventId) {
        if (!isAdded() || getActivity() == null) {
            return;
        }

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                )
                .replace(R.id.main_fragment_container, EventDetailsFragment.newInstance(eventId))
                .addToBackStack(null)
                .commit();
    }

    /**
     * Show invalid QR code error
     */
    private void showInvalidQRCode() {
        Toast.makeText(getContext(), "Invalid QR code - not a valid event", Toast.LENGTH_LONG).show();
        updateScanStatus("Invalid QR code");
    }

    /**
     * Update the status text view
     */
    private void updateScanStatus(String message) {
        if (tvScanStatus != null) {
            tvScanStatus.setText(message);
            tvScanStatus.setVisibility(View.VISIBLE);
        }
    }
}