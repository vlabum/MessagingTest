package ru.vlabum.android.apps.messagingtest;


import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DDD " + MainActivity.class.getName();

    private Disposable disposable;

    private TextView tviewMesOut;
    private TextView tviewMesIn;
    private SmsBroadcastReceiver smsBroadcastReceiver;
    private SmsBroadcastReceiver.OnReceiveMessage listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tviewMesOut = findViewById(R.id.textView_message_out);
        tviewMesIn = findViewById(R.id.textView_message_in);
        listener = new SmsBroadcastReceiver.OnReceiveMessage() {
            @Override
            public void onReceiveMessage(String message) {
                tviewMesIn.setText(message);
            }
        };
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 200);

        smsBroadcastReceiver = new SmsBroadcastReceiver();
        smsBroadcastReceiver.setListener(listener);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(9999);
        intentFilter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        registerReceiver(smsBroadcastReceiver, intentFilter);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        Log.d(TAG, token);
                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });

        disposable = FirebaseService.PUSH_BUS
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        body -> Toast.makeText(this, body, Toast.LENGTH_SHORT).show(),
                        error -> Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
                );
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        unregisterReceiver(smsBroadcastReceiver);
        disposable.dispose();
    }

    public void composeMmsMessage(String message) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("sms:" + "+79080888888"));
        intent.putExtra("sms_body", message);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void sendSMS(View view) {
        composeMmsMessage(tviewMesOut.getText().toString());
    }

}
