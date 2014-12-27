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

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database.
 */
public class TimetableContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.nutjane.android.mutram";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_TIMETABLE = "timetable";

    /* Inner class that defines the table contents of the timetable */
    public static final class TimetableEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TIMETABLE).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_TIMETABLE;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_TIMETABLE;

        public static final String TABLE_NAME = "timetable";

        //no FK

        // tram ID
        public static final String COLUMN_TRAM_ID = "tram_id";
        // tram name
        public static final String COLUMN_TRAM_NAME = "tram_name";

        // time out- time that tram starts running from the terminal
        public static final String COLUMN_TIME = "min";


        public static Uri buildTimeUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    /*  public static Uri buildTimeUriWithTime(String thatTime) {
            return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_TIME, thatTime).build();
        }
    */
        public static Uri buildTimeUriWithTramID(String tramID) {
            return CONTENT_URI.buildUpon().appendPath(tramID).build();
        }

        /*public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getDateFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }*/

        public static String getTramIDFromUri(Uri uri) {
            return uri.getQueryParameter(COLUMN_TRAM_ID);
        }
    }
}