package com.hananfucs.snowcountdown;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;
    private LinearLayout mImageDescription;
    private TextView mCountDownTextView;
    private Handler countdownHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String message = (String) msg.obj;
            mCountDownTextView.setText(message);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageDescription = (LinearLayout)findViewById(R.id.imageDescriptionLayout);
        mImageDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.facebook.com/mayrhofen.hippach.zillertal/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        TextView weatherLink = (TextView) findViewById(R.id.weatherLink);
        weatherLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://www.myweather2.com";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        mCountDownTextView = (TextView) findViewById(R.id.countdownText);
        startCountdownThread();
        request();
    }

    private void startCountdownThread() {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {

                    long flightTimeMillis = WidgetProvider.flightDate.getTime();
                    long timeUntilMillis = flightTimeMillis - System.currentTimeMillis();
                    long tempNum;

                    int weeksLeft = (int) (timeUntilMillis / WidgetProvider.WEEK);
                    tempNum = timeUntilMillis % WidgetProvider.WEEK;
                    int daysLeft = (int) (tempNum / WidgetProvider.DAY);
                    tempNum = tempNum % WidgetProvider.DAY;
                    int hoursLeft = (int) (tempNum / WidgetProvider.HOUR);
                    tempNum = tempNum % WidgetProvider.HOUR;
                    int minutesLeft = (int) (tempNum / WidgetProvider.MINUTE);
                    tempNum = tempNum % WidgetProvider.MINUTE;
                    int secondsLeft = (int) (tempNum / WidgetProvider.SECOND);

                    String weekString = weeksLeft == 1 ? "Week" : "Weeks";
                    String daysString = daysLeft == 1 ? "Day" : "Days";
                    String hoursString = hoursLeft == 1 ? "Hour" : "Hours";
                    String minutesString = minutesLeft == 1 ? "Minute" : "Minutes";
                    String secondsString = secondsLeft == 1 ? "Second" : "Seconds";

                    String message = weeksLeft + " " + weekString + ", " + daysLeft + " " + daysString + ", " + hoursLeft+ " " + hoursString + ", " +
                            minutesLeft + " " + minutesString + ", " + secondsLeft + " " + secondsString + " left until the flight!";

                    Message messageToSend = Message.obtain();
                    messageToSend.obj = message;
                    countdownHandler.sendMessage(messageToSend);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        th.start();
    }

    private void request(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
//        String url = "https://graph.facebook.com/v2.8/1452917798070567/photos?fields=images&access_token=1313945542000302%7Cr6VnCef8WKT35gTeTPEnb7o-2is";
        String url = "https://graph.facebook.com/v2.8/119544414445/photos?fields=images&access_token=1313945542000302%7Cr6VnCef8WKT35gTeTPEnb7o-2is";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("HHH", "Response is: "+ response);
                        try {
                            responseToImages(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("HHH", "That didn't work!");
            }
        });
    // Add the request to the RequestQueue.
        queue.add(stringRequest);


        String weatherUrl = "http://www.myweather2.com/developer/weather.ashx?uac=0kYvqQJ0lz&uref=55b71f79-cb35-4f19-9215-35e5a162f2f3";
        StringRequest weatherStringRequest = new StringRequest(Request.Method.GET, weatherUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("HHH", "Weather Response is: "+ response);
                        try {
                            JSONObject weatherJson = XML.toJSONObject(response);
                            displayData(weatherJson);
                            Log.d("HHH", "JSON Weather Response is: "+ weatherJson);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("HHH", "That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(weatherStringRequest);
    }

    private void displayData(JSONObject weatherJson) throws JSONException {
        TextView lastSnowFall = (TextView) findViewById(R.id.lastSnowFallDate);
        TextView bottomSnow = (TextView)findViewById(R.id.bottomSnowAmount);
        TextView topSnow = (TextView)findViewById(R.id.topSnowAmount);

        JSONObject weather = weatherJson.getJSONObject("weather");
        JSONObject snowReport = weather.getJSONObject("snow_report");
        lastSnowFall.setText(snowReport.getString("last_snow_date"));
        bottomSnow.setText(snowReport.getString("lower_snow_depth") + " Cm");
        topSnow.setText(snowReport.getString("upper_snow_depth") + " Cm");

        JSONArray forecast = weather.getJSONArray("forecast");
        displayDayWeather(forecast.getJSONObject(0), (TextView)findViewById(R.id.tempToday), (ImageView) findViewById(R.id.todayIcon));
        displayDayWeather(forecast.getJSONObject(1), (TextView)findViewById(R.id.tempTomorrow), (ImageView) findViewById(R.id.tomorowIcon));
        displayDayWeather(forecast.getJSONObject(2), (TextView)findViewById(R.id.tempAfter), (ImageView) findViewById(R.id.afterIcon));
        displayThirdDate(forecast.getJSONObject(2).getString("date"));
    }

    private void displayThirdDate(String date) {
        String[] dateParts = date.split("-");
        String newDate = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];
        ((TextView)findViewById(R.id.textView13)).setText(newDate);
    }

    private void displayDayWeather(JSONObject dayForecast, TextView tempDisplay, ImageView icon) throws JSONException {
        int max =  dayForecast.getInt("day_max_temp");
        int min = dayForecast.getInt("night_min_temp");
        String tempDisplayString = max + "ºC / " +  min + "ºC";
        tempDisplay.setText(tempDisplayString);
        setIcon(icon, dayForecast.getJSONObject("day").getInt("weather_code"));
    }

    private void setIcon(ImageView icon, int weatherCode) {
        switch(weatherCode) {
            case 0 :
                icon.setImageResource(R.drawable.sunny);
                break;
            case 1:
                icon.setImageResource(R.drawable.partlycloudyday);
                break;
            case 2:
                icon.setImageResource(R.drawable.cloudy);
                break;
            case 3:
                icon.setImageResource(R.drawable.overcast);
                break;
            case 21:
                icon.setImageResource(R.drawable.occlightrain);
                break;
            case 22:
                icon.setImageResource(R.drawable.isosleetswrsday);
                break;
            case 23:
                icon.setImageResource(R.drawable.occlightsleet);
                break;
            case 24:
                icon.setImageResource(R.drawable.freezingdrizzle);
                break;
            case 29:
                icon.setImageResource(R.drawable.partcloudrainthunderday);
                break;
            case 38:
                icon.setImageResource(R.drawable.modsnow);
                break;
            case 39:
                icon.setImageResource(R.drawable.blizzard);
                break;
            case 45:
                icon.setImageResource(R.drawable.fog);
                break;
            case 49:
                icon.setImageResource(R.drawable.freezingfog);
                break;
            case 50:
                icon.setImageResource(R.drawable.isorainswrsday);
                break;
            case 51:
                icon.setImageResource(R.drawable.occlightrain);
                break;
            case 56:
            case 57:
                icon.setImageResource(R.drawable.freezingdrizzle);
                break;
            case 60:
                icon.setImageResource(R.drawable.occlightrain);
                break;
            case 61:
                icon.setImageResource(R.drawable.modrain);
                break;
            case 70:
            case 71:
                icon.setImageResource(R.drawable.occlightsnow);
                break;
            case 72:
            case 73:
                icon.setImageResource(R.drawable.modsnow);
                break;
            case 74:
                icon.setImageResource(R.drawable.heavysnowswrsday);
                break;
            case 75:
                icon.setImageResource(R.drawable.heavysnow);
                break;
            default:
                icon.setImageResource(android.R.drawable.arrow_up_float);
                break;
        }
    }

    private void responseToImages(String response) throws JSONException {
        JSONObject big = new JSONObject(response);
        JSONArray dataArray = big.getJSONArray("data");
        JSONObject imagesUrls = new JSONObject();
        ArrayList<String> imageUrlsArray = new ArrayList<>();
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject dat = dataArray.getJSONObject(i);
            imagesUrls.put(dat.getString("id"), ((JSONObject)dat.getJSONArray("images").get(0)).getString("source").replace("\\", ""));
            imageUrlsArray.add(((JSONObject)dat.getJSONArray("images").get(0)).getString("source").replace("\\", ""));
        }

        Log.d("HHH", imagesUrls.toString());


//        Iterator<String> iter = imagesUrls.keys();
//        while (iter.hasNext()) {
//            String key = iter.next();
//            try {
//                Object value = imagesUrls.get(key);
//            } catch (JSONException e) {
//                // Something went wrong!
//            }
//        }
        int numOfItems = imageUrlsArray.size();
        int rand = (int) (System.currentTimeMillis()%numOfItems);
        Log.d("HHH", "numOfItems: " + numOfItems + " rand: " + rand);
        new DownloadImage().execute(imageUrlsArray.get(rand));
    }


public class DownloadImage extends AsyncTask<String, Integer, Drawable> {

    @Override
    protected Drawable doInBackground(String... arg0) {
        // This is done in a background thread
        return downloadImage(arg0[0]);
    }

    /**
     * Called after the image has been downloaded
     * -> this calls a function on the main thread again
     */
    protected void onPostExecute(Drawable image)
    {
        animate(mImageView, image);
//        mImageView.setImageDrawable(image);
    }


    /**
     * Actually download the Image from the _url
     * @param _url
     * @return
     */
    private Drawable downloadImage(String _url)
    {
        Log.d("HHH", "X)X)X)X)X) Download image: " + _url);
        //Prepare to download image
        URL url;
        BufferedOutputStream out;
        InputStream in;
        BufferedInputStream buf;

        //BufferedInputStream buf;
        try {
            url = new URL(_url);
            in = url.openStream();
            buf = new BufferedInputStream(in);

            // Convert the BufferedInputStream to a Bitmap
            Bitmap bMap = BitmapFactory.decodeStream(buf);
            if (in != null) {
                in.close();
            }
            if (buf != null) {
                buf.close();
            }

            return new BitmapDrawable(bMap);

        } catch (Exception e) {
            Log.e("Error reading file", e.toString());
        }

        return null;
    }

}

//    private void animate(final ImageView imageView, final int images[], final int imageIndex, final boolean forever) {
    private void animate(final ImageView imageView, Drawable imageToPut) {
        int fadeInDuration = 1000; // Configure time values here

        imageView.setVisibility(View.VISIBLE);    //Visible or invisible by default - this will apply when the animation ends
        imageView.setImageDrawable(imageToPut);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
        fadeIn.setDuration(fadeInDuration);

        AnimationSet animation = new AnimationSet(false); // change to false
        animation.addAnimation(fadeIn);

        imageView.setAnimation(animation);
        imageView.setPadding(0,0,0,0);
        mImageDescription.setVisibility(View.VISIBLE);

        ImageView myImageView= (ImageView)findViewById(R.id.logo);
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);
        myImageView.setVisibility(View.VISIBLE);
        myImageView.startAnimation(myFadeInAnimation);
    }

}
