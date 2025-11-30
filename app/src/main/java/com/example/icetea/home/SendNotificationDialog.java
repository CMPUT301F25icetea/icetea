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

/**
 * A custom DialogFragment for sending notifications.
 * Provides a simple UI with title and message inputs and validates them before sending.
 */
public class SendNotificationDialog extends androidx.fragment.app.DialogFragment {

    /**
     * Listener interface to handle sending notifications.
     */
    public interface Listener {
        /**
         * Called when the user presses the Send button with valid input.
         *
         * @param title   The title of the notification
         * @param message The message of the notification
         */
        void onSendNotification(String title, String message);
    }

    /** Listener to handle send action */
    private Listener listener;

    /**
     * Sets the listener for this dialog.
     *
     * @param listener The listener that will handle the send action
     */
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * Creates the dialog with title and message input fields.
     * Adds validation for empty fields and triggers the listener on successful input.
     *
     * @param savedInstanceState Saved state
     * @return The created dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // Create vertical layout programmatically
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        // Title input
        EditText titleInput = new EditText(requireContext());
        titleInput.setHint("Title");
        layout.addView(titleInput);

        // Message input
        EditText messageInput = new EditText(requireContext());
        messageInput.setHint("Message");
        layout.addView(messageInput);

        // Build AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Send Notification")
                .setView(layout)
                .setPositiveButton("Send", null) // Override later
                .setNegativeButton("Cancel", (d, w) -> d.dismiss())
                .create();

        // Customize button behavior on show
        dialog.setOnShowListener(d -> {
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded);
            }

            // Override positive button to validate inputs before dismiss
            Button sendButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            sendButton.setOnClickListener(v -> {
                String title = titleInput.getText().toString().trim();
                String message = messageInput.getText().toString().trim();

                // Input validation
                if (title.isEmpty()) {
                    titleInput.setError("Title cannot be empty");
                    return;
                }
                if (message.isEmpty()) {
                    messageInput.setError("Message cannot be empty");
                    return;
                }

                // Trigger listener if set
                if (listener != null) {
                    listener.onSendNotification(title, message);
                }

                // Close the dialog
                dialog.dismiss();
            });
        });

        return dialog;
    }
}