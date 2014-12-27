package com.nutjane.android.MUTram;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    public static final int VIEW_TYPE_TODAY = 0;
    public static final int VIEW_TYPE_FUTURE_DAY = 1;
    public static final int VIEW_TYPE_COUNT = 2;

    //flag this if we want to use seperate view for today
    private boolean mUseTodayLayout = true;
    public static final String LOG_TAG = ForecastAdapter.class.getSimpleName();

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView nameView;
        public final TextView descriptionView;
        public final TextView timeView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            nameView = (TextView) view.findViewById(R.id.list_item_tram_name);
            descriptionView = (TextView) view.findViewById(R.id.list_item_tram_desc);
            timeView = (TextView) view.findViewById(R.id.list_item_time);
        }
    }

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public void setUseTodayLayout(boolean useTodayLayout){
        mUseTodayLayout = useTodayLayout;
    }


    @Override
    public int getItemViewType(int position){
        return (position==0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount(){
        return VIEW_TYPE_COUNT;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //choose the layout type
        Log.d(LOG_TAG, "IN newView");

        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        switch (viewType){
            case VIEW_TYPE_TODAY: {
                layoutId = R.layout.list_item_forecast_today;
                break;
            }
            case VIEW_TYPE_FUTURE_DAY: {
                layoutId = R.layout.list_item_forecast;
                break;
            }
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.d(LOG_TAG, "IN bindView");

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        /*// Read weather icon ID from cursor
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_ID);
        // Use placeholder image for now
        ImageView iconView = (ImageView) view.findViewById(R.id.list_item_icon);
        iconView.setImageResource(R.drawable.ic_launcher);*/

        int viewType = getItemViewType(cursor.getPosition());

        viewHolder.iconView.setImageResource(Utility.getArtResourceForWeatherCondition(
        cursor.getInt(TimetableFragment.COL_TRAM_ID)));



        // Read date from cursor
        String nameString = cursor.getString(TimetableFragment.COL_TRAM_NAME);
        // Find TextView and set formatted date on it
        //TextView dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
        //dateView.setText(Utility.getFriendlyDayString(context, dateString));
        viewHolder.nameView.setText(nameString);

        // Read weather forecast from cursor
//        String description = cursor.getString(TimetableFragment.COL_WEATHER_DESC);
        // Find TextView and set weather forecast on it
        //TextView descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
        //descriptionView.setText(description);
        viewHolder.descriptionView.setText("TESTDESC");

        //For accessibiltt
//        viewHolder.iconView.setContentDescription(description);


        String timeString = cursor.getString(TimetableFragment.COL_TRAM_TIME);
        //TextView lowView = (TextView) view.findViewById(R.id.list_item_low_textview);
        //lowView.setText(Utility.formatTemperature(low,isMetric));
        viewHolder.timeView.setText(timeString);
    }
}