package com.example.hakayler.myplace;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    static SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

                                /* Location listeners*/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
            mMap.clear();
            LatLng userlocation = new LatLng(location.getLatitude(),location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userlocation,15));
            mMap.addMarker(new MarkerOptions().position(userlocation).title("You're here!"));

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

            Intent intent = getIntent();
            String info = intent.getStringExtra("info");

            if (info.equalsIgnoreCase("new")){

                mMap.clear();
                Location userlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                LatLng lastuserlocation = new LatLng(userlocation.getLatitude(),userlocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastuserlocation,15));


            } else {

                mMap.clear();
                int position = intent.getIntExtra("position",0);
                LatLng location = new LatLng(MainActivity.locations.get(position).latitude,MainActivity.locations.get(position).longitude);
                mMap.addMarker( new MarkerOptions().position(location).title(MainActivity.name.get(position)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));

            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Intent intent = getIntent();
                String info = intent.getStringExtra("info");
                if (info.equalsIgnoreCase("new")){

                    mMap.clear();
                    Location userlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    LatLng lastuserlocation = new LatLng(userlocation.getLatitude(),userlocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastuserlocation,15));


                } else {

                    mMap.clear();
                    int position = intent.getIntExtra("position",0);
                    LatLng location = new LatLng(MainActivity.locations.get(position).latitude,MainActivity.locations.get(position).longitude);
                    mMap.addMarker( new MarkerOptions().position(location).title(MainActivity.name.get(position)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));

                }
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = "";

        try {
            List<Address> addresslist = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if (addresslist != null && addresslist.size() > 0){

                if (addresslist.get(0).getThoroughfare() != null){
                    address += addresslist.get(0).getThoroughfare();

                    if (addresslist.get(0).getSubThoroughfare() != null){
                        address += addresslist.get(0).getSubThoroughfare();
                    }
                }
            } else {
               address = "New place!";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
        Toast.makeText(getApplicationContext(),"New address added.",Toast.LENGTH_LONG).show();

        database = this.openOrCreateDatabase("Places", MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS places (name VARCHAR, latitude VARCHAR, longitude VARCHAR)");

        Double l1 = latLng.latitude;
        Double l2 = latLng.longitude;

        String coord1 = l1.toString();
        String coord2 = l2.toString();

        String toCompile = "INSERT INTO places (name, latitude, longitude) VALUES (?, ?, ?)";
        SQLiteStatement statement = database.compileStatement(toCompile);
        statement.bindString(1,address);
        statement.bindString(2,coord1);
        statement.bindString(3,coord2);
        statement.execute();


    }
}
