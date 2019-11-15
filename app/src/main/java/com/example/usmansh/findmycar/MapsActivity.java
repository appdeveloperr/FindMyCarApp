package com.example.usmansh.findmycar;

import android.icu.text.DecimalFormat;
import android.location.Location;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Toast.makeText(this, "mylat "+getIntent().getStringExtra("myLat"), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "mylang "+getIntent().getStringExtra("myLang"), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "locLat "+getIntent().getStringExtra("locLat"), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "locLang " +getIntent().getStringExtra("locLang"), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "locTitle "+getIntent().getStringExtra("locTitle"), Toast.LENGTH_SHORT).show();


    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadLocationForThisUser() {


        double mylat  = Double.parseDouble(getIntent().getStringExtra("myLat"));
        double mylang = Double.parseDouble(getIntent().getStringExtra("myLang"));

        double frndlat  = Double.parseDouble(getIntent().getStringExtra("locLat"));
        double frndlang = Double.parseDouble(getIntent().getStringExtra("locLang"));


        //Add marker for friend location
                    LatLng friendLocation = new LatLng(frndlat,frndlang);


                    //Create Location from user coordinates
                    Location currentUser = new Location("");
                    currentUser.setLatitude(mylat);
                    currentUser.setLongitude(mylang);

                    //Create Location from Friends coordinates
                    Location friend= new Location("");
                    friend.setLatitude(frndlat);
                    friend.setLongitude(frndlang);

                    //Create Function which will calculate the distance between friend and user
                    //distance(currentUser,friend);


                    //Clear All  old Marker
                    mMap.clear();

                    //Add friend marker on map
                  mMap.addMarker(new MarkerOptions()
                            .position(friendLocation)
                            .title(getIntent().getStringExtra("locTitle"))
                            .snippet("Distance "+new DecimalFormat("#.#").format((currentUser.distanceTo(friend))/1000)+" km")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(frndlat,frndlang),16.0f));


                //Create Marker for Current User
                LatLng current = new LatLng(mylat,mylang);
                mMap.addMarker(new MarkerOptions().position(current).title("My Location"));


            }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        loadLocationForThisUser();
    }
}
