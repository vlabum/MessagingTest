package ru.vlabum.android.apps.messagingtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    private static final String LOG = "DDD SmsBroadcast";
    private OnReceiveMessage listener;

    public void setListener(OnReceiveMessage listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG, "onReceive");
        final Bundle bundle = intent.getExtras();
        if (bundle != null) {
            try {
                Object[] pdus = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdus.length; i++) {
                    SmsMessage message = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    if (listener != null) {
                        listener.onReceiveMessage(message.getMessageBody());
                    }
                    Log.d(LOG, message.getMessageBody());
                }
            } catch (Exception e) {
                Log.d(LOG, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    interface OnReceiveMessage {
        void onReceiveMessage(String message);
    }
}
