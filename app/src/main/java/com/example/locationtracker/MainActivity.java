package com.example.locationtracker;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

public class MainActivity extends AppCompatActivity {
    private static final int TAG_CODE_PERMISSION_LOCATION = 1;
    private boolean loc_code ;
    private int LOCATION_PERMISSION_CODE = 1;
    private int STORAGE_PERMISSION_CODE = 1;
    private TextView txt_1, txt_2;
    private Button btn,btn_start,btn_stop;
    private TextView txt_err_status;
    private Spinner spinner;
    private static final String[] paths = {"2 Minutes ", "5 Minutes", "10 Minutes"};

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final int[] time_interval = new int[1];
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //shared preference starts
       //   Log.d("unique_id",getDeviceUniqueID(this));

//
//        //retrieve a reference to an instance of TelephonyManager
       
//        shared preference ends

        txt_err_status=(TextView)findViewById(R.id.txt_err_status);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_stop = (Button) findViewById(R.id.btn_stop);

        change_btn_status();

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),"Permission on",Toast.LENGTH_LONG).show();

            loc_code=true;
            Log.d("is_loc_on", String.valueOf(isLocationEnabled(getApplicationContext())));
        } else {
            requestlocationPermision();
            requestStoragePermission();

        }




//       spinner starts
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item, paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            int tmp_time_interval;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0)
                    tmp_time_interval = 2;
                else if (position == 1)
                    tmp_time_interval = 5;
                else if (position == 2)
                    tmp_time_interval = 10;
                time_interval[0] = tmp_time_interval;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // can leave this empty
            }
        });
        //        spinner ends

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isLocationEnabled(getApplicationContext())==true && loc_code==true){
                    Intent serviceIntent = new Intent(getApplicationContext(), ExampleService.class);
                    serviceIntent.putExtra("inputExtra", time_interval[0]);
                    ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);
                    btn_status("btn_status",true);
                    change_btn_status();

                }else{
                    if (isLocationEnabled(getApplicationContext())==false){
                        txt_err_status.setText("Location is Disabled \nTurn on location and restart the app");
                    }
                  //  Log.d("check_erro", String.valueOf(isLocationEnabled(getApplicationContext())));
                  //  Log.d("check_erro", String.valueOf(loc_code));
                }


            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent(getApplicationContext(), ExampleService.class);
                stopService(serviceIntent);
                btn_status("btn_status",false);
                change_btn_status();
            }
        });


    }

    private void change_btn_status() {
//        btn_status("btn_status",true);
//        get_btn_status("btn_status");
        if (get_btn_status("btn_status")==true){
            btn_start.setEnabled(false);
            btn_stop.setEnabled(true);
        }else{
            btn_start.setEnabled(true);
            btn_stop.setEnabled(false);
        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of tracking location needs location permission")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    private void requestlocationPermision() {



            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    TAG_CODE_PERMISSION_LOCATION);

    }


    private boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return LocationManagerCompat.isLocationEnabled(locationManager);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
               // restartActivity();



                loc_code=true;
                if (isLocationEnabled(getApplicationContext())==false){

                    txt_err_status.setText("Location is Disabled \nTurn on location and restart the app");
                    btn_start.setEnabled(false);
                    btn_stop.setEnabled(false);
                }
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void btn_status(String key, boolean value) {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    private boolean get_btn_status(String key){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        boolean savedPref = sharedPreferences.getBoolean(key,false);
       // Log.d("pref_value", String.valueOf(savedPref));
         return  savedPref;
    }

    public String getDeviceUniqueID(Activity activity){
        String device_unique_id = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return device_unique_id;
    }

    public void restartActivity(){
        Intent mIntent = getIntent();
        finish();
        startActivity(mIntent);
    }
}