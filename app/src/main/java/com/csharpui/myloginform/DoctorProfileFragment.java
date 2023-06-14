package com.csharpui.myloginform;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DoctorProfileFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyAdapter1 adapter;
    private static final String TAG = DoctorProfileFragment.class.getSimpleName();
    private List<PracticeDetails> apiResponseData;
    private Context context;
    private RequestQueue requestQueue;
    private ImageView imageView;
    private TextView nameTextView;
    private TextView SpecializtionTextView, YearOfExperience, AboutMe;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doctor_profile, container, false);

        imageView = view.findViewById(R.id.imageView);
        nameTextView = view.findViewById(R.id.nameTextView);
        SpecializtionTextView = view.findViewById(R.id.SpecializtionTextView);
        YearOfExperience = view.findViewById(R.id.YearOfExperienceTextView);
        AboutMe = view.findViewById(R.id.DoctorAboutMe);

        recyclerView = view.findViewById(R.id.recyclerView1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        String doctorID = getArguments().getString("doctorID");
        String doctorName = getArguments().getString("doctorName");
        String doctorAbout = getArguments().getString("doctorAbout");
        String doctorSpec = getArguments().getString("doctorSpecial");
        String doctorYOE = getArguments().getString("doctorYoE");
        String doctorAvatar = getArguments().getString("doctorAvatar");
        String doctorphoneNo = getArguments().getString("doctorPhone");
        String doctorEmail = getArguments().getString("doctorEmail");

        Picasso.get().load(doctorAvatar).into(imageView);
        nameTextView.setText("Name: " + doctorName);
        SpecializtionTextView.setText("Specialization: " + doctorSpec);
        YearOfExperience.setText("Yer of experience: " + doctorYOE);
        AboutMe.setText(doctorAbout);
        apiResponseData = new ArrayList<>();

        // Initialize the RequestQueue
        requestQueue = Volley.newRequestQueue(getContext());

        // Make the API request
        getDoctorDetails(doctorID);

        return view;
    }

    private void getDoctorDetails(String doctorID) {
        String url = "https://dmswebapi.bsite.net/api/Get/DoctorOfflineConsultaionDetails?DoctorID="+Integer.parseInt(doctorID);

        requestQueue.add(new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "API Response: " + response.toString());
                        apiResponseData = parseResponse(response);
                        adapter = new MyAdapter1(apiResponseData);
                        recyclerView.setAdapter(adapter);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching API response: " + error.getMessage());
                        Toast.makeText(getContext(), "An error occurred, please try again later!",
                                Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private List<PracticeDetails> parseResponse(JSONObject response) {
        List<PracticeDetails> userList = new ArrayList<>();

        try {
            String statusCode = response.getString("StatusCode");
            if (statusCode.equals("200")) {

                JSONArray userObject = response.getJSONArray("Datalist");
                for (int i = 0; i < userObject.length(); i++) {
                    JSONObject data = userObject.getJSONObject(i);
                    String days="";
                    String timings = "";
                    String HospitalName = data.getString("OFCD_HospitalName");
                    String mondayStartTime = data.getString("OFCD_MondayStartTime");
                    String tuesdayStartTime = data.getString("OFCD_TuesdayStartTime");
                    String wednesdayStartTime = data.getString("OFCD_WednesdayStartTime");
                    String thursdayStartTime = data.getString("OFCD_ThursdayStartTime");
                    String fridayStartTime = data.getString("OFCD_FridayStartTime");
                    String SaturdayStartTime = data.getString("OFCD_SaturdayStartTime");
                    String SundayStartTime = data.getString("OFCD_SundayStartTime");
                    String Fees = data.getString("OFCD_Charges");
                    if (mondayStartTime != "null"){
                        if (timings == ""){
                            String mondayEndTime = data.getString("OFCD_MondayEndTime");
                            timings = timings.concat(mondayStartTime + " - "+mondayEndTime);
                        }
                        days =days.concat(" Monday");
                    }
                    if (tuesdayStartTime != "null"){
                        days =days.concat(" Tuesday, ");
                        if (timings == ""){
                            String tuesdayEndTime = data.getString("OFCD_TuesdayEndTime");
                            timings = timings.concat(tuesdayStartTime + " - "+tuesdayEndTime);
                        }
                    }
                    if (wednesdayStartTime != "null"){
                        days =days.concat(" Wednesday, ");
                        if (timings == ""){
                            String wednesdayEndTime = data.getString("OFCD_WednesdayEndTime");
                            timings = timings.concat(wednesdayEndTime + " - "+wednesdayEndTime);
                        }
                    }
                    if (thursdayStartTime != "null"){
                        days =days.concat(" Thursday, ");
                        if (timings == ""){
                            String thursdayEndTime = data.getString("OFCD_ThursdayEndTime");
                            timings = timings.concat(thursdayStartTime + " - "+thursdayEndTime);
                        }
                    }
                    if (fridayStartTime != "null"){
                        days = days.concat(" Friday, ");
                        if (timings == ""){
                            String fridayEndTime = data.getString("OFCD_FridayEndTime");
                            timings = timings.concat(fridayStartTime + " - "+fridayEndTime);
                        }
                    }
                    if (SaturdayStartTime != "null"){
                        days = days.concat(" Saturday, ");
                        if (timings == ""){
                            String saturdayEndTime = data.getString("OFCD_SaturdayEndTime");
                            timings = timings.concat(SaturdayStartTime + " - "+saturdayEndTime);
                        }
                    }
                    if (SundayStartTime != "null"){
                        days =days.concat(" Sunday, ");
                        if (timings == ""){
                            String sundayEndTime = data.getString("OFCD_SundayEndTime");
                            timings = timings.concat(SundayStartTime + " - "+sundayEndTime);
                        }
                    }
                    PracticeDetails doctorObj = new PracticeDetails(HospitalName,days, timings, Fees);
                    userList.add(doctorObj);
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON response: " + e.getMessage());
        }

        return userList;
    }
}