package com.example.icetea.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.icetea.models.Notification;
import com.example.icetea.models.NotificationDB;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

public class NotificationsViewModel extends ViewModel {

    private final MutableLiveData<List<Notification>> notificationsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Notification> newNotificationEvent = new MutableLiveData<>();
    private ListenerRegistration listenerRegistration;

    public LiveData<List<Notification>> getNotifications() {
        return notificationsLiveData;
    }

    public LiveData<Notification> getNewNotificationEvent() {
        return newNotificationEvent;
    }

    public void startListening(String userId) {
        NotificationDB notificationDB = new NotificationDB();
        listenerRegistration = notificationDB.listenNotificationsForUser(userId, notifications -> {
            List<Notification> oldList = notificationsLiveData.getValue();
            notificationsLiveData.postValue(notifications);

            if (oldList != null && !notifications.isEmpty()) {
                for (Notification n : notifications) {
                    boolean isNew = oldList.stream().noneMatch(o -> o.getTimestamp().equals(n.getTimestamp()));
                    if (isNew) {
                        newNotificationEvent.postValue(n);
                        break;
                    }
                }
            }
        });
    }

    public void stopListening() {
        if (listenerRegistration != null) listenerRegistration.remove();
    }

    @Override
    protected void onCleared() {
        stopListening();
        super.onCleared();
    }
}
