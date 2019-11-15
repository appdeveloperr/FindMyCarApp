package com.example.usmansh.findmycar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
        ,LocationListener {

    Button saveLoc,getLoc;

    DatabaseReference onlineRef,currentUserRef,counterRef,locations,login;


    private static final int MY_PERMISSION_REQUEST_CODE = 7171;
    private static final int PLAY_SERVICES_RES_REQUEST = 7172;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static int UPDATE_INTERVAL = 3000;
    private static int FASTEST_INTERVAL = 1000;
    private static int DISTANCE = 10;
    FirebaseAuth mAuth;


    LocationManager locationManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        saveLoc = (Button)findViewById(R.id.savelocationbt);
        getLoc  = (Button)findViewById(R.id.getlocationbt);

        locations = FirebaseDatabase.getInstance().getReference("GPSLocations");
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);



        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            },MY_PERMISSION_REQUEST_CODE);

        }
        else{

            if(checkPlayServices()){

                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
                //locationMange();
            }
        }




        getLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //createLocationRequest();
                displayLocation();

                if(mLastLocation != null) {
                    Intent goSaveLoaction = new Intent(getApplicationContext(), LocationList.class);
                    goSaveLoaction.putExtra("lat", mLastLocation.getLatitude());
                    goSaveLoaction.putExtra("lang", mLastLocation.getLongitude());
                    startActivity(goSaveLoaction);
                }else{
                    Toast.makeText(MainActivity.this, "Not Getting your location..!\nCheck Internet connection..!", Toast.LENGTH_SHORT).show();
                }


            }
        });



        saveLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //buildGoogleApiClient();
                //createLocationRequest();
                displayLocation();

                if(mLastLocation != null) {
                    Intent goSaveLoaction = new Intent(getApplicationContext(), SaveLocation.class);
                    goSaveLoaction.putExtra("lat", mLastLocation.getLatitude());
                    goSaveLoaction.putExtra("lang", mLastLocation.getLongitude());
                    startActivity(goSaveLoaction);
                }else{
                    Toast.makeText(MainActivity.this, "Not Getting your location..!\nCheck Internet connection..!", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }



    private void locationMange() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1000, new android.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    LatLng latLng = new LatLng(latitude, longitude);

                    List<Address> address = null;

                    Geocoder geocoder = new Geocoder(getApplicationContext());

                    try {
                        address = geocoder.getFromLocation(latitude, longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    //Update to FireBase
                    locations.child("User LatLang").
                            setValue(new Tracking(
                                    String.valueOf(latitude),
                                    String.valueOf(longitude))).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Location uploaded", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getApplicationContext(), "Location not uploaded", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }catch (Exception e){
            Toast.makeText(this, "Check Network Connection..!", Toast.LENGTH_SHORT).show();
        }


    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){

            case MY_PERMISSION_REQUEST_CODE:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(checkPlayServices()){

                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }
                }
            }
            break;
        }
    }

    private void displayLocation() {


        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);



    }

    private void createLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000)
                .setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this).
                addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();


    }

    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS) {

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {

                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RES_REQUEST).show();
            } else {
                Toast.makeText(this, "This Device is not supported.!", Toast.LENGTH_SHORT).show();
                finish();
            }

            return false;
        }
        return true;
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //displayLocation();
        //startLocationUpdate();
        Toast.makeText(this, "Client Connected..!", Toast.LENGTH_SHORT).show();
    }

    private void startLocationUpdate() {


        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest, (com.google.android.gms.location.LocationListener) this);

    }

    @Override
    public void onConnectionSuspended(int i) {

        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        Toast.makeText(this, "changed LAt: "+location.getLatitude(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Change Lang: "+location.getLongitude(), Toast.LENGTH_SHORT).show();
        //displayLocation();
        //startLocationUpdate();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    protected void onStart() {
        super.onStart();
        if(mGoogleApiClient != null){
            mGoogleApiClient.connect();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mGoogleApiClient != null){
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
    }




}
