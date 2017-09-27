package com.example.hakayler.myplace;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;

   static ArrayList<String> name = new ArrayList<String>();
   static ArrayList<LatLng> locations = new ArrayList<LatLng>();
    ArrayAdapter arrayAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_place,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        try{
            MapsActivity.database = this.openOrCreateDatabase("Places",MODE_PRIVATE,null);

            Cursor cursor = MapsActivity.database.rawQuery("SELECT * FROM places",null);

            int nameX = cursor.getColumnIndex("name");
            int latitudeX = cursor.getColumnIndex("latitude");
            int logitudeX = cursor.getColumnIndex("longitude");

            cursor.moveToFirst();

            while (cursor != null){

                String nameFromdatabase = cursor.getString(nameX);
                String latitudeFromdatabase = cursor.getString(latitudeX);
                String longitudeFromdatabase = cursor.getString(logitudeX);

                name.add(nameFromdatabase);

                Double l1 = Double.parseDouble(latitudeFromdatabase);
                Double l2 = Double.parseDouble(longitudeFromdatabase);

                LatLng locationFromdatabase = new LatLng(l1,l2);
                locations.add(locationFromdatabase);
                cursor.moveToNext();
        }

        } catch (Exception e){
            e.printStackTrace();
        }

       arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,name);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("info", "old");
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
    }

}
