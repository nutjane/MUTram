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
package com.nutjane.android.MUTram;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.nutjane.android.MUTram.data.TimetableContract.TimetableEntry;
import com.nutjane.android.MUTram.data.TimetableDbHelper;

import java.util.Map;
import java.util.Set;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();
    static final String TEST_TRAM_ID = "1";
    static final String TEST_TIME = "08:30";

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(TimetableDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new TimetableDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        TimetableDbHelper dbHelper = new TimetableDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = createFirstTramValue();

        long timetableRowID;
        timetableRowID = db.insert(TimetableEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(timetableRowID != -1);
        Log.d(LOG_TAG, "New row id: " + timetableRowID);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                TimetableEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        validateCursor(cursor, testValues);

//        // Fantastic.  Now that we have a location, add some weather!
//        ContentValues weatherValues = createWeatherValues(timetableRowID);
//
//        long weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);
//        assertTrue(weatherRowId != -1);
//
//        // A cursor is your primary interface to the query results.
//        Cursor weatherCursor = db.query(
//                WeatherEntry.TABLE_NAME,  // Table to Query
//                null, // leaving "columns" null just returns all the columns.
//                null, // cols for "where" clause
//                null, // values for "where" clause
//                null, // columns to group by
//                null, // columns to filter by row groups
//                null  // sort order
//        );
//
//        validateCursor(weatherCursor, weatherValues);

        dbHelper.close();
    }

//    static ContentValues createWeatherValues(long locationRowId) {
//        ContentValues weatherValues = new ContentValues();
//        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
//        weatherValues.put(WeatherEntry.COLUMN_DATETEXT, TEST_DATE);
//        weatherValues.put(WeatherEntry.COLUMN_DEGREES, 1.1);
//        weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
//        weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
//        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 75);
//        weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 65);
//        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
//        weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
//        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 321);
//
//        return weatherValues;
//    }

    static ContentValues createFirstTramValue() {
        // Create a new value, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(TimetableEntry.COLUMN_TRAM_ID, TEST_TRAM_ID);
        testValues.put(TimetableEntry.COLUMN_TRAM_NAME, "PoonPoon");
        testValues.put(TimetableEntry.COLUMN_TIME, TEST_TIME);

        return testValues;
    }


//    static ContentValues createNorthPoleLocationValues() {
//        // Create a new map of values, where column names are the keys
//        ContentValues testValues = new ContentValues();
//        testValues.put(LocationEntry.COLUMN_LOCATION_SETTING, TEST_LOCATION);
//        testValues.put(LocationEntry.COLUMN_CITY_NAME, "North Pole");
//        testValues.put(LocationEntry.COLUMN_COORD_LAT, 64.7488);
//        testValues.put(LocationEntry.COLUMN_COORD_LONG, -147.353);
//
//        return testValues;
//    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }
}