package com.example.icetea.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.icetea.models.Waitlist;
import com.example.icetea.models.WaitlistDB;
import com.example.icetea.util.Callback;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel responsible for managing and exposing the waitlist data
 * for a specific event. Handles real-time updates from Firestore and
 * provides methods to replace or revoke waitlist entries.
 *
 * <p>This ViewModel is lifecycle-aware and should be used in conjunction
 * with a Fragment or Activity. It also exposes toast messages via LiveData
 * to provide user feedback for operations.</p>
 */
public class WaitlistViewModel extends ViewModel {

    /** Controller handling the business logic for managing winners and notifications */
    ManageEventController controller = new ManageEventController();

    /** LiveData to post toast messages to the UI */
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();

    /** LiveData to expose the list of waitlist entries for observers */
    private final MutableLiveData<List<Waitlist>> waitlistLiveData = new MutableLiveData<>();

    /** Firestore listener for real-time updates to the waitlist */
    private ListenerRegistration listener;

    /**
     * Returns a LiveData that emits toast messages to be displayed on the UI.
     *
     * @return LiveData of toast message strings
     */
    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    /**
     * Returns a LiveData containing the list of waitlist entries for the event.
     *
     * @return LiveData of List of Waitlist objects
     */
    public LiveData<List<Waitlist>> getWaitlist() {
        return waitlistLiveData;
    }

    /**
     * Starts listening to Firestore for real-time updates to the waitlist of the given event.
     * Only attaches a listener if one is not already active.
     *
     * @param eventId the ID of the event to listen for
     */
    public void startListening(String eventId) {
        if (listener != null) return;

        listener = WaitlistDB.getInstance().listenToWaitlist(eventId, (snap, e) -> {
            if (snap != null) {
                List<Waitlist> list = new ArrayList<>();
                for (DocumentSnapshot doc : snap) {
                    Waitlist entry = doc.toObject(Waitlist.class);
                    if (entry != null) list.add(entry);
                }
                waitlistLiveData.postValue(list);
            }
        });
    }

    /**
     * Cleans up resources when the ViewModel is cleared. Specifically,
     * removes the Firestore listener to avoid memory leaks and sets the
     * listener reference to null so it can be re-attached if necessary.
     */
    @Override
    protected void onCleared() {
        if (listener != null) {
            listener.remove();
            listener = null;
        }
    }

    /**
     * Replaces the current winner of the event with another user from the waitlist.
     * Posts success or failure messages to {@link #toastMessage}.
     *
     * @param current the current waitlist entry to be replaced
     */
    public void replaceEntry(Waitlist current) {
        Log.d("tag", "BEFORE");

        controller.replaceWinner(current.getUserId(), current.getEventId(), new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                toastMessage.postValue("Successfully replaced winner");
            }

            @Override
            public void onFailure(Exception e) {
                toastMessage.postValue(e.getMessage());
            }
        });
    }

    /**
     * Revokes a user's winning status in the event, effectively cancelling them as a winner.
     * Posts success or failure messages to {@link #toastMessage}.
     *
     * @param current the waitlist entry representing the user to revoke
     */
    public void revokeEntry(Waitlist current) {
        controller.revokeWinner(current.getUserId(), current.getEventId(), new Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                toastMessage.postValue("Successfully cancelled winner");
            }

            @Override
            public void onFailure(Exception e) {
                toastMessage.postValue(e.getMessage());
            }
        });
    }

}