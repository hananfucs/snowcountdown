package com.hananfucs.snowcountdown;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by Hanan on 19/01/17.
 */

public class WidgetProvider extends AppWidgetProvider {
    public static final long SECOND = 1000;
    public static final long MINUTE = SECOND*60;
    public static final long HOUR = MINUTE*60;
    public static final long DAY = HOUR*24;
    public static final long WEEK = DAY*7;


    public static Date flightDate = new Date(117, 2, 18, 7, 45);

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        long flightTimeMillis = flightDate.getTime();
        long timeUntilMillis = flightTimeMillis - System.currentTimeMillis();
        long tempNum;

        int weeksLeft = (int) (timeUntilMillis / WEEK);
        tempNum = timeUntilMillis % WEEK;
        int daysLeft = (int) (tempNum / DAY);
        tempNum = tempNum % DAY;
        int hoursLeft = (int) (tempNum / HOUR);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout);
        remoteViews.setTextViewText(R.id.weeksText, weeksLeft + "W");
        remoteViews.setTextViewText(R.id.daysText, daysLeft + "D");
        remoteViews.setTextViewText(R.id.hoursText, hoursLeft + "H");

        for (int i = 0; i < appWidgetIds.length; i++) {
            Intent intent = new Intent(context, WidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            remoteViews.setOnClickPendingIntent(R.id.icon, getPendingIntent(context));
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }
    }

    private PendingIntent getPendingIntent(Context cn) {
        String url = "http://www.snow-forecast.com/resorts/Mayrhofen/6day/mid";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));

        Intent intent = new Intent(cn, MainActivity.class);


        return PendingIntent.getActivity(cn, 232, intent, 0);
    }


}
