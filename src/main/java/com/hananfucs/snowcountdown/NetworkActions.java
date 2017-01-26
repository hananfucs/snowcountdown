package com.hananfucs.snowcountdown;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

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
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Hanan on 26/01/17.
 */

public class NetworkActions {
    public interface NetworkActionsResponsListener {
        public void onActionComplete(Object response);
    }
    private NetworkActionsResponsListener imageListener;
    private RequestQueue queue;

    public NetworkActions(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    public void getImage(NetworkActionsResponsListener listener) {
        String url = "https://graph.facebook.com/v2.8/119544414445/photos?fields=images&access_token=1313945542000302%7Cr6VnCef8WKT35gTeTPEnb7o-2is";
        imageListener = listener;
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



    public void getWeather(final NetworkActionsResponsListener listener) {
        String weatherUrl = "http://www.myweather2.com/developer/weather.ashx?uac=0kYvqQJ0lz&uref=55b71f79-cb35-4f19-9215-35e5a162f2f3";
        StringRequest weatherStringRequest = new StringRequest(Request.Method.GET, weatherUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("HHH", "Weather Response is: "+ response);
                        try {
                            JSONObject weatherJson = XML.toJSONObject(response);
                            listener.onActionComplete(weatherJson);
//                            displayData(weatherJson);
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
        queue.add(weatherStringRequest);
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
            imageListener.onActionComplete(image);
//            animate(mImageView, image);
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

}
