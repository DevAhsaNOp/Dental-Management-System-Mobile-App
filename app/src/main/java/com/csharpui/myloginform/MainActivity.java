package com.csharpui.myloginform;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 5000;
    View first,second,third,fourth,fifth,sixth;
    TextView slogan;
    ImageView dmsLogo;
    //Animations
    Animation topAnimantion,bottomAnimation,middleAnimation;
    private boolean isConnected() {
        boolean res = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            res = true;
        }
        return res;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isConnected()){
            Intent intent = new Intent(MainActivity.this, NetworkErrorActivity.class);
            startActivity(intent);
        }
        else{
            setContentView(R.layout.activity_main);
            //Hooks
            first = findViewById(R.id.first_line);
            second = findViewById(R.id.second_line);
            third = findViewById(R.id.third_line);
            fourth = findViewById(R.id.fourth_line);
            fifth = findViewById(R.id.fifth_line);
            sixth = findViewById(R.id.sixth_line);
            dmsLogo = findViewById(R.id.dmsLogo);
            slogan = findViewById(R.id.tagLine);
            //Animation Calls
            topAnimantion = AnimationUtils.loadAnimation(this, R.anim.top_animantion);
            bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animantion);
            middleAnimation = AnimationUtils.loadAnimation(this, R.anim.middle_animation);
            //-----------Setting Animations to the elements of SplashScreen-------- -
            first.setAnimation(topAnimantion);
            second.setAnimation(topAnimantion);
            third.setAnimation(topAnimantion);
            fourth.setAnimation(topAnimantion);
            fifth.setAnimation(topAnimantion);
            sixth.setAnimation(topAnimantion);
            dmsLogo.setAnimation(middleAnimation);
            slogan.setAnimation(bottomAnimation);
            //Splash Screen Code to call new Activity after some time
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Calling new Activity
                    Intent intent = new Intent(MainActivity.this, LoginActivity1.class);
                    startActivity(intent);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }


    }
}