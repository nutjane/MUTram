package com.nutjane.android.MUTram.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class TimetableProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.nutjane.android.MUTram";
    static final String URL = "content://" + PROVIDER_NAME + "/timetable";
    static final Uri CONTENT_URI = Uri.parse(URL);

    public static final String _ID = "_id";
    public static final String TRAM_ID = "tram_id";
    public static final String TRAM_NAME = "tram_name";
    public static final String TIME = "time";

    public static final String LOG_TAG = TimetableProvider.class.getSimpleName();


    private static HashMap<String, String> TIMETABLE_PROJECTION_MAP;

    static final int TIME_ALL = 1;
    static final int TIME_TRAMID = 2;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "timetable", TIME_ALL);
        uriMatcher.addURI(PROVIDER_NAME, "timetable/#", TIME_TRAMID);
    }

    /**
     * Database specific constant declarations
     */
    private SQLiteDatabase db;
    static final String DATABASE_NAME = "TimetableDB";
    static final String TABLE_NAME = "timetable";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " tram_id TEXT NOT NULL, " +
                    " tram_name TEXT NOT NULL, " +
                    " time TEXT NOT NULL);";

    /**
     * Helper class that actually creates and manages
     * the provider's underlying data repository.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        Context context;
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context.getApplicationContext();
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_DB_TABLE);

            //Add primary data
            try{
                AssetManager am = context.getAssets();
                Log.d(LOG_TAG, "ACCESS context");

                BufferedReader br = new BufferedReader(new InputStreamReader(am.open("data.csv")));
                Log.d(LOG_TAG, "ACCESS FILE");

                String readLine = null;
                readLine = br.readLine(); //skip first line

                try{
                    while((readLine = br.readLine())!=null){
                        String[] str = readLine.split(",");
                        String SQL_INSERT_DATA = "INSERT INTO "+ TABLE_NAME
                                + " (tram_id, tram_name, time) VALUES ('"
                                + str[0]+"', '"+ str[1] +"', '" + str[2] +"');";

                        db.execSQL(SQL_INSERT_DATA);

                    }
                } catch (IOException e){
                    e.printStackTrace();
                }

            } catch (IOException e){
                e.printStackTrace();
            }
            Log.d(LOG_TAG, "DATA added ");



        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */
        db = dbHelper.getWritableDatabase();
        return (db == null)? false:true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /**
         * Add a new record
         */
        long rowID = db.insert(TABLE_NAME, "", values);
        /**
         * If record is added successfully
         */
        if (rowID > 0)
        {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case TIME_ALL:
                qb.setProjectionMap(TIMETABLE_PROJECTION_MAP);
                break;
            case TIME_TRAMID:
                qb.appendWhere( _ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (sortOrder == null || sortOrder == ""){
            /**
             * By default sort on time
             */
            sortOrder = TIME;
        }
        Cursor c = qb.query(db,	projection,	selection, selectionArgs,
                null, null, sortOrder);
        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case TIME_ALL:
                count = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case TIME_TRAMID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(TABLE_NAME, _ID +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case TIME_ALL:
                count = db.update(TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            case TIME_TRAMID:
                count = db.update(TABLE_NAME, values, _ID +
                        " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            /**
             * Get all time records
             */
            case TIME_ALL:
                return "vnd.android.cursor.dir/com.nutjane.android.timetable";
            /**
             * Get a particular record
             */
            case TIME_TRAMID:
                return "vnd.android.cursor.item/com.nutjane.android.timetable";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}