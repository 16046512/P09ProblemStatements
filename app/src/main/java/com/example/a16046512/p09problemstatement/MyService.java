package com.example.a16046512.p09problemstatement;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileWriter;

public class MyService extends Service {

    boolean started;
    FusedLocationProviderClient client;
    LocationCallback mLocationCallback;
    TextView tvLat,tvLng;
    String folderLocation;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.d("Service", "Service created");
        super.onCreate();
        folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyFolder";
        client = 	LocationServices.getFusedLocationProviderClient(this);
        File folder = new File(folderLocation);
        if (folder.exists() == false){
            boolean result = folder.mkdir();
            if (result == true){
                Log.i("File Read/Write", "Folder created");

            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (started == false){
            started = true;
            if (checkPermission()== true) {
                Log.d("Service", "Service started");
                LocationRequest mLocationRequest = LocationRequest.create();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setInterval(10000);
                mLocationRequest.setFastestInterval(5000);
                mLocationRequest.setSmallestDisplacement(100);
                MainActivity main = new MainActivity();
                final TextView tvLat = main.tvLat;
                final TextView tvLng = main.tvLng;
                LocationCallback mLocationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult != null) {
                            Location data = locationResult.getLastLocation();
                            double lat = data.getLatitude();
                            double lng = data.getLongitude();
//                            tvLat.setText("Latitude : " + lat);
//                            tvLng.setText("Longitude : " + lng);
                            Toast.makeText(MyService.this,"Detected\nLat: "+lat+" Lng: "+lng,Toast.LENGTH_LONG).show();

                        }
                    }

                    ;
                };

                client.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            }else{
                String msg = "Permission not granted to retrieve location info";
                Toast.makeText(MyService.this,msg,Toast.LENGTH_LONG).show();
//                ActivityCompat.requestPermissions(MyService.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},0);

            }


        } else {

            Log.d("Service", "Service is still running");
            if (checkPermission()== true) {
                LocationRequest mLocationRequest = LocationRequest.create();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setInterval(10000);
                mLocationRequest.setFastestInterval(5000);
                mLocationRequest.setSmallestDisplacement(100);
                MainActivity main = new MainActivity();
                final TextView tvLat = main.tvLat;
                final TextView tvLng = main.tvLng;
                LocationCallback mLocationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult != null) {
                            Location data = locationResult.getLastLocation();
                            double lat = data.getLatitude();
                            double lng = data.getLongitude();
//                            tvLat.setText("Latitude : " + lat);
//                            tvLng.setText("Longitude : " + lng);
                            try {
                                String folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyFolder";
                                File targetFile = new File(folderLocation, "data.txt");
                                FileWriter writer = new FileWriter(targetFile, true);
                                writer.write("Lat: "+lat+"Lng:" +lng+ "\n");
                                writer.flush();
                                writer.close();
                            } catch (Exception e) {
                                Toast.makeText(MyService.this, "Failed to write!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }

                    ;
                };

                client.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            }else{
                String msg = "Permission not granted to retrieve location info";
                Toast.makeText(MyService.this,msg,Toast.LENGTH_LONG).show();
//                ActivityCompat.requestPermissions(MyService.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},0);

            }

        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("Service", "Service exited");

        super.onDestroy();
        client.removeLocationUpdates(mLocationCallback);
    }

    private boolean checkPermission(){
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MyService.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MyService.this, Manifest.permission.ACCESS_FINE_LOCATION);

        int read = ContextCompat.checkSelfPermission(
                MyService.this, Manifest.permission.READ_EXTERNAL_STORAGE);

        int write = ContextCompat.checkSelfPermission(
                MyService.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED
                ||read==PermissionChecker.PERMISSION_GRANTED
                ||write==PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

}
