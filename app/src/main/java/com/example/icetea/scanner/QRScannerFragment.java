package com.example.icetea.scanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.icetea.R;
import com.example.icetea.home.EventDetailsFragment;
import com.example.icetea.models.EventDB;
import com.google.android.material.button.MaterialButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * Fragment for scanning QR codes to join event waitlists.
 *
 * <p>Uses ZXing library to scan QR codes and validates them against the database
 * to ensure the event exists before navigating to the event details page.</p>
 */
public class QRScannerFragment extends Fragment {

    private TextView tvScanStatus;

    /**
     * Required empty public constructor.
     */
    public QRScannerFragment() {
    }

    /**
     * Factory method to create a new instance of QRScannerFragment.
     *
     * @return A new instance of QRScannerFragment.
     */
    public static QRScannerFragment newInstance() {
        return new QRScannerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr_scanner, container, false);
    }

    /**
     * Called immediately after onCreateView(). Initializes UI elements and sets up
     * the scan button click listener.
     *
     * @param view The view returned by onCreateView.
     * @param savedInstanceState Saved state bundle.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvScanStatus = view.findViewById(R.id.tvScanStatus);
        MaterialButton btnStartScan = view.findViewById(R.id.btnStartScan);

        btnStartScan.setOnClickListener(v -> startScanner());
    }

    /**
     * Initializes and starts the QR code scanner using ZXing IntentIntegrator.
     */
    private void startScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);

        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan an Event QR Code");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.setOrientationLocked(false);

        integrator.initiateScan();
    }

    /**
     * Handles the result from the QR code scanner.
     *
     * @param requestCode Request code.
     * @param resultCode Result code.
     * @param data Intent data.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getContext(), "Scan cancelled", Toast.LENGTH_SHORT).show();
                updateScanStatus("Scan cancelled");
            } else {
                String scannedData = result.getContents();
                updateScanStatus("Verifying event...");
                verifyAndOpenEvent(scannedData);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Verifies that the scanned data corresponds to a valid event in the database.
     *
     * @param eventId The scanned event ID.
     */
    private void verifyAndOpenEvent(String eventId) {
        if (eventId == null || eventId.trim().isEmpty()) {
            showInvalidQRCode();
            return;
        }

        if (!isAdded() || getActivity() == null) {
            return;
        }

        EventDB.getInstance().getEvent(eventId.trim(), task -> {
            if (!isAdded()) return;

            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                updateScanStatus("Event found! Opening...");
                openEventDetails(eventId.trim());
            } else {
                showInvalidQRCode();
            }
        });
    }

    /**
     * Navigates to the event details fragment for a valid event.
     *
     * @param eventId The event ID to open.
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
     * Shows a Toast indicating that the scanned QR code is invalid.
     */
    private void showInvalidQRCode() {
        Toast.makeText(getContext(), "Invalid QR code - not a valid event", Toast.LENGTH_LONG).show();
        updateScanStatus("Invalid QR code");
    }

    /**
     * Updates the scan status TextView with the given message.
     *
     * @param message Status message to display.
     */
    private void updateScanStatus(String message) {
        if (tvScanStatus != null) {
            tvScanStatus.setText(message);
            tvScanStatus.setVisibility(View.VISIBLE);
        }
    }
}