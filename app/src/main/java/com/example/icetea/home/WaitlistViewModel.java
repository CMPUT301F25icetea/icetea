package com.example.icetea.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.icetea.models.Waitlist;
import com.example.icetea.models.WaitlistDB;
import com.example.icetea.util.Callback;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class WaitlistViewModel extends ViewModel {
    ManageEventController controller = new ManageEventController();
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    public LiveData<String> getToastMessage() { return toastMessage; }
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
