package com.hananfucs.snowcountdown;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        request();
    }

    private void test() {
//        GraphRequest request = GraphRequest.newMeRequest(
//                null,
//                new GraphRequest.GraphJSONObjectCallback() {
//                    @Override
//                    public void onCompleted(JSONObject object, GraphResponse response) {
//
//                    }
//                });
//        Bundle parameters = new Bundle();
//        parameters.putString("fields", "id,name,link");
//        request.setParameters(parameters);
//        request.executeAsync();

//        AccessToken.getCurrentAccessToken();
//        GraphRequest request = GraphRequest.newGraphPathRequest(
//                accessToken,
//                "/1452917798070567/photos",
//                new GraphRequest.Callback() {
//                    @Override
//                    public void onCompleted(GraphResponse response) {
//                        // Insert your code here
//                    }
//                });
//
//        Bundle parameters = new Bundle();
//        parameters.putString("fields", "images");
//        request.setParameters(parameters);
//        request.executeAsync();

    }



    private void request(){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://graph.facebook.com/v2.8/1452917798070567/photos?fields=images&access_token=1313945542000302%7Cr6VnCef8WKT35gTeTPEnb7o-2is";

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
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject dat = dataArray.getJSONObject(i);
            imagesUrls.put(dat.getString("id"), ((JSONObject)dat.getJSONArray("images").get(0)).getString("source"));
        }

        Log.d("HHH", imagesUrls.toString().replace("\\", ""));
    }
}
