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

public class ViewQRcodeFragment extends Fragment {

    private static final String ARG_EVENT_ID = "eventId";
    private String eventId;

    public ViewQRcodeFragment() { }

    public static ViewQRcodeFragment newInstance(String eventId) {
        ViewQRcodeFragment fragment = new ViewQRcodeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_qrcode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton backButton = view.findViewById(R.id.buttonBackQR);
        backButton.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        ImageView qrImageView = view.findViewById(R.id.qrImageView);
        if (eventId != null && !eventId.isEmpty()) {
            QRCode.generateQRCode(eventId, qrImageView);
        }

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
