package com.hananfucs.snowcountdown;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
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
        request();
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
//        animate(mImageView, image);
        mImageView.setImageDrawable(image);
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
    }

}
