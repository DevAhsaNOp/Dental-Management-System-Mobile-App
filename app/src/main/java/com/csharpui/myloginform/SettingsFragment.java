package com.csharpui.myloginform;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsFragment extends Fragment {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String mobilePattern = "03[0-9]{2}(?!1234567)(?!1111111)(?!7654321)[-]?[0-9]{7}";
    Button updateProfile;
    EditText email, pass;
    Spinner cityDropdown;
    EditText fname, lname, phoneno;
    TextView gender;
    ProgressDialog progressDialog;
    ProgressDialog progressDialog1;
    RadioButton maleradbtn, femaleradbtn;
    ArrayAdapter<String> spinnerAdapter;
    String selectedGender;
    List<JSONObject> allCitiesData = new ArrayList<>();
    int CurrentUserID = 0, StateID = 0, AddressID = 0;
    String profileImage = "";
    HashMap<String, Object> data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        sharedPreferences = requireContext().getSharedPreferences("LoginFile", Context.MODE_PRIVATE);

        String userData = sharedPreferences.getString("userDetails", "");
        editor = sharedPreferences.edit();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        fname = view.findViewById(R.id.fnametext);
        lname = view.findViewById(R.id.lnametext);
        phoneno = view.findViewById(R.id.pNotext);
        gender = view.findViewById(R.id.gender);
        maleradbtn = view.findViewById(R.id.maleradiobtn);
        femaleradbtn = view.findViewById(R.id.femaleradiobtn);
        email = view.findViewById(R.id.emailtext);
        pass = view.findViewById(R.id.passtext);
        cityDropdown = view.findViewById(R.id.citytext);
        GetCities();

        if (sharedPreferences.getString("isLoggedIn", "").equals("true") && userData != null) {
            System.out.println("User Data Received");
            try {
                JSONObject jsonObject = new JSONObject(userData);
                String UserID = jsonObject.getString("UserID");
                CurrentUserID = Integer.parseInt(UserID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println(userData);
        }

        updateProfile = view.findViewById(R.id.updateProfile);
        updateProfile.setOnClickListener(v -> {
            boolean isAllFieldsChecked = validate();
            if (isAllFieldsChecked) {
                UpdateUser();
            }
        });


        RadioGroup radioGroup = view.findViewById(R.id.genderGroup);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = view.findViewById(checkedId);
            selectedGender = radioButton.getText().toString();
        });

        return view;
    }

    @SuppressLint("SetTextI18n")
    private boolean validate() {
        boolean res = true;
        int spinnerselecteditempos = cityDropdown.getSelectedItemPosition();
        TextView errorText = (TextView) cityDropdown.getSelectedView();
        Pattern regexPhone = Pattern.compile(mobilePattern);
        Matcher phoneRegexMatcher = regexPhone.matcher(phoneno.getText());
        if (fname.length() == 0 && lname.length() == 0 && phoneno.length() == 0 && email.length() == 0 && pass.length() == 0
                && (!(spinnerselecteditempos > 0))
                && !(maleradbtn.isChecked() || femaleradbtn.isChecked())) {
            fname.setError("This field is required");
            lname.setError("This field is required");
            phoneno.setError("This field is required");
            gender.setTextColor(Color.RED);
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText("Please select city");
            cityDropdown.requestFocus();
            email.setError("This field is required");
            pass.setError("This field is required");
            res = false;
        }
        if (fname.length() == 0) {
            fname.setError("This field is required");
            res = false;
        } else {
            fname.setError(null);
        }
        if (lname.length() == 0) {
            lname.setError("This field is required");
            res = false;
        } else {
            lname.setError(null);
        }
        if (phoneno.length() == 0) {
            phoneno.setError("This field is required");
            res = false;
        } else {
            if (phoneno.length() < 11 || phoneno.length() > 11) {
                phoneno.setError("Phone Number contain 11 digits");
                res = false;
            } else if (phoneRegexMatcher.matches()) {
                phoneno.setError(null);
            } else {
                phoneno.setError("Number should start's with 03");
                res = false;
            }
        }
        if (!(maleradbtn.isChecked() || femaleradbtn.isChecked())) {
            gender.setTextColor(Color.RED);
            res = false;
        } else {
            gender.setTextColor(Color.BLACK);
        }
        if (!(spinnerselecteditempos > 0)) {
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText("Please select city");
            res = false;
        }
        if (email.length() == 0 && pass.length() == 0 && spinnerselecteditempos == 0) {
            email.setError("This field is required");
            pass.setError("This field is required");
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            errorText.setText("Please select city");
            res = false;
        }
        if (email.length() == 0) {
            email.setError("This field is required");
            res = false;
        } else {
            if (!(Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches())) {
                email.setError("Invalid Email Address");
                res = false;
            } else {
                email.setError(null);
            }
        }
        if (pass.length() == 0) {
            pass.setError("This field is required");
            res = false;
        } else {
            pass.setError(null);
        }
        // after all validation return true.
        return res;
    }


    public void GetCities() {
        Map<Integer, String> cityDataMap = new HashMap<>();
        cityDataMap.put(0, "Please select city");
        RequestQueue mQueue;
        mQueue = Volley.newRequestQueue(getContext());

        mQueue.add(HTTPReq.getRequest("https://dmswebapi.bsite.net/api/Get/City",
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject response = new JSONObject(result);
                            JSONArray dataList = response.getJSONArray("Datalist");
                            for (int i = 0; i < dataList.length(); i++) {
                                JSONObject cityData = dataList.getJSONObject(i);
                                allCitiesData.add(cityData);
                                int CityID = cityData.getInt("CityID");
                                String CityName = cityData.getString("CityName");
                                cityDataMap.put(CityID, CityName);
                            }
                            List<String> cityNames = new ArrayList<>(cityDataMap.values());
                            spinnerAdapter = new ArrayAdapter<>(getContext(),
                                    android.R.layout.simple_spinner_item, cityNames);
                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            cityDropdown.setAdapter(spinnerAdapter);
                            cityDropdown.setSelection(0);
                            GetPatientDetails(CurrentUserID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        System.out.println(result);
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "An error occurred please try later!",
                                Toast.LENGTH_SHORT).show();

                    }
                }));
    }

    public void GetPatientDetails(int PatientID) {
        String url = "https://dmswebapi.bsite.net/api/Get/Patient?PatientID=" + PatientID;

        RequestQueue SQueue;
        SQueue = Volley.newRequestQueue(getContext());
        SQueue.add(HTTPReq.getRequest(url,
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject response = new JSONObject(result);
                            String statusCode = response.getString("StatusCode");
                            if (statusCode.equals("200")) {
                                JSONObject dataList = response.getJSONObject("Datalist");
                                System.out.println("Patient Data Received!");
                                System.out.println(dataList);
                                for (int i = 0; i < dataList.length(); i++) {
                                    CurrentUserID = dataList.getInt("UserID");
                                    String Gender = dataList.getString("Gender");
                                    String UserFname = dataList.getString("UserFirstName");
                                    String UserLname = dataList.getString("UserLastName");
                                    String UserPhoneNumber = dataList.getString("UserPhoneNumber");
                                    AddressID = dataList.getInt("UserAddressID");
                                    String UserEmail = dataList.getString("UserEmail");
                                    String UserPassword = dataList.getString("UserPassword");
                                    StateID = dataList.getInt("StateID");
                                    int CityID = dataList.getInt("CityID");
                                    profileImage = dataList.getString("UserProfileImage");
                                    email.setText(UserEmail);
                                    phoneno.setText(UserPhoneNumber);
                                    fname.setText(UserFname);
                                    lname.setText(UserLname);
                                    pass.setText(UserPassword);
                                    if (Gender.equals("Male")){
                                        maleradbtn.setChecked(true);
                                        femaleradbtn.setChecked(false);
                                    }else{
                                        maleradbtn.setChecked(false);
                                        femaleradbtn.setChecked(true);
                                    }
                                    cityDropdown.setSelection(CityID);
                                }
                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(getContext(), "An error occurred please try later!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        System.out.println(result);
                        progressDialog1.dismiss();
                        Toast.makeText(getContext(), "An error occurred please try later!",
                                Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    public void UpdateUser() {
        progressDialog1 = new ProgressDialog(getContext());
        progressDialog1.setMessage("Please wait...");
        progressDialog1.show();

        String baseURL = "https://dmswebapi.bsite.net/api/Update/Patient";
        RequestQueue SQueue;
        RetryPolicy policy = new APICustomRetryPolicy();
        StringRequest jsonObjRequest = (StringRequest) new StringRequest(
                Request.Method.POST,
                baseURL,
                response -> {
                    try {
                        JSONObject result = new JSONObject(response);
                        String statusCode = result.getString("StatusCode");
                        System.out.println("Status Code:" + statusCode);
                        if (statusCode.equals("500")) {
                            Toast.makeText(getContext(),"Error occurred on updating Account. Please try again later!",
                                    Toast.LENGTH_LONG).show();
                            progressDialog1.dismiss();
                        }else if (statusCode.equals("405")) {
                            Toast.makeText(getContext(),"Phone Number already exists. Please ensure to enter not used phone number again!",
                                    Toast.LENGTH_LONG).show();
                            progressDialog1.dismiss();
                        }else if (statusCode.equals("404")) {
                            Toast.makeText(getContext(),"Email already exists. Please ensure to enter not used email account again!",
                                    Toast.LENGTH_LONG).show();
                            progressDialog1.dismiss();
                        }else if (statusCode.equals("412")) {
                            Toast.makeText(getContext(),"Invalid data provided. Please try again later!",
                                    Toast.LENGTH_LONG).show();
                            progressDialog1.dismiss();
                        }else if (statusCode.equals("200")){
                            Toast.makeText(getContext(),"Account updated successfully!",
                                    Toast.LENGTH_LONG).show();
                            progressDialog1.dismiss();
                        }else {
                            Toast.makeText(getContext(),"Error occurred on creating Account. Please try again later!",
                                    Toast.LENGTH_LONG).show();
                            progressDialog1.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    VolleyLog.d("volley", "Error: " + error.getMessage());
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
                int citySelectedValue = (int) cityDropdown.getSelectedItemId();
                String firstname = fname.getText().toString();
                String lastname = lname.getText().toString();
                String phoneNumber = phoneno.getText().toString();
                String Email = email.getText().toString();
                String password = pass.getText().toString();
                String stateID = String.valueOf(StateID);
                String cityID = String.valueOf(citySelectedValue);
                String gender = selectedGender;
                String UserID =  String.valueOf(CurrentUserID);

                params.put("UserID", UserID);
                params.put("UserFirstName", firstname);
                params.put("UserLastName", lastname);
                params.put("UserPhoneNumber", phoneNumber);
                params.put("UserUpdatePhoneNumber", phoneNumber);
                params.put("UserProfileImage", profileImage);
                params.put("UserEmail", Email);
                params.put("UserUpdateEmail", Email);
                params.put("UserPassword", password);
                params.put("StateID", stateID);
                params.put("CityID", cityID);
                params.put("Gender", gender);
                params.put("UserOTP", "123456");
                params.put("UserUpdatedBy", UserID);
                params.put("UserCreatedBy", UserID);
                return params;
            }

        }.setRetryPolicy(policy);
        SQueue = Volley.newRequestQueue(getContext());
        SQueue.add(jsonObjRequest);
    }
}