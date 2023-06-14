package com.csharpui.myloginform;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private DrawerLayout drawerLayout;
    private ImageButton customBtn;
    private HashMap<String, String> userDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        userDetails = new HashMap<>();

        sharedPreferences = getSharedPreferences("LoginFile", MODE_PRIVATE);
        String userData = sharedPreferences.getString("userDetails", "");
        editor = sharedPreferences.edit();
//        Toolbar toolbar = findViewById(R.id.toolbar); //Ignore red line errors
//        setSupportActionBar(toolbar);

        customBtn = findViewById(R.id.custom_button);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        customBtn.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public void onBackPressed() {
        // Check if the user is logged in
        sharedPreferences = getSharedPreferences("LoginFile", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getString("isLoggedIn","").equals("true");

        if (isLoggedIn) {
            // User is logged in, prevent going back to LoginActivity
            moveTaskToBack(true); // Move the task to the back of the activity stack
        } else {
            super.onBackPressed(); // Allow going back to LoginActivity if not logged in
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                break;
            case R.id.nav_share:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ShareFragment()).commit();
                break;
            case R.id.nav_about :
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DoctorList()).commit();
                break;
            case R.id.nav_logout:
                Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show();
                logout();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logout() {
        editor.putString("isLoggedIn","false");
        editor.commit();
        startActivity(new Intent(Dashboard.this, LoginActivity1.class));
        finishAffinity();
    }
}