package com.example.locationtracker;


import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.location.LocationManagerCompat;

import retrofit2.Call;
import retrofit2.Callback;

import static com.example.locationtracker.App.CHANNEL_ID;


public class ExampleService extends Service {
    CountDownTimer yourCountDownTimer;
    private Response response_data;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



         int time_interval = intent.getIntExtra("inputExtra", 6000);
         time_interval=time_interval*6000;
        String NOTIFICATION_CHANNEL_ID = "com.example.locationtracker";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)

                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);

        PendingIntent contentIntent =
                PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        timer(time_interval);
        //stopSelf();
        return START_NOT_STICKY;
    }

    private void timer(int time_interval) {

        yourCountDownTimer = new CountDownTimer(300000000, time_interval) {

            public void onTick(long millisUntilFinished) {
              //  Log.d("Timer", "seconds remaining: " + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
                getlocation(getApplicationContext());
            }

            public void onFinish() {

            }

        }.start();
    }

    private void getlocation(Context context) {

        getLocation();

    }

    public Location getLocation() {


        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            if (isLocationEnabled(getApplicationContext())==true){
                Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocationGPS != null) {
                    Location loc =  locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                  //  Log.d("long", " Not Real Longtitude->"+String.valueOf(loc.getLongitude()));
                    push_data(loc);
                    return lastKnownLocationGPS;
                } else {
                    Location loc =  locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    //Log.d("long", " Real Longtitude->"+String.valueOf(loc.getLongitude()));
                    push_data(loc);
                    return loc;
                }
            }else{
                Toast.makeText(getApplicationContext(),"Location is Disabled\n turn on ",Toast.LENGTH_SHORT).show();
                return null;
            }

        } else {
            return null;
        }
    }

    private void push_data(Location loc ) {
         final String id=getDeviceUniqueID(this);

        Call<Response> responseCall=RetroClient
                .getInstance()
                .api_push_data()
                .insert_new_location(loc.getLongitude(),loc.getLatitude(),id);
        responseCall.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                response_data=response.body();
                Log.d("Api_call","Success");
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Log.d("Api_call", String.valueOf(t));

            }
        });
    }

    public String getDeviceUniqueID(ExampleService activity){
        String device_unique_id = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return device_unique_id;
    }

    private boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return LocationManagerCompat.isLocationEnabled(locationManager);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

      yourCountDownTimer.cancel();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}