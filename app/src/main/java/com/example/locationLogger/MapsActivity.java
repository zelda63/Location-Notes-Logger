package com.example.locationLogger;


import androidx.appcompat.app.AppCompatCallback;
import androidx.appcompat.view.ActionMode;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.Loader;

import android.content.ContentValues;
import android.net.Uri;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LoaderCallbacks, AppCompatCallback {

    //declaring variables and objects
    private GoogleMap mMap;
    private Marker CurrentMarker;

    private EditText nameEdit;
    private EditText addressEdit;
    private EditText LatEdit;
    private EditText LngEdit;
    private EditText NotesEdit;

    private Button saveButton;

    SimpleCursorAdapter InspectionsAdapter;

    //defining database columns
    String[] columns = new String[] {InspectionLogProvider.CLIENT_NAME, InspectionLogProvider.CLIENT_ADDRESS,
    InspectionLogProvider.LATITUDE, InspectionLogProvider.LONGITUDE, InspectionLogProvider.NOTES};

    //defining textViews for all columns for our database
    int[] views = new int[] {R.id.txtName, R.id.txtAddress, R.id.txtLatitude, R.id.txtLongitude, R.id.txtNotes};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //assigning editText
        nameEdit = (EditText) findViewById(R.id.txtName);
        addressEdit = (EditText) findViewById(R.id.txtAddress);
        LatEdit = (EditText) findViewById(R.id.txtLatitude);
        LngEdit = (EditText) findViewById(R.id.txtLongitude);
        NotesEdit = (EditText) findViewById(R.id.txtNotes);
        saveButton = (Button) findViewById(R.id.saveBtn);

    }

    //Code for menu and its items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_show_all:
                startActivity(new Intent(this, ShowAllLogs.class));
                return true;
            case R.id.menu_quit:
                finishAffinity(); //to finish all the current and parent activities
                System.exit(0); //then exit the application
            default:
                return super.onOptionsItemSelected(item);
        }

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
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            LatLng Saskpoly = new LatLng(50.4029, -105.5498);

            CurrentMarker = mMap.addMarker(new MarkerOptions().position(Saskpoly).title("Saskpolytech in Moose Jaw"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(Saskpoly));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Saskpoly,15));


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                Log.d("DEBUG","Map clicked [" + point.latitude + " / " + point.longitude + "]");
               //declaring a new geoCoder object
                Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
                try {
                    //setting Latitude and Longitude values obtained
                    LatEdit.setText(String.valueOf(point.latitude));
                    LngEdit.setText(String.valueOf(point.longitude));

                    List<Address> addresses = geoCoder.getFromLocation(point.latitude,point.longitude,1);

                    String add = ""; //string variable that hold the address value
                    if (addresses.size() > 0)
                    {
                        for (int i=0; i <= addresses.get(0).getMaxAddressLineIndex(); i++)
                            add += addresses.get(0).getAddressLine(i) + "\n";
                    }

                    CurrentMarker.remove(); // to remove current marker
                    CurrentMarker = mMap.addMarker(new MarkerOptions().position(point).title(add)); //then add a new marker at the location the user just clicked on
                    CurrentMarker.showInfoWindow(); //to show address on the marker
                    addressEdit.setText(add.toString()); // displays the address in the respective edit box
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point,18)); // moves the camera when new marker is placed

                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });


    }

    //Method for Save button...it inserts the values in EditTexts into the database
    public void onClickSave(View view) {
        ContentValues values = new ContentValues();

        values.put(InspectionLogProvider.CLIENT_NAME, ((EditText) findViewById(R.id.txtName)).getText().toString());
        values.put(InspectionLogProvider.CLIENT_ADDRESS, ((EditText)  findViewById(R.id.txtAddress)).getText().toString());
        values.put(InspectionLogProvider.LATITUDE, ((EditText) findViewById(R.id.txtLatitude)).getText().toString());
        values.put(InspectionLogProvider.LONGITUDE, ((EditText)  findViewById(R.id.txtLongitude)).getText().toString());
        values.put(InspectionLogProvider.NOTES, ((EditText) findViewById(R.id.txtNotes)).getText().toString());

        Uri uri = getContentResolver().insert(InspectionLogProvider.CONTENT_URI, values);

        Toast.makeText(getBaseContext(),"Data Saved Successfully!", Toast.LENGTH_LONG).show();
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }


    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }
}