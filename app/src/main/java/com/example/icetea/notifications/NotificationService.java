package com.example.icetea.notifications;

import androidx.annotation.NonNull;

import com.example.icetea.models.UserDB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class NotificationService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            UserDB.getInstance().saveFcmToken(uid, token);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();
        String eventName = data.get("eventName");
        String typeOrMessage = data.get("type");     // "won", "lost", "replacement"
        String customMessage = data.get("message");  // optional

        if (eventName == null) eventName = "Event";

        if (customMessage != null && !customMessage.isEmpty()) {
            //TODO: WHEN NOTIFICATION HELPER IS IMPLEMENTED
            //NotificationHelper.showStatusNotification(this, eventName, customMessage);
        } else if (typeOrMessage != null) {
            //NotificationHelper.showStatusNotification(this, eventName, typeOrMessage);
        }
    }
}
