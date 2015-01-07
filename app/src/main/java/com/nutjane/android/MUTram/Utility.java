
package com.nutjane.android.MUTram;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utility {
    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String pref = prefs.getString(context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default));
        String[] str = pref.split(",");
        return str[0];
    }


    public static String getPreferredAddtimeTram(Context context, int tram_id) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String pref = prefs.getString(context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default));
        String[] str = pref.split(",");
        switch (tram_id){
            case 1: return str[1];
            case 2: return str[2];
            case 3: return str[3];
            default: return "0";


        }
    }


}