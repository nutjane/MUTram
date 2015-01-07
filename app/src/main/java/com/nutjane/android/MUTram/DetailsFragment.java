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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class DetailsFragment extends Fragment  {

    private static final String LOG_TAG = DetailsFragment.class.getSimpleName();

    private static final String TIMETABLE_SHARE_HASHTAG = " #MUTramApp";

    public static final String TRAMID_KEY = "TRAM_ID";
    public static final String TIME_TRAM_COME = "timeTramCome";
    public static final String TIME_TRAM_COME_NEXT = "timeTramCome_next";

    private ShareActionProvider mShareActionProvider;
    private String mShareValue;

    private String mTramID_Value;
    private String mTimeCome_Value;
    private String mTimeCome_next_Value;
    private String mLocaiton_Value;

    private static final int DETAIL_LOADER = 0;

    private TextView mTramDesc;
    private TextView mLocation;
    private TextView mTramName;
    private TextView mTramTime;
    private TextView mTramTime_next;
    private LinearLayout mTramHeader;


    public DetailsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(TRAMID_KEY, mTramID_Value);
        outState.putString(TIME_TRAM_COME, mTimeCome_Value);
        outState.putString(TIME_TRAM_COME_NEXT, mTimeCome_next_Value);

        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mTramID_Value = arguments.getString(DetailsActivity.TRAMID_KEY);
            mTimeCome_Value = arguments.getString(DetailsActivity.TIME_TRAM_COME);
            mTimeCome_next_Value = arguments.getString(DetailsActivity.TIME_TRAM_COME_NEXT);

        }
        if (savedInstanceState != null) {
            mTramID_Value = savedInstanceState.getString(TRAMID_KEY);
            mTimeCome_Value = savedInstanceState.getString(TIME_TRAM_COME);
            mTimeCome_next_Value = savedInstanceState.getString(TIME_TRAM_COME_NEXT);

        }
        mLocaiton_Value = Utility.getPreferredLocation(getActivity());

        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        mTramName = (TextView) rootView.findViewById(R.id.detail_tram_name);
        mTramDesc = (TextView) rootView.findViewById(R.id.detail_tram_desc);
        mLocation = (TextView) rootView.findViewById(R.id.detail_location);
        mTramHeader = (LinearLayout) rootView.findViewById(R.id.detail_tram_header);

        mTramTime = (TextView) rootView.findViewById(R.id.detail_amount_min);
        mTramTime_next = (TextView) rootView.findViewById(R.id.detail_amount_min_next);

        String tramDesc="";
        String tramName="";
        //change detail header color
        switch (Integer.parseInt(mTramID_Value)){
            case 1: {
                mTramHeader.setBackgroundColor(getResources().getColor(R.color.tram_blue));
                tramName = getResources().getString(R.string.blue_line_name);
                tramDesc = getResources().getString(R.string.blue_line_des);
            } break;
            case 2: {
                mTramHeader.setBackgroundColor(getResources().getColor(R.color.tram_red));
                tramName = getResources().getString(R.string.red_line_name);
                tramDesc = getResources().getString(R.string.red_line_des);
            } break;
            case 3: {
                mTramHeader.setBackgroundColor(getResources().getColor(R.color.tram_green));
                tramName = getResources().getString(R.string.green_line_name);
                tramDesc = getResources().getString(R.string.green_line_des);
            } break;
        }

        mTramName.setText(tramName);
        mTramDesc.setText(tramDesc);
        mLocation.setText(mLocaiton_Value);
        mTramTime.setText(mTimeCome_Value);
        mTramTime_next.setText(mTimeCome_next_Value);
        setShareValue();

        //notify no tram
        if(mTimeCome_Value.equals("-- : --")) Toast.makeText(getActivity()
                , getResources().getText(R.string.share_no_tram)
                , Toast.LENGTH_SHORT).show();
        if(mTimeCome_Value.equals("X")) Toast.makeText(getActivity()
                , getResources().getText(R.string.this_route_no_tram)
                , Toast.LENGTH_SHORT).show();



        return rootView;
    }

    private void setShareValue(){
        if(mTimeCome_Value.equals("-- : --")){
            mShareValue = getResources().getString(R.string.share_no_tram);
        }
        else{
            String tramName = "";
            switch (Integer.parseInt(mTramID_Value)){
                case 1: tramName = getResources().getString(R.string.blue_line_name); break;
                case 2: tramName = getResources().getString(R.string.red_line_name); break;
                case 3: tramName = getResources().getString(R.string.green_line_name); break;
            }

            mShareValue = String.format(getResources().getString(R.string.share_value)
                    ,tramName,mLocaiton_Value,mTimeCome_Value);
        }


    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mShareValue != null) {
            mShareActionProvider.setShareIntent(createShareTimetableIntent());
        }
    }

    //for sharing
    private Intent createShareTimetableIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mShareValue + TIMETABLE_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mTramID_Value = savedInstanceState.getString(TRAMID_KEY);
            mTimeCome_Value = savedInstanceState.getString(TIME_TRAM_COME);
            mTimeCome_next_Value = savedInstanceState.getString(TIME_TRAM_COME_NEXT);

        }

    }



}