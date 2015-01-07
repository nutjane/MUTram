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

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class DetailsActivity extends ActionBarActivity {

    public static final String TRAMID_KEY = "TRAM_ID";
    public static final String TIME_TRAM_COME = "timeTramCome";
    public static final String TIME_TRAM_COME_NEXT = "timeTramCome_next";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            String tramID = getIntent().getStringExtra(TRAMID_KEY);
            String timeCome = getIntent().getStringExtra(TIME_TRAM_COME);
            String timeCome_next = getIntent().getStringExtra(TIME_TRAM_COME_NEXT);

            Bundle arguments = new Bundle();
            arguments.putString(DetailsActivity.TRAMID_KEY, tramID);
            arguments.putString(DetailsActivity.TIME_TRAM_COME, timeCome);
            arguments.putString(DetailsActivity.TIME_TRAM_COME_NEXT, timeCome_next);

            DetailsFragment fragment = new DetailsFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, fragment)
                    .commit();
        }
    }



}