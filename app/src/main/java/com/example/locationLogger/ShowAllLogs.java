package com.example.locationLogger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class ShowAllLogs extends AppCompatActivity implements LoaderCallbacks {

    //declaring ListView and SimpleCursorAdapter
    ListView LogsListView;
    SimpleCursorAdapter InspectionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_all_logs);
        //assigning ListView
        LogsListView = findViewById((R.id.listview));

        //assigning columns of the database
        String[] columns = new String[]{InspectionLogProvider.CLIENT_NAME, InspectionLogProvider.CLIENT_ADDRESS,
                InspectionLogProvider.LATITUDE, InspectionLogProvider.LONGITUDE, InspectionLogProvider.NOTES};

        //assigning textViews to columns of database
        int[] views = new int[]{R.id.nameText, R.id.addressText, R.id.latitudeText, R.id.longitudeText, R.id.notesText};

        //creating a new adapter
        InspectionsAdapter = new SimpleCursorAdapter(this, R.layout.rowitems, null, columns, views, 0);
        //setting that adapter
        LogsListView.setAdapter(InspectionsAdapter);

        LoaderManager.getInstance(this).initLoader(0, null, this);

    }
    //Creating menu and its items
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main2, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_go_back:
                finish();
                return true;
            case R.id.menu_quit:

                finishAffinity(); //finishes all current and parent activities
                System.exit(0);
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        //creating a path for uri
        Uri allTitles = Uri.parse("content://com.example.locationLogger.Inspections/inspections");

        CursorLoader cursorLoader = new CursorLoader(
                this,
                allTitles,
                null,
                null,
                null,
                "client_name DESC");

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        Cursor c = (Cursor) data;

        InspectionsAdapter.swapCursor((c));
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }
}
