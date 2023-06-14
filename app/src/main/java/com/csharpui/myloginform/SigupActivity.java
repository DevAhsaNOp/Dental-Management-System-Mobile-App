package com.csharpui.myloginform;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SigupActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    String mobilePattern = "03[0-9]{2}(?!1234567)(?!1111111)(?!7654321)[-]?[0-9]{7}";
    Button nextBtn;
    EditText email, pass;
    Spinner cityDropdown;
    EditText fname, lname, phoneno;
    TextView logintextview, gender;
    ProgressDialog progressDialog;
    ProgressDialog progressDialog1;
    RadioButton maleradbtn, femaleradbtn;
    ArrayAdapter<String> spinnerAdapter;
    String selectedGender;
    List<JSONObject> allCitiesData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        // Fragment Code
//        Fragment fragment = new reg_frag1();
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.fragment_container,fragment,"FirstFragment");
//        transaction.commit();
        progressDialog = new ProgressDialog(SigupActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        logintextview = findViewById(R.id.logintext);
        fname = findViewById(R.id.fnametext);
        lname = findViewById(R.id.lnametext);
        phoneno = findViewById(R.id.pNotext);
        gender = findViewById(R.id.gender);
        maleradbtn = findViewById(R.id.maleradiobtn);
        femaleradbtn = findViewById(R.id.femaleradiobtn);
        email = findViewById(R.id.emailtext);
        pass = findViewById(R.id.passtext);
        cityDropdown = findViewById(R.id.citytext);
        GetCities();
        nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(v -> {
            boolean isAllFieldsChecked = validate();
            if (isAllFieldsChecked) {
                progressDialog1 = new ProgressDialog(SigupActivity.this);
                progressDialog1.setMessage("Please wait...");
                progressDialog1.show();
                IsNumberExist(phoneno.getText().toString());
            }
        });

        logintextview.setOnClickListener(v -> {
            Intent intent = new Intent(SigupActivity.this, LoginActivity1.class);
            startActivity(intent);
        });

        RadioGroup radioGroup = findViewById(R.id.genderGroup);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = findViewById(checkedId);
            selectedGender = radioButton.getText().toString();
        });
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
        mQueue = Volley.newRequestQueue(this);

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
                            spinnerAdapter = new ArrayAdapter<>(getApplicationContext(),
                                    android.R.layout.simple_spinner_item, cityNames);
                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            cityDropdown.setAdapter(spinnerAdapter);
                            cityDropdown.setSelection(0);
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        System.out.println(result);
                        progressDialog.dismiss();
                        Toast.makeText(SigupActivity.this, "An error occurred please try later!",
                                Toast.LENGTH_SHORT).show();

                    }
                }));
    }

    public void IsEmailExist(String Email) {
        String url = "https://dmswebapi.bsite.net/api/Check/EmailExist?Email=" + Email;

        RequestQueue SQueue;
        SQueue = Volley.newRequestQueue(this);
        SQueue.add(HTTPReq.getRequest(url,
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject response = new JSONObject(result);
                            String statusCode = response.getString("StatusCode");
                            if (statusCode.equals("302")) {
                                email.setError("Email already exists");
                                progressDialog1.dismiss();
                            } else {
                                SendOtp(email.getText().toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        System.out.println(result);
                        progressDialog1.dismiss();
                        Toast.makeText(SigupActivity.this, "An error occurred please try later!",
                                Toast.LENGTH_SHORT).show();

                    }
                }));
    }

    public void IsNumberExist(String Number) {
        RequestQueue SQueue;
        SQueue = Volley.newRequestQueue(this);
        SQueue.add(HTTPReq.getRequest("https://dmswebapi.bsite.net/api/Check/PhoneNumberExist?PhoneNumber=" + Number,
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject response = new JSONObject(result);
                            String statusCode = response.getString("StatusCode");
                            if (statusCode.equals("302")) {
                                phoneno.setError("Phone Number already exists");
                                progressDialog1.dismiss();
                            } else {
                                IsEmailExist(email.getText().toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        System.out.println(result);
                        progressDialog1.dismiss();
                        Toast.makeText(SigupActivity.this, "An error occurred please try later!",
                                Toast.LENGTH_SHORT).show();

                    }
                }));
    }

    public void SendOtp(String Email) {
        RequestQueue SQueue;
        SQueue = Volley.newRequestQueue(this);
        SQueue.add(HTTPReq.getRequest("https://dmswebapi.bsite.net/api/Send/OTP?Email=" + Email,
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject response = new JSONObject(result);
                            String statusCode = response.getString("StatusCode");
                            if (statusCode.equals("500")) {
                                AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
                                alertDialog.setTitle("OTP Send Error");
                                alertDialog.setMessage("Error occurred on sending OTP at your Email Account");
                                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK",
                                        (dialog, which) -> dialog.dismiss());
                                alertDialog.show();
                                progressDialog1.dismiss();
                            } else {
                                progressDialog1.dismiss();

                                // If Phone Number and Email does not Exist navigate to verify OTP
                                HashMap<String, Object> data = setValues();
                                Intent intent = new Intent(SigupActivity.this, VerifyOtp.class);
                                intent.putExtra("patientData", data);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        System.out.println(result);
                        progressDialog1.dismiss();
                        Toast.makeText(SigupActivity.this, "An error occurred please try later!",
                                Toast.LENGTH_SHORT).show();

                    }
                }));
    }

    HashMap<String, Object> setValues() {
        int citySelectedValue = (int) cityDropdown.getSelectedItemId();
        int stateID = 0;
        for (JSONObject cityData : allCitiesData) {
            try {
                if (cityData.getInt("CityID") == citySelectedValue) {
                    stateID = cityData.getInt("StateID");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        HashMap<String, Object> data = new HashMap<>();
        data.put("Firstname", fname.getText().toString());
        data.put("Lastname", lname.getText().toString());
        data.put("PhoneNumber", phoneno.getText().toString());
        data.put("CityID", citySelectedValue);
        data.put("StateID", stateID);
        data.put("Email", email.getText().toString());
        data.put("Password", pass.getText().toString());
        data.put("Gender", selectedGender);
        return data;
    }


//    private void openFileExplorer() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");  // Set the MIME type to select only image files
//        startActivityForResult(intent, PICK_IMAGE_REQUEST);
//    }
//    public void setImage(Uri bitmap) {
//        // Set the bitmap to your ImageView or use it as needed
//        imageView.setImageURI(bitmap);
//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
//            Uri selectedImageUri = data.getData();
//
//            imageView.setBackground(null);
//            imageView.setImageURI(selectedImageUri);
//            t1.setVisibility(INVISIBLE);
//            // Assuming you have the Image URI stored in a variable named "imageUri"
//            SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putString("imageUri", selectedImageUri.toString());
//            editor.apply();
//            k=true;
//        }
//    }
}


