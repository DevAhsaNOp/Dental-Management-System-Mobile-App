package com.csharpui.myloginform;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LoginActivity1 extends AppCompatActivity {

    EditText lemail, lpass;
    ProgressDialog progressDialog1;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextView errorText;
    private HashMap<String, String> userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        lemail = findViewById(R.id.loginemailtext);
        lpass = findViewById(R.id.loginpasstext);
        userDetails = new HashMap<>();

        sharedPreferences = getSharedPreferences("LoginFile", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String userData = sharedPreferences.getString("userDetails", "");
        if (sharedPreferences.getString("isLoggedIn", "").equals("true") && userData != null) {
            System.out.println("User Data Received");
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(userData);
                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    String value = null;
                    try {
                        value = jsonObject.getString(key);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    userDetails.put(key, value);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println(userData);
            startActivity(new Intent(LoginActivity1.this, Dashboard.class));
            finishAffinity();
        }

        errorText = findViewById(R.id.errorText);
        Button button = findViewById(R.id.loginbutton);
        button.setOnClickListener(v -> {
            boolean isallfieldschecked = validate();
            if (isallfieldschecked) {
                RegisterUser();
            }
        });
    }

    private boolean validate() {
        boolean res = true;
        if (lemail.length() == 0) {
            lemail.setError("Email Address is required");
            res = false;
        }

        if (lpass.length() == 0) {
            lpass.setError("Password is required");
            res = false;
        }
        // after all validation return true.
        return res;
    }

    public void Reg_Click(View view) {
        Intent intent = new Intent(LoginActivity1.this, SigupActivity.class);
        startActivity(intent);
    }

    public void RegisterUser() {
        progressDialog1 = new ProgressDialog(LoginActivity1.this);
        progressDialog1.setMessage("Please wait...");
        progressDialog1.show();

        String baseURL = "https://dmswebapi.azurewebsites.net/api/login";
        RequestQueue SQueue;
        RetryPolicy policy = new APICustomRetryPolicy();
        StringRequest jsonObjRequest = (StringRequest) new StringRequest(
                Request.Method.POST,
                baseURL,
                response -> {
                    try {
                        JSONObject result = new JSONObject(response);
                        String statusCode = result.getString("StatusCode");
                        if (statusCode.equals("404")) {
                            //showDailog("Invalid username and password!");
                            errorText.setVisibility(View.VISIBLE);
                            progressDialog1.dismiss();
                        } else if (statusCode.equals("200")) {
                            //showDailog("Account Login successfully!");
                            errorText.setVisibility(View.INVISIBLE);
                            progressDialog1.dismiss();
                            String UserID = String.valueOf(result.getInt("UserID"));
                            String Username = result.getString("Username");
                            userDetails.put("UserID", UserID);
                            userDetails.put("Username", Username);

                            Toast.makeText(LoginActivity1.this, "Login Successful!",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity1.this, Dashboard.class);
                            JSONObject userJsonObject = new JSONObject(userDetails);
                            String userData = userJsonObject.toString();
                            System.out.println("API Received Data");
                            System.out.println(userData);
                            editor.putString("userDetails", userData);
                            editor.putString("isLoggedIn", "true");
                            editor.commit();
                            startActivity(intent);
                        } else {
                            showDailog("Error occurred on login Account. Please try again later!");
                            progressDialog1.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    VolleyLog.d("volley", "Error: " + error.getMessage());
                    showDailog("Error occurred on login Account. Please try again later!");
                    progressDialog1.dismiss();
                    error.printStackTrace();
                }) {

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

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String email = lemail.getText().toString().trim();
                String password = lpass.getText().toString().trim();

                params.put("UserEmail", email);
                params.put("UserPassword", password);
                return params;
            }

        }.setRetryPolicy(policy);
        SQueue = Volley.newRequestQueue(LoginActivity1.this);
        SQueue.add(jsonObjRequest);
    }

    private void showDailog(String Text) {
        new AlertDialog.Builder(this)
                .setTitle(Text)
                .setPositiveButton("Close", (dailog, which) -> resetFields()).show();
    }

    private void resetFields() {

    }
}