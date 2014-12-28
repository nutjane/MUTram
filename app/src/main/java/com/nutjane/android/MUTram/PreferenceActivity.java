package com.nutjane.android.MUTram;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class PreferenceActivity extends android.preference.PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
        PreferenceManager.setDefaultValues(PreferenceActivity.this, R.xml.pref_general, false);

    }

    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);



        }

    }
}