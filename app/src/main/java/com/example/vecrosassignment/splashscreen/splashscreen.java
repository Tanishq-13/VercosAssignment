package com.example.vecrosassignment.splashscreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vecrosassignment.MainActivity;
import com.example.vecrosassignment.R;

public class splashscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        ImageView gifImageView = findViewById(R.id.gifImageView);
        long delayDuration = 2000;
        getWindow().setStatusBarColor(getResources().getColor(R.color.white));

        // Create a handler to post a delayed action
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the new activity
                startActivity(new Intent(splashscreen.this, MainActivity.class));
                // Finish the current activity to prevent the user from returning to it using the back button
                finish();
            }
        }, delayDuration);
    }
}