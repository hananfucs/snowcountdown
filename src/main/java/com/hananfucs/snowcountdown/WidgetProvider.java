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
    private static final long MINUTE = 60*1000;
    private static final long HOUR = MINUTE*60;
    private static final long DAY = HOUR*24;
    private static final long WEEK = DAY*7;


    private Date flightDate = new Date(117, 2, 18, 7, 45);

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

        return PendingIntent.getActivity(cn, 232, i, 0);
    }


}
