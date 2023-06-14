package com.csharpui.myloginform;

import androidx.core.view.accessibility.AccessibilityEventCompat;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class HTTPReq {
    public static StringRequest getRequest(String path, final VolleyCallback callback) {
        // Instantiate the RequestQueue.
        String url = path;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error.toString());
            }
        }
        ) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    // Extract the required JSON string from jsonString if needed
                    System.out.println(jsonString);
                    // Return the JSON string
                    return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }
        };
        RetryPolicy policy = new APICustomRetryPolicy();
        request.setRetryPolicy(policy);
        return request;
    }


}