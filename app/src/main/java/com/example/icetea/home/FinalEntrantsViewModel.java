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

public class FinalEntrantsViewModel extends ViewModel {

    private final MutableLiveData<List<Waitlist>> entrantsLiveData = new MutableLiveData<>();
    public LiveData<List<Waitlist>> getEntrants() { return entrantsLiveData; }

    private ListenerRegistration listener;

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

    @Override
    protected void onCleared() {
        if (listener != null) listener.remove();
    }
}
