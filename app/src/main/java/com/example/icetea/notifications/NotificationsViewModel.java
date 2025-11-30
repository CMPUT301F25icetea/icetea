package com.example.icetea.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.icetea.models.Notification;
import com.example.icetea.models.NotificationDB;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

/**
 * ViewModel for managing and observing notifications for the current user.
 *
 * <p>Provides:
 * <ul>
 *     <li>A LiveData list of all notifications for the user.</li>
 *     <li>A LiveData event for newly received notifications.</li>
 *     <li>Automatic real-time updates via Firestore listener.</li>
 * </ul>
 * </p>
 *
 * <p>This ViewModel should be scoped to an Activity or Fragment that displays notifications
 * to ensure LiveData is updated correctly and Firestore listeners are properly removed.</p>
 */
public class NotificationsViewModel extends ViewModel {

    private final MutableLiveData<List<Notification>> notificationsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Notification> newNotificationEvent = new MutableLiveData<>();
    private ListenerRegistration listenerRegistration;

    /**
     * Returns a LiveData representing the current list of notifications for the user.
     *
     * @return LiveData containing a list of {@link Notification} objects.
     */
    public LiveData<List<Notification>> getNotifications() {
        return notificationsLiveData;
    }

    /**
     * Returns a LiveData event representing a newly received notification.
     *
     * <p>This will post a value only when a notification is detected that wasn't
     * present in the previous list of notifications.</p>
     *
     * @return LiveData containing a {@link Notification} object for the new event.
     */
    public LiveData<Notification> getNewNotificationEvent() {
        return newNotificationEvent;
    }

    /**
     * Starts listening for real-time updates to notifications for the specified user.
     *
     * @param userId The Firebase user ID of the current user.
     */
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

    /**
     * Stops listening for notification updates.
     *
     * <p>Removes the Firestore listener to avoid memory leaks or unnecessary database reads.</p>
     */
    public void stopListening() {
        if (listenerRegistration != null) listenerRegistration.remove();
    }

    /**
     * Called when the ViewModel is cleared.
     *
     * <p>Automatically stops listening to notifications before being destroyed.</p>
     */
    @Override
    protected void onCleared() {
        stopListening();
        super.onCleared();
    }
}