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

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nutjane.android.MUTram.data.TimetableProvider;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link android.widget.ListView} layout.
 */
public class
        TimetableFragment extends Fragment implements LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = TimetableFragment.class.getSimpleName();


    private ForecastAdapter mForecastAdapter;

    private ListView mListView;
    private TextView mtextWelcome;
    private int mPosition = ListView.INVALID_POSITION;
    private boolean mUseTodayLayout;

    private static final String SELECTED_KEY = "selected_position";

    private static final int TIMETABLE_LOADER = 0;
    private String mLocation;
    Context context;

    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] TIMETABLE_COLUMN = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            "tram_id","tram_name","time"
    };

    public static final int[] mToFields = { R.id.list_item_tram_name,
            R.id.list_item_tram_desc,
            R.id.list_item_time};



    // These indices are tied to TIMETABLE_COLUMN.  If TIMETABLE_COLUMN changes, these
    // must change.
    public static final int COL_TRAM_IDtable = 0;
    public static final int COL_TRAM_ID = 1;
    public static final int COL_TRAM_NAME = 2;
    public static final int COL_TRAM_TIME = 3;


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(String date);
    }


    public TimetableFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        /*if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }*/
        /*if (id == R.id.action_map) {
            openPreferredLocationInMap();
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Log.d(LOG_TAG, "IN CREATEVIEW");

        mtextWelcome = (TextView) rootView.findViewById(R.id.main_welcome);
        String a = "WELCOME";
        mtextWelcome.setText(a);
        Log.d(LOG_TAG, "WELCOME");




        getLoaderManager().initLoader(0,null,this);


        String URL = "content://com.nutjane.android.MUTram/timetable";
        Uri uri = Uri.parse(URL);
        Cursor c = getActivity().getContentResolver().query(uri,null,null,null,null);
        if (c.moveToFirst()) {
            do{
                Toast.makeText(getActivity(),
                        c.getString(c.getColumnIndex(TimetableProvider._ID)) +
                                ", " + c.getString(c.getColumnIndex(TimetableProvider.TRAM_ID)) +
                                ", " + c.getString(c.getColumnIndex(TimetableProvider.TRAM_NAME)) +
                                ", " + c.getString(c.getColumnIndex(TimetableProvider.TIME)),
                        Toast.LENGTH_SHORT).show();
            } while (c.moveToNext());
        }
//        ListView mListview = (ListView)rootView.findViewById(R.id.listview_forecast);
//
//        SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(context,
//                R.layout.list_item_forecast,
//                null,
//                TIMETABLE_COLUMN,
//                mToFields,
//                0);
//        mListview.setAdapter(mAdapter);




//        Cursor cursor = context.getApplicationContext().getContentResolver().query(
//                TimetableContract.TimetableEntry.CONTENT_URI,
//                null,
//                null,
//                null,
//                null
//        );
//        Log.d(LOG_TAG, "CURSOR ADDED");
//
//        String test = cursor.getString(TimetableFragment.COL_TRAM_NAME);
//        Log.d(LOG_TAG, test);




//        //The arrayAdapter will take data from a source and
//        //use it to populate the ListView it's attached to.
//        mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);

        /*// The SimpleCursorAdapter will take data from the database through the
        // Loader and use it to populate the ListView it's attached to.
        mForecastAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.list_item_forecast,
                null,
                // the column names to use to fill the textviews
                new String[]{WeatherEntry.COLUMN_DATETEXT,
                        WeatherEntry.COLUMN_SHORT_DESC,
                        WeatherEntry.COLUMN_MAX_TEMP,
                        WeatherEntry.COLUMN_MIN_TEMP
                },
                // the textviews to fill with the data pulled from the columns above
                new int[]{R.id.list_item_date_textview,
                        R.id.list_item_forecast_textview,
                        R.id.list_item_high_textview,
                        R.id.list_item_low_textview
                },
                0
        );

        mForecastAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                boolean isMetric = Utility.isMetric(getActivity());
                switch (columnIndex) {
                    case COL_WEATHER_MAX_TEMP:
                    case COL_WEATHER_MIN_TEMP: {
                        // we have to do some formatting and possibly a conversion
                        ((TextView) view).setText(Utility.formatTemperature(
                                cursor.getDouble(columnIndex), isMetric));
                        return true;
                    }
                    case COL_WEATHER_DATE: {
                        String dateString = cursor.getString(columnIndex);
                        TextView dateView = (TextView) view;
                        dateView.setText(Utility.formatDate(dateString));
                        return true;
                    }
                }
                return false;
            }
        });
        */


//        // Get a reference to the ListView, and attach this adapter to it.
//        mListView = (ListView) rootView.findViewById(R.id.listview_forecast);
//        mListView.setAdapter(mForecastAdapter);
//
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                Cursor cursor = mForecastAdapter.getCursor();
//                if (cursor != null && cursor.moveToPosition(position)) {
//                    ((Callback)getActivity())
//                            .onItemSelected(cursor.getString(COL_TRAM_ID));
//                }
//                mPosition = position;
//            }
//        });
//
//        // If there's instance state, mine it for useful information.
//        // The end-goal here is that the user never knows that turning their device sideways
//        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
//        // or magically appeared to take advantage of room, but data or place in the app was never
//        // actually *lost*.
//        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
//            // The listview probably hasn't even been populated yet.  Actually perform the
//            // swapout in onLoadFinished.
//            mPosition = savedInstanceState.getInt(SELECTED_KEY);
//        }
//
//        mForecastAdapter.setUseTodayLayout(mUseTodayLayout);
//


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(TIMETABLE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void updateWeather() {
        //normal
//        String location = Utility.getPreferredLocation(getActivity());
//        new FetchTimetableTask(getActivity()).execute(location);

        /// use service
        /*Intent intent = new Intent(getActivity(), SunshineService.class);
        intent.putExtra(SunshineService.LOCATION_QUERY_EXTRA,
                Utility.getPreferredLocation(getActivity()));
        getActivity().startService(intent);*/

        //use receiver
        /*Intent alarmIntent = new Intent(getActivity(), SunshineService.AlarmReceiver.class);
        alarmIntent.putExtra(SunshineService.LOCATION_QUERY_EXTRA, Utility.getPreferredLocation(getActivity()));

        //Wrap in a pending intent which only fires once.
        PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0,alarmIntent,PendingIntent.FLAG_ONE_SHOT);//getBroadcast(context, 0, i, 0);

        AlarmManager am=(AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);

        //Set the AlarmManager to wake up the system.
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pi);*/

        //use Sync Adapter
        /*String location = Utility.getPreferredLocation(getActivity());
        new FetchWeatherTask(getActivity()).execute(location);*/

        //use complete syncAdapter
        //SunshineSyncAdapter.syncImmediately(getActivity());

    }

//    private void openPreferredLocationInMap() {
//        // Using the URI scheme for showing a location found on a map.  This super-handy
//        // intent can is detailed in the "Common Intents" page of Android's developer site:
//        // http://developer.android.com/guide/components/intents-common.html#Maps
//        if ( null != mForecastAdapter ) {
//            Cursor c = mForecastAdapter.getCursor();
//            if ( null != c ) {
//                c.moveToPosition(0);
//                String posLat = c.getString(COL_COORD_LAT);
//                String posLong = c.getString(COL_COORD_LONG);
//                Uri geoLocation = Uri.parse("geo:" + posLat + "," + posLong);
//
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(geoLocation);
//
//                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
//                    startActivity(intent);
//                } else {
//                    Log.d(LOG_TAG, "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
//                }
//            }
//
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLocation != null && !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
            getLoaderManager().restartLoader(TIMETABLE_LOADER, null, this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "CURSOR");

        String URL = "content://com.nutjane.android.MUTram/timetable";
        Uri uri = Uri.parse(URL);
        return new CursorLoader(getActivity(),uri,null,null,null,null);


        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, get the String representation for today,
        // and filter the query to return weather only for dates after or including today.
        // Only return data after today.
//        String startDate = TimetableContract.getDbDateString(new Date());

        // Sort order:  Ascending, by date.
//        String sortOrder = WeatherEntry.COLUMN_DATETEXT + " ASC";
//
//        mLocation = Utility.getPreferredLocation(getActivity());
//       // Uri timetableForTramUri = TimetableEntry.buildTimeUriWithTramID(mLocation);
//
//        Uri timetableForTramUri = TimetableEntry.buildTimeUriWithTramID("1");
//
//         Log.d(LOG_TAG, "IN LOADER with"+ timetableForTramUri.t);
//
//        // Now create and return a CursorLoader that will take care of
//        // creating a Cursor for the data being displayed.
//        return new CursorLoader(
//                getActivity(),
//                timetableForTramUri,
//                TIMETABLE_COLUMN,
//                null,
//                null,
//                null
//        );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        mForecastAdapter.swapCursor(data);
//        if (mPosition != ListView.INVALID_POSITION) {
//            // If we don't need to restart the loader, and there's a desired position to restore
//            // to, do so now.
//            mListView.smoothScrollToPosition(mPosition);
//        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }


}