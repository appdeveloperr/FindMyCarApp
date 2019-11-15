package com.example.usmansh.findmycar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LocationList extends AppCompatActivity {

    ListView listView;
    DatabaseReference location;
    ArrayList<String> locationList = new ArrayList<>();
    ArrayList<Tracking> findLocation = new ArrayList<>();
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);


        listView = (ListView)findViewById(R.id.listView);

        final String mylat = String.valueOf(getIntent().getDoubleExtra("lat",0));
        final String mylang = String.valueOf(getIntent().getDoubleExtra("lang",0));



        location = FirebaseDatabase.getInstance().getReference("Location");



        location.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postData : dataSnapshot.getChildren()) {

                    Tracking track = postData.getValue(Tracking.class);

                    if(track!=null){

                        findLocation.add(track);
                        locationList.add(track.getTitle());
                        adapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

         adapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,locationList);
        listView.setAdapter(adapter);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Tracking Track = findLocation.get(position);

                if(Track !=null ) {

                    Intent showMap = new Intent(getApplicationContext(), MapsActivity.class);
                    showMap.putExtra("myLat", mylat);
                    showMap.putExtra("myLang", mylang);
                    showMap.putExtra("locLat", Track.getLat());
                    showMap.putExtra("locLang", Track.getLang());
                    showMap.putExtra("locTitle", Track.getTitle());
                    startActivity(showMap);

                }
            }
        });




        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id)
            {
                Tracking Track = findLocation.get(pos);

                Toast.makeText(getApplicationContext(), "LongClicked", Toast.LENGTH_LONG).show();

                location.child(Track.getTitle()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LocationList.this, "Value Removed..!", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(LocationList.this, "Error while removing value..!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                return true;

            }
        });


    }



}

