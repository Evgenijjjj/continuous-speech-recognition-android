package com.example.admin.contin_speech;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnStart, btnStop;
    TextView textView;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AddPermission();

        btnStart = (Button)findViewById(R.id.btnStart);
        btnStop = (Button)findViewById(R.id.btnStop);
        textView = (TextView)findViewById(R.id.textView);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);

        intent = new Intent(MainActivity.this,MyService.class);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnStart){
            startService(intent);
        }
        if(v.getId() == R.id.btnStop){
            stopService(intent);
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,new IntentFilter("intentKey"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intent);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("key");
            textView.setText(message);
        }
    };


    private void AddPermission() {
        int permissionStatus= ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        String[] arr = {""};
        arr[0] = Manifest.permission.RECORD_AUDIO;

        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arr,
                    1);
        }
    }

}
