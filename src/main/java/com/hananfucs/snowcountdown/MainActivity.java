package com.hananfucs.snowcountdown;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request();
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

    private void setImage(Drawable drawable)
    {
        mImageView.setImageDrawable(drawable);
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
        setImage(image);
    }


    /**
     * Actually download the Image from the _url
     * @param _url
     * @return
     */
    private Drawable downloadImage(String _url)
    {
        //Prepare to download image
        URL url;
        BufferedOutputStream out;
        InputStream in;
        BufferedInputStream buf;

        //BufferedInputStream buf;
        try {
            url = new URL(_url);
            in = url.openStream();

            /*
             * THIS IS NOT NEEDED
             *
             * YOU TRY TO CREATE AN ACTUAL IMAGE HERE, BY WRITING
             * TO A NEW FILE
             * YOU ONLY NEED TO READ THE INPUTSTREAM
             * AND CONVERT THAT TO A BITMAP
            out = new BufferedOutputStream(new FileOutputStream("testImage.jpg"));
            int i;

             while ((i = in.read()) != -1) {
                 out.write(i);
             }
             out.close();
             in.close();
             */

            // Read the inputstream
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

}
