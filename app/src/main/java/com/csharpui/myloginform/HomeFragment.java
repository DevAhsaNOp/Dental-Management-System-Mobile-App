package com.csharpui.myloginform;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment {

    private TextView Username;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private LinearLayout DoctorAppointment, DoctorVideoAppointment, TeethTest;
    private Button BookAppointment;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Username = view.findViewById(R.id.Username);
        TeethTest = view.findViewById(R.id.TeethTest);
        BookAppointment = view.findViewById(R.id.BookAppointment);
        DoctorAppointment = view.findViewById(R.id.DoctorAppointment);
        DoctorVideoAppointment = view.findViewById(R.id.DoctorVideoAppointment);

        sharedPreferences = requireContext().getSharedPreferences("LoginFile", Context.MODE_PRIVATE);

        //Set Nav Menu Item
        Activity activity = getActivity();
        NavigationView navigationView = activity.findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_home);

        String userData = sharedPreferences.getString("userDetails", "");
        editor = sharedPreferences.edit();

        BookAppointment.setOnClickListener(v -> openDoctorList());
        DoctorAppointment.setOnClickListener(v -> openDoctorList());
        DoctorVideoAppointment.setOnClickListener(v -> openDoctorList());
        TeethTest.setOnClickListener(v -> openTeethTest());

        if (sharedPreferences.getString("isLoggedIn", "").equals("true") && userData != null) {
            System.out.println("User Data Received");
            try {
                JSONObject jsonObject = new JSONObject(userData);
                String username = jsonObject.getString("Username");
                Username.setText("Hello, " + username);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println(userData);
        }

        runCodeWithDelay(view);
        return view;
    }

    private void runCodeWithDelay(View view) {
        final Handler handler = new Handler();
        final TypeWriter tw = (TypeWriter) view.findViewById(R.id.searchbtn);

        final boolean[] isFirstText = {true};

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFirstText[0]) {
                    tw.setText("");
                    tw.setCharacterDelay(150);
                    tw.animateText("Search for a Doctor");
                } else {
                    tw.setText("");
                    tw.setCharacterDelay(150);
                    tw.animateText("Search for offered Services");
                }

                isFirstText[0] = !isFirstText[0];
                handler.postDelayed(this, 5000);
            }
        }, 5000);
    }

    private void openDoctorList() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager(); // Replace with getFragmentManager() if you're using the native FragmentManager
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        NavigationView navigationView = ((Dashboard) requireActivity()).findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_about);

        fragmentTransaction.replace(R.id.fragment_container, new DoctorList());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void openTeethTest() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager(); // Replace with getFragmentManager() if you're using the native FragmentManager
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        NavigationView navigationView = ((Dashboard) requireActivity()).findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_share);

        fragmentTransaction.replace(R.id.fragment_container, new ShareFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}