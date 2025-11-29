package com.example.icetea.home;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.icetea.R;

public class SendNotificationDialog extends androidx.fragment.app.DialogFragment {

    public interface Listener {
        void onSendNotification(String title, String message);
    }

    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        EditText titleInput = new EditText(requireContext());
        titleInput.setHint("Title");
        layout.addView(titleInput);

        EditText messageInput = new EditText(requireContext());
        messageInput.setHint("Message");
        layout.addView(messageInput);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Send Notification")
                .setView(layout)
                .setPositiveButton("Send", null)
                .setNegativeButton("Cancel", (d, w) -> d.dismiss())
                .create();

        dialog.setOnShowListener(d -> {
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded);
            }

            Button sendButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            sendButton.setOnClickListener(v -> {
                String title = titleInput.getText().toString().trim();
                String message = messageInput.getText().toString().trim();

                if (title.isEmpty()) {
                    titleInput.setError("Title cannot be empty");
                    return;
                }
                if (message.isEmpty()) {
                    messageInput.setError("Message cannot be empty");
                    return;
                }

                if (listener != null) {
                    listener.onSendNotification(title, message);
                }
                dialog.dismiss();
            });
        });

        return dialog;
    }
}
