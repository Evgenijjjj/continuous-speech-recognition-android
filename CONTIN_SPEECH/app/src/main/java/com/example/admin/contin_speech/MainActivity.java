package com.example.admin.contin_speech;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
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

    Button btnStart, btnStop, btnUpdate;
    TextView textView;
    Intent intent;

    MyService mService;
    boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AddPermission();

        btnStart = (Button)findViewById(R.id.btnStart);
        btnStop = (Button)findViewById(R.id.btnStop);
        textView = (TextView)findViewById(R.id.textView);
        btnUpdate = (Button)findViewById(R.id.btnUpdate);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);

        intent = new Intent(MainActivity.this,MyService.class);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnStart){
            bindService(intent,mConnection, Service.BIND_AUTO_CREATE);
            //startService(intent);
        }
        if(v.getId() == R.id.btnStop){
            //stopService(intent);
            //LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,new IntentFilter("intentKey"));

            if (mBound) {
                unbindService(mConnection);
                mBound = false;
            }
        }
        if(v.getId() == R.id.btnUpdate){
            if(mBound){
                textView.setText(mService.getMESSAGE());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stopService(intent);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("key");
            textView.setText(message);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    private void AddPermission() {
        int permissionStatus= ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        String[] arr = {Manifest.permission.RECORD_AUDIO};

        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arr,
                    1);
        }
    }

}
