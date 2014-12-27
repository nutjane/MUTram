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

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nutjane.android.MUTram.data.TimetableContract.TimetableEntry;


/**
* A placeholder fragment containing a simple view.
*/
public class DetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailsFragment.class.getSimpleName();

    private static final String TIMETABLE_SHARE_HASHTAG = " #MUTramApp";

    private static final String TRAMID_KEY = "location";
//    private static final String LOCATION_KEY = "location";

    private ShareActionProvider mShareActionProvider;
    private String mTramID;
    private String mTimetable;
    private String mTimeStr;
//    private String mLocation;
//    private String mForecast;
//    private String mDateStr;


    private static final int DETAIL_LOADER = 0;

    private static final String[] TIMETABLE_COLUMN = {
            TimetableEntry.TABLE_NAME + "." + TimetableEntry._ID,
            TimetableEntry.COLUMN_TRAM_ID,
            TimetableEntry.COLUMN_TRAM_NAME,
            TimetableEntry.COLUMN_TIME
    };


    private TextView mFriendlyDateView;
    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;

    private ImageView mIconView;
    private TextView mTramRoute;
    private TextView mTramName;
    private TextView mTramTime;

    public DetailsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(TRAMID_KEY, mTramID);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mTimeStr = arguments.getString(DetailsActivity.TRAMID_KEY);
        }

        if (savedInstanceState != null) {
            mTramID = savedInstanceState.getString(TRAMID_KEY);
        }


        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        mTramName = (TextView) rootView.findViewById(R.id.detail_tram_bigname);
        mTramRoute = (TextView) rootView.findViewById(R.id.detail_tram_route);
        mTramTime = (TextView) rootView.findViewById(R.id.detail_amount_min);
        mIconView = (ImageView) rootView.findViewById(R.id.detail_icon);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(DetailsActivity.TRAMID_KEY) &&
                mTramID != null &&
                !mTramID.equals(Utility.getPreferredLocation(getActivity()))) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.v(LOG_TAG, "in onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mTimetable != null) {
            mShareActionProvider.setShareIntent(createShareTimetableIntent());
        }
    }

    //for sharing
    private Intent createShareTimetableIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mTimetable + TIMETABLE_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mTramID = savedInstanceState.getString(TRAMID_KEY);
        }

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(DetailsActivity.TRAMID_KEY)) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        /*Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null || !intent.hasExtra(DetailsActivity.TRAMID_KEY)) {
            return null;
        }
        String forecastDate = intent.getStringExtra(DetailsActivity.TRAMID_KEY);*/

        // Sort order:  Ascending, by date.
        String sortOrder = TimetableEntry.COLUMN_TIME + " ASC";

        mTramID = Utility.getPreferredLocation(getActivity());
        Uri timetableForTimeUri = TimetableEntry.buildTimeUriWithTramID(mTramID);
        Log.v(LOG_TAG, timetableForTimeUri.toString());

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                timetableForTimeUri,
                TIMETABLE_COLUMN,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {


            // Read weather condition ID from cursor
            int tramID = data.getInt(data.getColumnIndex(TimetableEntry.COLUMN_TRAM_ID));
            // Use ART(COLOR) Image
            mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(tramID));


            // Read tram name from cursor and update view
            String tramName = data.getString(data.getColumnIndex(
                    TimetableEntry.COLUMN_TRAM_NAME));
            mDescriptionView.setText(tramName);

            //For accessibility, add a content description to the icon field
            mIconView.setContentDescription(tramName);



            String tramTime = data.getString(data.getColumnIndex(TimetableEntry.COLUMN_TIME));
            mTramTime.setText(tramTime);

//            // Read low temperature from cursor and update view
//            double low = data.getDouble(data.getColumnIndex(WeatherEntry.COLUMN_MIN_TEMP));
//            String lowString = Utility.formatTemperature(getActivity(), low);
//            mLowTempView.setText(lowString);
//
//            // Read humidity from cursor and update view
//            float humidity = data.getFloat(data.getColumnIndex(WeatherEntry.COLUMN_HUMIDITY));
//            mHumidityView.setText(getActivity().getString(R.string.format_humidity, humidity));
//
//            // Read wind speed and direction from cursor and update view
//            float windSpeedStr = data.getFloat(data.getColumnIndex(WeatherEntry.COLUMN_WIND_SPEED));
//            float windDirStr = data.getFloat(data.getColumnIndex(WeatherEntry.COLUMN_DEGREES));
//            mWindView.setText(Utility.getFormattedWind(getActivity(), windSpeedStr, windDirStr));
//
//            // Read pressure from cursor and update view
//            float pressure = data.getFloat(data.getColumnIndex(WeatherEntry.COLUMN_PRESSURE));
//            mPressureView.setText(getActivity().getString(R.string.format_pressure, pressure));

            // We still need this for the share intent
            mTimetable = String.format("%s - %s", tramName, tramTime);

            Log.v(LOG_TAG, "Forecast String: " + mTimetable);



            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareTimetableIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}