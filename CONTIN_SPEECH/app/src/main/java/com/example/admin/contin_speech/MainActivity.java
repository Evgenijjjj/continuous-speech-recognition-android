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
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnStart, btnStop;
    TextView textView;
    Intent intent;

    MyService mService;
    boolean mBound = false;

    TaskServiceAnswer taskServiceAnswer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AddPermission();
        btnStart = (Button)findViewById(R.id.btnStart);
        btnStop = (Button)findViewById(R.id.btnStop);
        textView = (TextView)findViewById(R.id.textView);

        btnStop.setVisibility(View.INVISIBLE);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);

        intent = new Intent(MainActivity.this,MyService.class);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnStart){
            btnStop.setVisibility(View.VISIBLE);
            btnStart.setVisibility(View.INVISIBLE);

            Toast.makeText(this,"Recognizing start",Toast.LENGTH_SHORT).show();
            try{
                taskServiceAnswer = new TaskServiceAnswer();
                taskServiceAnswer.execute();
            }catch (Exception e ){
                Log.d("testServiceEE",e.toString() + "\n" + taskServiceAnswer.getStatus().toString());
            }
        }
        if(v.getId() == R.id.btnStop){
            if (taskServiceAnswer != null) {
                btnStart.setVisibility(View.VISIBLE);
                btnStop.setVisibility(View.INVISIBLE);

                Toast.makeText(this,"Recognizing stop",Toast.LENGTH_SHORT).show();
                mService.setMESSAGE("");
                taskServiceAnswer.setEXECUTE_FLAG(false);
                taskServiceAnswer.cancel(false);
                //taskServiceAnswer = null;
                textView.setText("Tap start button to execute speech recognizing");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

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

    class TaskServiceAnswer extends AsyncTask<Void,Void,Void>{
        private boolean EXECUTE_FLAG = true;

        @Override
        protected void onPreExecute() {
            try {
                bindService(intent, mConnection, Service.BIND_AUTO_CREATE);
                EXECUTE_FLAG = true;
            }catch(Exception e){
                Log.d("testService",e.toString());
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mBound) {
                unbindService(mConnection);
                mBound = false;
            }
            Log.d("testService","onPostExecute!!");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            try {
                textView.setText(mService.getMESSAGE());
            }catch (Exception e){
                Log.d("testService",e.toString());
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while(EXECUTE_FLAG) {
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress();
            }
            this.onPostExecute(null);
            return null;
        }

        public void setEXECUTE_FLAG(boolean EXECUTE_FLAG) {
            this.EXECUTE_FLAG = EXECUTE_FLAG;
        }
    }


    private void AddPermission() {
        int permissionStatus= ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        String[] arr = {Manifest.permission.RECORD_AUDIO};

        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arr,
                    1);
        }
    }

}
