package com.csharpui.myloginform;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VerifyOtp extends AppCompatActivity {

    EditText optText;
    TextView userEmail, resendOtp;
    HashMap<String, Object> data;
    ProgressDialog progressDialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        optText = findViewById(R.id.otpText);
        userEmail = findViewById(R.id.userEmail);
        resendOtp = findViewById(R.id.resendOtp);
        data = (HashMap<String, Object>) getIntent().getSerializableExtra("patientData");
        userEmail.setText(Objects.requireNonNull(data.get("Email")).toString());

        Button button = findViewById(R.id.SignUpBtn);
        button.setOnClickListener(v -> {
            boolean isallfieldschecked = validate();
            if (isallfieldschecked) {
                RegisterUser();
            }
        });
    }

    private boolean validate() {
        boolean res = true;
        if (optText.length() == 0) {
            optText.setError("OTP is required");
            res = false;
        }
        return res;
    }

    public void ResendOTP(View view) {
        progressDialog1 = new ProgressDialog(VerifyOtp.this);
        progressDialog1.setMessage("Please wait...");
        progressDialog1.show();
        RequestQueue SQueue;
        SQueue = Volley.newRequestQueue(this);
        SQueue.add(HTTPReq.getRequest("https://dmswebapi.bsite.net/api/Send/OTP?Email=" +
                        userEmail.getText().toString(),
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject response = new JSONObject(result);
                            String statusCode = response.getString("StatusCode");
                            if (statusCode.equals("500")) {
                                showDailog("Error occurred while sending OTP at your Email Account");
                                progressDialog1.dismiss();
                            } else {
                                showDailog("OTP has been send at your Email Account successfully!");
                                resendOtp.setEnabled(false);
                                progressDialog1.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        System.out.println(result);
                        progressDialog1.dismiss();
                        Toast.makeText(VerifyOtp.this, "An error occurred please try later!",
                                Toast.LENGTH_SHORT).show();

                    }
                }));
    }

    public void RegisterUser() {
        progressDialog1 = new ProgressDialog(VerifyOtp.this);
        progressDialog1.setMessage("Please wait...");
        progressDialog1.show();

        String baseURL = "https://dmswebapi.bsite.net/api/Register/Patient";
        RequestQueue SQueue;
        RetryPolicy policy = new APICustomRetryPolicy();
        StringRequest jsonObjRequest = (StringRequest) new StringRequest(
                Request.Method.POST,
                baseURL,
                response -> {
                    try {
                        JSONObject result = new JSONObject(response);
                        String statusCode = result.getString("StatusCode");
                        if (statusCode.equals("500")) {
                            showDailog("Error occurred on creating Account. Please try again later!");
                            progressDialog1.dismiss();
                        }else if (statusCode.equals("405")) {
                            showDailog("Phone Number already exists. Please ensure to enter not used phone number again!");
                            progressDialog1.dismiss();
                        }else if (statusCode.equals("404")) {
                            showDailog("Email already exists. Please ensure to enter not used email account again!");
                            progressDialog1.dismiss();
                        }else if (statusCode.equals("403")) {
                            showDailog("Invalid OTP provided. Please ensure to enter correct OTP again!");
                            progressDialog1.dismiss();
                        }else if (statusCode.equals("412")) {
                            showDailog("Invalid data provided!");
                            progressDialog1.dismiss();
                        }else if (statusCode.equals("201")){
                            showDailog("Account registered successfully!");
                            progressDialog1.dismiss();

                            Toast.makeText(VerifyOtp.this,"Sign Up Successful! Login to " +
                                    "access your Account",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(VerifyOtp.this, LoginActivity1.class);
                            startActivity(intent);
                        }else {
                            showDailog("Error occurred on creating Account. Please try again later!");
                            progressDialog1.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    VolleyLog.d("volley", "Error: " + error.getMessage());
                    showDailog("Error occurred on creating Account. Please try again later!");
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
                String firstname = data.get("Firstname").toString().trim();
                String lastname = data.get("Lastname").toString().trim();
                String phoneNumber = data.get("PhoneNumber").toString().trim();
                String email = data.get("Email").toString().trim();
                String password = data.get("Password").toString().trim();
                String otp = optText.getText().toString().trim();
                String stateID = data.get("StateID").toString().trim();
                String cityID = data.get("CityID").toString().trim();
                String gender = data.get("Gender").toString().trim();

                params.put("UserFirstName", firstname);
                params.put("UserLastName", lastname);
                params.put("UserPhoneNumber", phoneNumber);
                params.put("UserProfileImage", "~/uploads/PatientsProfileImage/nophoto.png");
                params.put("UserEmail", email);
                params.put("UserPassword", password);
                params.put("UserCreatedBy", "1");
                params.put("UserOTP", otp);
                params.put("StateID", stateID);
                params.put("CityID", cityID);
                params.put("Gender", gender);
                params.put("UserUpdatedBy", "1");
                return params;
            }

        }.setRetryPolicy(policy);
        SQueue = Volley.newRequestQueue(VerifyOtp.this);
        SQueue.add(jsonObjRequest);
    }


    private void showDailog(String Text){
        new AlertDialog.Builder(this)
                .setTitle(Text)
                .setPositiveButton("Close", (dailog, which) -> resetFields()).show();
    }

    private void resetFields(){

    }

}