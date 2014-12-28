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
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nutjane.android.MUTram.data.TimetableProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link android.widget.ListView} layout.
 */
public class
        TimetableFragment extends Fragment implements LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = TimetableFragment.class.getSimpleName();



    private ListView mListView;

    static class ViewHolder{
         LinearLayout mtextBlueArea;
         TextView mtextBlueArrival;
         TextView mtextBlueArrivalNext;
         LinearLayout mtextRedArea;
         TextView mtextRedArrival;
         TextView mtextRedArrivalNext;
         LinearLayout mtextGreenArea;
         TextView mtextGreenArrival;
         TextView mtextGreenArrivalNext;
         TextView mtextPrefLocation;

    }



    private int mPosition = ListView.INVALID_POSITION;
    private boolean mUseTodayLayout;

    //for keep value of time
    private ArrayList<String> line1 = new ArrayList<String>();
    private ArrayList<String> line2 = new ArrayList<String>();
    private ArrayList<String> line3 = new ArrayList<String>();


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

    public static String[] timeTramCome = new String[6];



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
        inflater.inflate(R.menu.timetablefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Log.d(LOG_TAG, "IN CREATEVIEW");

        getLoaderManager().initLoader(0,null,this);


        String URL = "content://com.nutjane.android.MUTram/timetable";
        Uri uri = Uri.parse(URL);
        Cursor c = getActivity().getContentResolver().query(uri, null, null, null, null);
        if (c.moveToFirst()) {
            do{
                if(c.getString(c.getColumnIndex(TimetableProvider.TRAM_ID)).equals("1")){ // #1
                    line1.add(c.getString(c.getColumnIndex(TimetableProvider.TIME)));
                    Toast.makeText(getActivity(),"add to line1",Toast.LENGTH_LONG).show();

                }
                else if(c.getString(c.getColumnIndex(TimetableProvider.TRAM_ID)).equals("2")){ // #1
                    line2.add(c.getString(c.getColumnIndex(TimetableProvider.TIME)));
                    Toast.makeText(getActivity(),"add to line2",Toast.LENGTH_LONG).show();

                }
                else if(c.getString(c.getColumnIndex(TimetableProvider.TRAM_ID)).equals("3")){ // #1
                    line3.add(c.getString(c.getColumnIndex(TimetableProvider.TIME)));
                    Toast.makeText(getActivity(),"add to line3",Toast.LENGTH_LONG).show();

                }

//                Toast.makeText(getActivity(),
//                        c.getString(c.getColumnIndex(TimetableProvider._ID)) +
//                                ", " + c.getString(c.getColumnIndex(TimetableProvider.TRAM_ID)) +
//                                ", " + c.getString(c.getColumnIndex(TimetableProvider.TRAM_NAME)) +
//                                ", " + c.getString(c.getColumnIndex(TimetableProvider.TIME)),
//                        Toast.LENGTH_SHORT).show();
            } while (c.moveToNext());
        }
        ViewHolder holder = new ViewHolder();

        mLocation = Utility.getPreferredLocation(getActivity());
        holder.mtextPrefLocation = (TextView) rootView.findViewById(R.id.main_pref_location);
        holder.mtextPrefLocation.setText(mLocation);
        holder.mtextPrefLocation.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){ //change location
                Intent settingsActivity = new Intent(getActivity(),
                        PreferenceActivity.class);
                startActivity(settingsActivity);

            }
        });



        calculation(1,line1,0);
        calculation(2,line2,2);
        calculation(3,line3,4);

        holder.mtextBlueArrival = (TextView) rootView.findViewById(R.id.blueLine_arrival);
        holder.mtextBlueArrival.setText(timeTramCome[0]);
        holder.mtextBlueArrivalNext = (TextView) rootView.findViewById(R.id.blueLine_arrival_next);
        holder.mtextBlueArrivalNext.setText(timeTramCome[1]);

        holder.mtextBlueArea = (LinearLayout) rootView.findViewById(R.id.blueLine_area);
        holder.mtextBlueArea.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Toast.makeText(getActivity(),"CLICK",Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, "IN CLICK");

                Intent detail = new Intent(getActivity(),DetailsActivity.class);
                detail.putExtra("TRAM_ID","1");
                detail.putExtra("timeTramCome",timeTramCome[0]);
                detail.putExtra("timeTramCome_next",timeTramCome[1]);
                startActivity(detail);

            }
         });

        holder.mtextRedArrival = (TextView) rootView.findViewById(R.id.redLine_arrival);
        holder.mtextRedArrival.setText(timeTramCome[2]);
        holder.mtextRedArrivalNext = (TextView) rootView.findViewById(R.id.redLine_arrival_next);
        holder.mtextRedArrivalNext.setText(timeTramCome[3]);

        holder.mtextRedArea = (LinearLayout) rootView.findViewById(R.id.redLine_area);
        holder.mtextRedArea.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Toast.makeText(getActivity(),"CLICK",Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, "IN CLICK");

                Intent detail = new Intent(getActivity(),DetailsActivity.class);
                detail.putExtra("TRAM_ID","2");
                detail.putExtra("timeTramCome",timeTramCome[2]);
                detail.putExtra("timeTramCome_next",timeTramCome[3]);
                startActivity(detail);

            }
        });

        holder.mtextGreenArrival = (TextView) rootView.findViewById(R.id.greenLine_arrival);
        holder.mtextGreenArrival.setText(timeTramCome[4]);
        holder.mtextGreenArrivalNext = (TextView) rootView.findViewById(R.id.greenLine_arrival_next);
        holder.mtextGreenArrivalNext.setText(timeTramCome[5]);

        holder.mtextGreenArea = (LinearLayout) rootView.findViewById(R.id.greenLine_area);
        holder.mtextGreenArea.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Toast.makeText(getActivity(),"CLICK",Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, "IN CLICK");

                Intent detail = new Intent(getActivity(),DetailsActivity.class);
                detail.putExtra("TRAM_ID","3");
                detail.putExtra("timeTramCome",timeTramCome[4]);
                detail.putExtra("timeTramCome_next",timeTramCome[5]);
                startActivity(detail);

            }
        });



        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(TIMETABLE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void changeLocation(){

        ViewHolder holder = new ViewHolder();
        mLocation = Utility.getPreferredLocation(getActivity());
        holder.mtextPrefLocation = (TextView) getView().findViewById(R.id.main_pref_location);
        holder.mtextPrefLocation.setText(mLocation);
        calculation(1,line1,0);
        calculation(2,line2,2);
        calculation(3,line3,4);
        holder.mtextBlueArrival = (TextView) getView().findViewById(R.id.blueLine_arrival);
        holder.mtextBlueArrival.setText(timeTramCome[0]);
        holder.mtextBlueArrivalNext = (TextView) getView().findViewById(R.id.blueLine_arrival_next);
        holder.mtextBlueArrivalNext.setText(timeTramCome[1]);
        holder.mtextRedArrival = (TextView) getView().findViewById(R.id.redLine_arrival);
        holder.mtextRedArrival.setText(timeTramCome[2]);
        holder.mtextRedArrivalNext = (TextView) getView().findViewById(R.id.redLine_arrival_next);
        holder.mtextRedArrivalNext.setText(timeTramCome[3]);
        holder.mtextGreenArrival = (TextView) getView().findViewById(R.id.greenLine_arrival);
        holder.mtextGreenArrival.setText(timeTramCome[4]);
        holder.mtextGreenArrivalNext = (TextView) getView().findViewById(R.id.greenLine_arrival_next);
        holder.mtextGreenArrivalNext.setText(timeTramCome[5]);


    }

    private void calculation(int tramID, ArrayList<String> line, int st){


        int addLine = Integer.parseInt(Utility.getPreferredAddtimeTram(getActivity(),tramID));
        if(addLine == -1){ //if tram doesn't pass that location
            timeTramCome[st] = "-- : --";
            timeTramCome[st+1] = "-- : --";
            Toast.makeText(getActivity(),getResources().getText(R.string.no_tram_pass)
                    ,Toast.LENGTH_SHORT).show();
            return;

        }

        //nowTime
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        Calendar calNow = Calendar.getInstance();
        Calendar calTramCome = Calendar.getInstance();


        Log.d(LOG_TAG, "TRAMID: "+tramID+" ADD+" + addLine+ "  NOW:"+df.format(calNow.getTime()));

        int position=-1;
        int amountLoop = line.size();
        for(int i=0;i<amountLoop;i++) {

            String get = line.get(i);
            Log.d(LOG_TAG, tramID + " " +get);
            String[] time = line.get(i).split(":");
            Log.d(LOG_TAG, time[0] + " " +time[1]);

            calTramCome.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
            calTramCome.set(Calendar.MINUTE,Integer.parseInt(time[1]));
            Log.d(LOG_TAG, "TIME OUT>: "+df.format(calTramCome.getTime()));

//                System.out.println("T "+i+" "+calTramCome.getTime());
            calTramCome.add(Calendar.MINUTE, addLine);
            Log.d(LOG_TAG, "TIME ARRIVE>: "+df.format(calTramCome.getTime()));

//                System.out.println("Ta"+i+" "+calTramCome.getTime());
            if (calTramCome.after(calNow)) {
                position = i;
                break;
            }
        }

        Log.d(LOG_TAG, "position : " +position);

        if(position==-1){ //no tram found for this day
            timeTramCome[st] = "-- : --";
        }
        else {
            timeTramCome[st] = (df.format(calTramCome.getTime()));
        }
        Log.d(LOG_TAG, st + ": " +timeTramCome[st]);

        //calculation for the next tarm
        if(position+1 == line.size() || position == -1){ //if it is out of bound or no tram
            timeTramCome[st+1] = "-- : --";

        }
        else {
            String[] time = line.get(position+1).split(":");
            calTramCome.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
            calTramCome.set(Calendar.MINUTE,Integer.parseInt(time[1]));
            calTramCome.add(Calendar.MINUTE, addLine);
            timeTramCome[st+1]=(df.format(calTramCome.getTime()));
        }
        Log.d(LOG_TAG, (st+1) + ": " +timeTramCome[st+1]);




        Log.d(LOG_TAG, " ");


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
            changeLocation();
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
//        mForecastAdapter.swapCursor(null);
    }


}