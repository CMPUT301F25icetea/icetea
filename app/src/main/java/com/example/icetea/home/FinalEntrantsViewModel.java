package com.example.icetea.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.icetea.models.Waitlist;
import com.example.icetea.models.WaitlistDB;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for tracking the final entrants of an event.
 * <p>
 * This ViewModel listens to the {@link WaitlistDB} for entries
 * that have been accepted for a specific event and exposes them
 * as {@link LiveData} for UI observation.
 */
public class FinalEntrantsViewModel extends ViewModel {

    /** LiveData holding the list of accepted waitlist entries */
    private final MutableLiveData<List<Waitlist>> entrantsLiveData = new MutableLiveData<>();

    /**
     * Returns a {@link LiveData} object that can be observed for changes
     * in the list of accepted entrants.
     *
     * @return LiveData of accepted waitlist entries
     */
    public LiveData<List<Waitlist>> getEntrants() {
        return entrantsLiveData;
    }

    /** Listener registration for the Firestore waitlist listener */
    private ListenerRegistration listener;

    /**
     * Starts listening for accepted entrants for a given event ID.
     * Updates {@link #entrantsLiveData} whenever changes occur.
     * <p>
     * If the listener is already active, this method does nothing.
     *
     * @param eventId ID of the event for which to listen to accepted entrants
     */
    public void startListening(String eventId) {
        if (listener != null) return;

        listener = WaitlistDB.getInstance().listenToWaitlist(eventId, (snap, e) -> {
            if (snap != null) {
                List<Waitlist> acceptedEntries = new ArrayList<>();
                for (DocumentSnapshot doc : snap) {
                    Waitlist entry = doc.toObject(Waitlist.class);
                    if (entry != null && Waitlist.STATUS_ACCEPTED.equals(entry.getStatus())) {
                        acceptedEntries.add(entry);
                    }
                }
                entrantsLiveData.postValue(acceptedEntries);
            }
        });
    }

    /**
     * Cleans up resources when the ViewModel is destroyed.
     * Removes the Firestore listener if it is active.
     */
    @Override
    protected void onCleared() {
        if (listener != null) {
            listener.remove();
            listener = null;
        }
    }
}