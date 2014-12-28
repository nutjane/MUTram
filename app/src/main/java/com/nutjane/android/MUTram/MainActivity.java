package com.nutjane.android.MUTram;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.nutjane.android.MUTram.data.TimetableDbHelper;


public class MainActivity extends ActionBarActivity implements TimetableFragment.Callback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private boolean mTwoPane;

    SQLiteDatabase mDb;
    TimetableDbHelper mHelper;
    Cursor mCursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if (findViewById(R.id.weather_detail_container) != null) {
//            // The detail container view will be present only in the large-screen layouts
//            // (res/layout-sw600dp). If this view is present, then the activity should be
//            // in two-pane mode.
//            mTwoPane = true;
//
//            // In two-pane mode, show the detail view in this activity by
//            // adding or replacing the detail fragment using a
//            // fragment transaction.
//            if (savedInstanceState == null) {
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.weather_detail_container, new DetailsFragment())
//                        .commit();
//            }
//        } else {
//            mTwoPane = false;
//        }

//        //setContentView(R.layout.main);
//
//        ListView listView1 = (ListView)findViewById(R.id.listview_forecast);
//
//        mHelper = new TimetableDbHelper(this);
//        mDb = mHelper.getWritableDatabase();
//        mCursor = mDb.rawQuery("SELECT " + TimetableContract.TimetableEntry.COLUMN_TRAM_ID + ", "
//                + TimetableContract.TimetableEntry.COLUMN_TRAM_NAME + ", "
//                + TimetableContract.TimetableEntry.COLUMN_TIME
//                + " FROM " + TimetableContract.TimetableEntry.TABLE_NAME, null);
//
//        ArrayList<String> dirArray = new ArrayList<String>();
//        mCursor.moveToFirst();
//
//        while ( !mCursor.isAfterLast() ){
//            dirArray.add(mCursor.getString(mCursor.getColumnIndex(TimetableContract.TimetableEntry.COLUMN_TRAM_ID)) + "\n"
//                    + "name : " + mCursor.getString(mCursor.getColumnIndex(TimetableContract.TimetableEntry.COLUMN_TRAM_NAME)) + "\t\t"
//                    + "time : " + mCursor.getString(mCursor.getColumnIndex(TimetableContract.TimetableEntry.COLUMN_TIME)));
//            mCursor.moveToNext();
//        }
//
//        ArrayAdapter<String> adapterDir = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dirArray);
//        listView1.setAdapter(adapterDir);



//        TimetableFragment timetableFragment = ((TimetableFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.fragment_forecast));
//        timetableFragment.setUseTodayLayout(!mTwoPane);



    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
        // The activity is about to become visible.
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "Resume");

        // The activity has become visible (it is now "resumed").
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "pause");

        // Another activity is taking focus (this activity is about to be "paused").
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "stop");

        // The activity is no longer visible (it is now "stopped")
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "destroy");

        // The activity is about to be destroyed.
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, PreferenceActivity.class));
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    /**
     * A placeholder fragment containing a simple view.
     */

    @Override
    public void onItemSelected(String date) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putString(DetailsActivity.TRAMID_KEY, date);

            DetailsFragment fragment = new DetailsFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailsActivity.class)
                    .putExtra(DetailsActivity.TRAMID_KEY, date);
            startActivity(intent);
        }
    }


}
