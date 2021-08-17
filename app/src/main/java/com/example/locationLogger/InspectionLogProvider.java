package com.example.locationLogger;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class InspectionLogProvider extends ContentProvider {

    //declaring variables

    static final String PROVIDER_NAME = "com.example.locationLogger.Inspections";
    static final Uri CONTENT_URI = Uri.parse("content://"+ PROVIDER_NAME + "/inspections");
    static final String _ID = "_id";
    static final String CLIENT_NAME = "client_name";
    static final String CLIENT_ADDRESS = "client_address";
    static final String LATITUDE = "latitude";
    static final String LONGITUDE = "longitude";
    static final String NOTES = "notes";
    static final int INSPECTIONS = 1;
    static final int INSPECTIONS_ID = 2;

    //declaring uriMatcher
    private static final UriMatcher uriMatcher;

    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "inspections", INSPECTIONS);
        uriMatcher.addURI(PROVIDER_NAME, "inspections/#", INSPECTIONS_ID);
    }
    //code for creating a database
    DatabaseHelper dbHelper;

    static final String DATABASE_NAME = "Inspections";
    static final String DATABASE_TABLE = "logs";
    static final int DATABASE_VERSION = 1;

    static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE +
                    " (_id integer primary key autoincrement, "
                    + "client_name text not null, client_address text not null," +
                    "latitude double not null, longitude double not null, notes text not null)";


    private static class DatabaseHelper extends SQLiteOpenHelper{
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(DATABASE_CREATE);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            Log.w("Provider database",
                    "Upgrading database from version " +
                            oldVersion + " to " + newVersion +
                            ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS client_name");
            onCreate(db);
        }
    }

    //delete method for database
    @Override
    public int delete(Uri arg0, String arg1, String[] arg2) {

        SQLiteDatabase inspectionsDB = dbHelper.getWritableDatabase();

        int count=0;
        switch (uriMatcher.match(arg0)){

            case INSPECTIONS:

                count = inspectionsDB.delete(
                        DATABASE_TABLE,
                        arg1,
                        arg2);
                break;
            case INSPECTIONS_ID:

                String id = arg0.getPathSegments().get(1);
                count = inspectionsDB.delete(
                        DATABASE_TABLE,
                        _ID + " = " + id +
                                (!TextUtils.isEmpty(arg1) ? " AND (" +
                                        arg1 + ')' : ""),
                        arg2);
                break;
            default: throw new IllegalArgumentException("Unknown URI " + arg0);

        }
        getContext().getContentResolver().notifyChange(arg0, null);
        return count;
    }

    //getType method for database
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){


            case INSPECTIONS:
                return "vnd.android.cursor.dir/vnd.learn2develop.inspections ";


            case INSPECTIONS_ID:
                return "vnd.android.cursor.item/vnd.learn2develop.inspections ";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    //Code to insert entries
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase inspectionsDB = dbHelper.getWritableDatabase();


        long rowID = inspectionsDB.insert(
                DATABASE_TABLE,
                "",
                values);

        if (rowID > 0)
        {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);


            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new DatabaseHelper(context);
        return (dbHelper == null)? false:true;
    }

    //query method for database
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {


        SQLiteDatabase inspectionsDB = dbHelper.getWritableDatabase();

        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
        sqlBuilder.setTables(DATABASE_TABLE);

        if (uriMatcher.match(uri) == INSPECTIONS_ID)

            sqlBuilder.appendWhere(_ID + " = " + uri.getPathSegments().get(1));

        if (sortOrder == null || sortOrder == "")

            sortOrder = CLIENT_NAME + "ASC";
        Cursor c = sqlBuilder.query(
                inspectionsDB,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);


        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    //update method for database
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase inspectionsDB = dbHelper.getWritableDatabase();

        int count = 0;
        switch (uriMatcher.match(uri)){
            case INSPECTIONS:

                count = inspectionsDB.update(
                        DATABASE_TABLE,
                        values,
                        selection,
                        selectionArgs);
                break;
            case INSPECTIONS_ID:

                count = inspectionsDB.update(
                        DATABASE_TABLE,
                        values,
                        _ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? " AND (" +
                                        selection + ')' : ""),
                        selectionArgs);
                break;
            default: throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
