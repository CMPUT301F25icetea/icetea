package com.example.icetea.entrant;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
public class EntrantScannerFragment extends Fragment {

    private Button btnScan;
    private TextView tvScanResult;

    public EntrantScannerFragment() {
        // Required empty public constructor
    }

    public static EntrantScannerFragment newInstance() {
        return new EntrantScannerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entrant_scanner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnScan = view.findViewById(R.id.btnScan);
        tvScanResult = view.findViewById(R.id.tvScanResult);

        btnScan.setOnClickListener(v -> startScanner());
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
                Toast.makeText(getContext(), R.string.scan_cancelled, Toast.LENGTH_SHORT).show();
                updateScanResult("Scan cancelled", false);
            } else {
                // Successfully scanned a QR code
                String scannedData = result.getContents();
                updateScanResult("Verifying event...", true);

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
                updateScanResult("Event found! Opening...", true);
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

        // Create bundle with event ID
        Bundle bundle = new Bundle();
        bundle.putString("event_id", eventId);

        // Create and configure fragment
        UserEventDetailsFragment fragment = new UserEventDetailsFragment();
        fragment.setArguments(bundle);

        // Navigate to event details
        getParentFragmentManager().beginTransaction()
                .replace(R.id.entrant_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Show invalid QR code error
     */
    private void showInvalidQRCode() {
        Toast.makeText(getContext(), R.string.invalid_qr_code, Toast.LENGTH_LONG).show();
        updateScanResult("Invalid QR code - not a valid event", false);
    }

    /**
     * Update the result text view
     */
    private void updateScanResult(String message, boolean isLoading) {
        if (tvScanResult != null) {
            tvScanResult.setText(message);
            tvScanResult.setVisibility(View.VISIBLE);

            // Hide after 3 seconds if not loading
            if (!isLoading) {
                tvScanResult.postDelayed(() -> {
                    if (tvScanResult != null) {
                        tvScanResult.setVisibility(View.GONE);
                    }
                }, 3000);
            }
        }
    }
}