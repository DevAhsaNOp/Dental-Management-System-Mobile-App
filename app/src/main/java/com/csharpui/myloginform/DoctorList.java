package com.csharpui.myloginform;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DoctorList extends Fragment implements MyAdapter.OnItemClickListener {

    private static final String TAG = DoctorList.class.getSimpleName();
    TextView searchEditText;
    List<Doctor> userList = new ArrayList<>();
    ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<Doctor> apiResponseData;
    private Context context;
    private RequestQueue requestQueue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doctor_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        apiResponseData = new ArrayList<>();

        //Set Nav Menu Item
        Activity activity = getActivity();
        NavigationView navigationView = activity.findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_about);

        // Initialize the RequestQueue
        requestQueue = Volley.newRequestQueue(getContext());

        // Make the API request
        getDoctorDetails();

        EditText searchEditText = view.findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<Doctor> doctorlist = filter(s.toString());
                adapter.setData(doctorlist);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    private List<Doctor> filter(String query) {
        query = query.toLowerCase();
        List<Doctor> filteredList = new ArrayList<>();

        for (Doctor doctor : userList) {
            if (doctor.getName().toLowerCase().contains(query)) {
                filteredList.add(doctor);
            }
        }

        return filteredList;
    }

    private void getDoctorDetails() {
        String url = "https://dmswebapi.bsite.net/api/Get/AllDoctors";
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        requestQueue.add(new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "API Response: " + response.toString());
                        apiResponseData = parseResponse(response);
                        adapter = new MyAdapter(apiResponseData, DoctorList.this);
                        recyclerView.setAdapter(adapter);
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching API response: " + error.getMessage());
                        Toast.makeText(getContext(), "An error occurred, please try again later!",
                                Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }));
    }

    private List<Doctor> parseResponse(JSONObject response) {


        try {
            String statusCode = response.getString("StatusCode");
            if (statusCode.equals("200")) {

                JSONArray userObject = response.getJSONArray("Datalist");
                for (int i = 0; i < userObject.length(); i++) {
                    JSONObject doctor = userObject.getJSONObject(i);
                    String did = doctor.getString("D_ID");
                    String firstName = doctor.getString("D_FirstName");
                    String lastName = doctor.getString("D_LastName");
                    String avatar = doctor.getString("D_ProfileImage");
                    String aboutme = doctor.getString("D_AboutMe");
                    String specialization = doctor.getString("D_Specialization");
                    String yearOfExperience = doctor.getString("D_YearsOfExperience");
                    String phoneno = doctor.getString("D_PhoneNumber");
                    String email = doctor.getString("D_Email");
                    Doctor doctorObj = new Doctor(did, avatar, firstName, lastName, aboutme, specialization, yearOfExperience, phoneno, email);
                    userList.add(doctorObj);
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON response: " + e.getMessage());
        }

        return userList;
    }

    @Override
    public void onItemClick(int position) {
        Doctor item = apiResponseData.get(position);
        String itemId = item.getID();
        String name = item.getName();
        String aboutme = item.getAboutme();
        String Specialization = item.getSpecializtion();
        String YOE = item.getYearOfExperience();
        String avatar = item.getImageUrl();
        String phone = item.getPhone();
        String email = item.getEmail();
        // Perform any desired action with the clicked item
        DoctorProfileFragment doctorProfileFragment = new DoctorProfileFragment();

        Bundle bundle = new Bundle();
        bundle.putString("doctorName", name);
        bundle.putString("doctorID", itemId);
        bundle.putString("doctorAbout", aboutme);
        bundle.putString("doctorSpecial", Specialization);
        bundle.putString("doctorYoE", YOE);
        bundle.putString("doctorAvatar", avatar);
        bundle.putString("doctorPhone", phone);
        bundle.putString("doctorEmail", email);

        doctorProfileFragment.setArguments(bundle);

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, doctorProfileFragment)
                .addToBackStack(null)
                .commit();
    }
}