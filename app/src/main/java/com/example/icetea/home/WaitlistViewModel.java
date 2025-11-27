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

public class WaitlistViewModel extends ViewModel {

    private final MutableLiveData<List<Waitlist>> waitlistLiveData = new MutableLiveData<>();
    private ListenerRegistration listener;

    public LiveData<List<Waitlist>> getWaitlist() {
        return waitlistLiveData;
    }

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

    @Override
    protected void onCleared() {
        if (listener != null) listener.remove();
    }
}
