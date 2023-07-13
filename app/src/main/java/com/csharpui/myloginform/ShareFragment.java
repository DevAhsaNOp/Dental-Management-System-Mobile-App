package com.csharpui.myloginform;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

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
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.utils.ObjectUtils;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ShareFragment extends Fragment {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Map config = new HashMap();
    AppCompatButton Uploadbutton;
    EditText Fullname, PhoneNumber, Email, City, Gender;
    RelativeLayout PickImagebutton;
    ViewPager viewPager;
    Uri ImageUri;
    ArrayList<Uri> ChooseImageList;
    ArrayList<String> UrlsList;
    ProgressDialog progressDialog;
    ProgressDialog progressDialog1;
    int CurrentUserID = 0, StateID = 0, AddressID = 0;
    String folderName = "";
    private static boolean isMediaManagerInitialized = false;

    public static String GenerateFileName() {
        String validCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        int length = 10;

        Random random = new Random();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(validCharacters.length());
            char randomChar = validCharacters.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_teeth, container, false);
        sharedPreferences = requireContext().getSharedPreferences("LoginFile", Context.MODE_PRIVATE);

        String userData = sharedPreferences.getString("userDetails", "");
        editor = sharedPreferences.edit();

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

        PickImagebutton = view.findViewById(R.id.ChooseImage);
        viewPager = view.findViewById(R.id.viewPager);
        Fullname = view.findViewById(R.id.Fullname);
        PhoneNumber = view.findViewById(R.id.Phone);
        Email = view.findViewById(R.id.Email);
        City = view.findViewById(R.id.City);
        Gender = view.findViewById(R.id.Gender);

        //Set Nav Menu Item
        Activity activity = getActivity();
        NavigationView navigationView = activity.findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_share);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        GetPatientDetails(CurrentUserID);

        configCloudinary();
        Uploadbutton = view.findViewById(R.id.UploadBtn);

        ChooseImageList = new ArrayList<>();
        UrlsList = new ArrayList<>();
        PickImagebutton.setOnClickListener(v -> CheckPermission());

        Uploadbutton.setOnClickListener(v -> {
            if (ChooseImageList.size() >= 5) {
                uploadToCloudinary(ChooseImageList, CurrentUserID);
            } else {
                Toast.makeText(getContext(), "Please upload minimum 5 images", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    @SuppressLint("ObsoleteSdkInt")
    private void CheckPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Activity activity = getActivity();
                ActivityCompat.requestPermissions(activity, new
                        String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            } else {
                PickImageFromgallery();
            }

        } else {
            PickImageFromgallery();
        }
    }

    private void PickImageFromgallery() {
        // here we go to gallery and select Image
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.getClipData() != null) {
            int count = data.getClipData().getItemCount();
            for (int i = 0; i < count; i++) {
                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                ChooseImageList.add(imageUri);
            }
            setAdapter();
        }
    }

    private void setAdapter() {
        System.out.println("Choose Image List: ");
        System.out.println(ChooseImageList);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity(), ChooseImageList);
        viewPager.setAdapter(adapter);
    }

    public void GetPatientDetails(int PatientID) {
        String url = "https://dmswebapi.bsite.net/api/Get/Patient?PatientID=" + PatientID;

        RequestQueue SQueue;
        SQueue = Volley.newRequestQueue(getContext());
        SQueue.add(HTTPReq.getRequest(url,
                new VolleyCallback() {
                    @SuppressLint("SetTextI18n")
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
                                    String UserGender = dataList.getString("Gender");
                                    String UserFname = dataList.getString("UserFirstName");
                                    String UserLname = dataList.getString("UserLastName");
                                    String UserPhoneNumber = dataList.getString("UserPhoneNumber");
                                    AddressID = dataList.getInt("UserAddressID");
                                    String UserEmail = dataList.getString("UserEmail");
                                    String UserPassword = dataList.getString("UserPassword");
                                    StateID = dataList.getInt("StateID");
                                    int CityID = dataList.getInt("CityID");
                                    Email.setText(UserEmail);
                                    PhoneNumber.setText(UserPhoneNumber);
                                    Fullname.setText(UserFname + " " + UserLname);
                                    Gender.setText(UserGender);

                                    if (CityID == 1) {
                                        City.setText("Hyderabad");
                                    } else if (CityID == 2) {
                                        City.setText("Karachi");
                                    } else if (CityID == 3) {
                                        City.setText("Multan");
                                    } else if (CityID == 4) {
                                        City.setText("Lahore");
                                    }
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
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "An error occurred please try later!",
                                Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void configCloudinary() {
        config.put("cloud_name", "dg5esqkeb");
        config.put("api_key", "654286619656251");
        config.put("api_secret", "7-JkBR5t_EU8lN3ArIdvsZ1txCw");

        if (!isMediaManagerInitialized) {
            MediaManager.init(getContext(), config);
            isMediaManagerInitialized = true;
        }
    }

    private void uploadToCloudinary(ArrayList<Uri> imageList, int PatientID) {
        progressDialog1 = new ProgressDialog(getContext());
        progressDialog1.setMessage("Please wait...");
        progressDialog1.show();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mmss");
        String minuteAndSeconds = LocalDateTime.now().format(formatter);
        folderName = "PatientTeethDetection/" + minuteAndSeconds + "Patient" + PatientID + "/PatientUploaded";

        int totalImages = imageList.size();
        final int[] uploadedImages = {0};

        for (Uri file : imageList) {
            String publicId = GenerateFileName();

            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "folder", folderName,
                    "tags", "PatientImages",
                    "public_id", publicId
            );

            MediaManager.get()
                    .upload(file)
                    .options(uploadParams)
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {
                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            uploadedImages[0]++;
                            if (uploadedImages[0] == totalImages) {
                                InsertPatientTest(); // Call your function here
                            }
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            Toast.makeText(getContext(), "An error occurred while uploading images. Please try again later!",
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {

                        }
                    }).dispatch();
        }
    }

    public void InsertPatientTest() {
        String baseURL = "https://dmswebapi.bsite.net/api/Insert/PatientTest";
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
                        if (statusCode.equals("412")) {
                            Toast.makeText(getContext(), "Invalid data provided!",
                                    Toast.LENGTH_LONG).show();
                            progressDialog1.dismiss();
                        } else if (statusCode.equals("500")) {
                            Toast.makeText(getContext(), "Error occured on adding Patient Test details. Please try again later!",
                                    Toast.LENGTH_LONG).show();
                            progressDialog1.dismiss();
                        } else if (statusCode.equals("200")) {
                            Toast.makeText(getContext(), "Patient Test has been added successfully!",
                                    Toast.LENGTH_LONG).show();
                            RunTeethDetection();
                        } else {
                            Toast.makeText(getContext(), "Error occurred on creating Account. Please try again later!",
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
                String ImagesCount = "5";
                String UserID = String.valueOf(CurrentUserID);
                String PT_Images = folderName;

                params.put("PT_PatientID", UserID);
                params.put("ImagesCount", ImagesCount);
                params.put("PT_Images", PT_Images);
                return params;
            }

        }.setRetryPolicy(policy);
        SQueue = Volley.newRequestQueue(getContext());
        SQueue.add(jsonObjRequest);
    }

    public void RunTeethDetection() {
        RequestQueue mQueue;
        mQueue = Volley.newRequestQueue(getContext());
        int PatientID = CurrentUserID;
        String url = "https://127.0.0.1:5000/api/api/patient/" + PatientID;

        mQueue.add(HTTPReq.getRequest(url,
                new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject response = new JSONObject(result);
                            String StatusCode = response.getString("StatusCode");
                            if (StatusCode.equals("204")){
                                Toast.makeText(getContext(), "Error record exists. No images found.!",
                                        Toast.LENGTH_LONG).show();
                                progressDialog1.dismiss();
                            }else if (StatusCode.equals("404")){
                                Toast.makeText(getContext(), "Error Record does not exist!",
                                        Toast.LENGTH_LONG).show();
                                progressDialog1.dismiss();
                            }else if (StatusCode.equals("500")){
                                Toast.makeText(getContext(), "Error in retrieving data!",
                                        Toast.LENGTH_LONG).show();
                                progressDialog1.dismiss();
                            }else if (StatusCode.equals("504")){
                                Toast.makeText(getContext(), "Error invalid data provided!",
                                        Toast.LENGTH_LONG).show();
                                progressDialog1.dismiss();
                            }else if (StatusCode.equals("200")){
                                Toast.makeText(getContext(), "Record exists. Teeth Testing initiated. Report " +
                                                "will be mailed under 5 - 10 minutes",
                                        Toast.LENGTH_LONG).show();
                                progressDialog1.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String result) {
                        System.out.println(result);
                        Toast.makeText(getContext(), "An error occurred please try later!",
                                Toast.LENGTH_SHORT).show();
                        progressDialog1.dismiss();
                    }
                }));
    }
}