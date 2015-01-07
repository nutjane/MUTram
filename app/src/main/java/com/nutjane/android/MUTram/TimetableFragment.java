
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nutjane.android.MUTram.data.TimetableProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class
        TimetableFragment extends Fragment implements LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = TimetableFragment.class.getSimpleName();

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



    private boolean hasTram = false;

    //for keep value of time
    private ArrayList<String> line1 = new ArrayList<String>();
    private ArrayList<String> line2 = new ArrayList<String>();
    private ArrayList<String> line3 = new ArrayList<String>();



    private static final int TIMETABLE_LOADER = 0;
    private String mLocation;
    Context context;

    public static String[] timeTramCome = new String[6];


    public TimetableFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        ViewHolder holder = new ViewHolder();

        holder.mtextPrefLocation = (TextView) rootView.findViewById(R.id.main_pref_location);
        holder.mtextPrefLocation.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){ //click to change location
                Intent settingsActivity = new Intent(getActivity(),
                        PreferenceActivity.class);
                startActivity(settingsActivity);

            }
        });

        holder.mtextRedArea = (LinearLayout) rootView.findViewById(R.id.redLine_area);
        holder.mtextRedArea.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent detail = new Intent(getActivity(), DetailsActivity.class);
                detail.putExtra("TRAM_ID", "2");
                detail.putExtra("timeTramCome", timeTramCome[2]);
                detail.putExtra("timeTramCome_next", timeTramCome[3]);
                startActivity(detail);

            }
        });

        holder.mtextBlueArea = (LinearLayout) rootView.findViewById(R.id.blueLine_area);
        holder.mtextBlueArea.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent detail = new Intent(getActivity(), DetailsActivity.class);
                detail.putExtra("TRAM_ID", "1");
                detail.putExtra("timeTramCome", timeTramCome[0]);
                detail.putExtra("timeTramCome_next", timeTramCome[1]);
                startActivity(detail);

            }
        });


        holder.mtextGreenArea = (LinearLayout) rootView.findViewById(R.id.greenLine_area);
        holder.mtextGreenArea.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent detail = new Intent(getActivity(), DetailsActivity.class);
                detail.putExtra("TRAM_ID", "3");
                detail.putExtra("timeTramCome", timeTramCome[4]);
                detail.putExtra("timeTramCome_next", timeTramCome[5]);
                startActivity(detail);

            }
        });




        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(0, null, this);
        super.onActivityCreated(savedInstanceState);

    }

    private void checkTramAvailable(){
        //notify if there' no tram at this time
        if(!hasTram) Toast.makeText(getActivity()
                , getResources().getText(R.string.share_no_tram)
                , Toast.LENGTH_SHORT).show();
    }


    private void calculation(int tramID, ArrayList<String> line, int st){

        int addLine = Integer.parseInt(Utility.getPreferredAddtimeTram(getActivity(),tramID));
        if(addLine == -1){ //if tram doesn't pass that location
            timeTramCome[st] = "X";
            timeTramCome[st+1] = "X";
            return;

        }

        //nowTime
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        Calendar calNow = Calendar.getInstance();
        Calendar calTramCome = Calendar.getInstance();

        int position=-1;
        int amountLoop = line.size();
        for(int i=0;i<amountLoop;i++) {

            String get = line.get(i);
            String[] time = line.get(i).split(":");

            calTramCome.set(Calendar.HOUR_OF_DAY,Integer.parseInt(time[0]));
            calTramCome.set(Calendar.MINUTE,Integer.parseInt(time[1]));

            calTramCome.add(Calendar.MINUTE, addLine);

            if (calTramCome.after(calNow)) {
                position = i;
                break;
            }
        }

        if(position==-1){ //no tram found for this day
            timeTramCome[st] = "-- : --";
        }
        else {
            timeTramCome[st] = (df.format(calTramCome.getTime()));
            hasTram = true;
        }
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

    }


    @Override
    public void onResume() {
        super.onResume();
        if (mLocation != null && !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
            getLoaderManager().restartLoader(TIMETABLE_LOADER, null, this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String URL = TimetableProvider.getURLProvider();
        Uri uri = Uri.parse(URL);
        return new CursorLoader(getActivity(),uri,null,null,null,null);


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data.moveToFirst()) {
            do{
                if(data.getString(data.getColumnIndex(TimetableProvider.TRAM_ID)).equals("1")){ // #1
                    line1.add(data.getString(data.getColumnIndex(TimetableProvider.TIME)));

                }
                else if(data.getString(data.getColumnIndex(TimetableProvider.TRAM_ID)).equals("2")){ // #1
                    line2.add(data.getString(data.getColumnIndex(TimetableProvider.TIME)));

                }
                else if(data.getString(data.getColumnIndex(TimetableProvider.TRAM_ID)).equals("3")){ // #1
                    line3.add(data.getString(data.getColumnIndex(TimetableProvider.TIME)));

                }


            } while (data.moveToNext());
        }

        calculation(1,line1,0);
        calculation(2,line2,2);
        calculation(3,line3,4);

        ViewHolder holder = new ViewHolder();

        mLocation = Utility.getPreferredLocation(getActivity());
        holder.mtextPrefLocation = (TextView) getView().findViewById(R.id.main_pref_location);
        holder.mtextPrefLocation.setText(mLocation);

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

        checkTramAvailable();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


}