package com.example.usmansh.findmycar;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SaveLocation extends AppCompatActivity {


    DatabaseReference location;
    Button SLbt;
    EditText inputTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_location);



        location = FirebaseDatabase.getInstance().getReference("Location");

        SLbt = (Button)findViewById(R.id.SLbt);
        inputTitle =(EditText)findViewById(R.id.inputTitle);

        SLbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String lat = String.valueOf(getIntent().getDoubleExtra("lat",0));
                String lang = String.valueOf(getIntent().getDoubleExtra("lang",0));

                Tracking Track = new Tracking();
                Track.setLat(lat);
                Track.setLang(lang);
                Track.setTitle(inputTitle.getText().toString());

                location.child(inputTitle.getText().toString()).setValue(Track).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            Toast.makeText(SaveLocation.this, "Location Saved..!", Toast.LENGTH_SHORT).show();

                            Intent goMain = new Intent(getApplicationContext(),MainActivity.class);
                            goMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(goMain);
                            finish();
                        }
                        else{
                            Toast.makeText(SaveLocation.this, "Error While Saving your location..!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });



    }
}
