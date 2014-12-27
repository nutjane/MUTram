/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nutjane.android.MUTram.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nutjane.android.MUTram.data.TimetableContract.TimetableEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Manages a local database for timetable data.
 */
public class TimetableDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = TimetableDbHelper.class.getSimpleName();

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "timetable.db";

    public TimetableDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context.getApplicationContext();

    }

    Context context;

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold timetable.  A timetable consists of the string supplied in the
        // tramID tramName and time
        final String SQL_CREATE_TIMETABLE_TABLE = "CREATE TABLE " + TimetableEntry.TABLE_NAME + " (" +
                TimetableEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TimetableEntry.COLUMN_TRAM_ID + " INTEGER NOT NULL, " +
                TimetableEntry.COLUMN_TRAM_NAME + " TEXT NOT NULL, " +
                TimetableEntry.COLUMN_TIME + " TEXT NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_TIMETABLE_TABLE);
            Log.d(LOG_TAG, "CREATE SUCCESS");

        //Add primary data to database

        try{
            AssetManager am = context.getAssets();
            Log.d(LOG_TAG, "ACCESS 2 SUCCESS");

            BufferedReader br = new BufferedReader(new InputStreamReader(am.open("data.csv")));
            Log.d(LOG_TAG, "ACCESS SUCCESS");

            String readLine = null;
            readLine = br.readLine(); //skip first line

            try{
                while((readLine = br.readLine())!=null){
                    String[] str = readLine.split(",");
                    String SQL_INSERT_DATA = "INSERT INTO "+TimetableEntry.TABLE_NAME
                            + " ("+ TimetableEntry.COLUMN_TRAM_ID
                            + ", " +TimetableEntry.COLUMN_TRAM_NAME
                            + ", " +TimetableEntry.COLUMN_TIME
                            +") VALUES ('" + str[0]
                            +"', '"+ str[1] +"', '" + str[2] +"');";

                    sqLiteDatabase.execSQL(SQL_INSERT_DATA);

                }
            } catch (IOException e){
                e.printStackTrace();
            }

        } catch (IOException e){
            e.printStackTrace();
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.*/
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TimetableEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
