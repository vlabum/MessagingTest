package ru.vlabum.android.apps.messagingtest;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import io.reactivex.subjects.PublishSubject;

public class FirebaseService extends FirebaseMessagingService {

    public final static PublishSubject<String> PUSH_BUS = PublishSubject.create();
    private final static String TAG = "DDD FriebaseService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            StringBuilder message = new StringBuilder()
                    .append("Title: ")
                    .append(remoteMessage.getNotification().getTitle())
                    .append(" Body: ")
                    .append(remoteMessage.getNotification().getBody());
            PUSH_BUS.onNext(message.toString());
        }
    }
}
