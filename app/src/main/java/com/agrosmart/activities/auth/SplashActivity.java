package com.agrosmart.activities.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.appcompat.app.AppCompatActivity;

import com.agrosmart.activities.BaseActivity;
import com.agrosmart.databinding.ActivitySplashBinding;
import com.agrosmart.utils.Constants;
import com.agrosmart.utils.SessionManager;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {

    private ActivitySplashBinding binding;
    private SessionManager sessionManager;
    private static final int SPLASH_DURATION_MS = 2200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        // Gentle fade-in animation for branding
        Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1200);
        binding.ivLogo.startAnimation(fadeIn);
        binding.tvAppName.startAnimation(fadeIn);
        binding.tvTagline.startAnimation(fadeIn);

        // Transition timer
        new Handler(Looper.getMainLooper()).postDelayed(this::routeUserToNextScreen, SPLASH_DURATION_MS);
    }

    private void routeUserToNextScreen() {
        if (!sessionManager.isLanguageSelected()) {
            // First run or language not yet selected -> Go to Language Selection
            startActivity(new Intent(SplashActivity.this, LanguageSelectionActivity.class));
        } else if (!sessionManager.isLoggedIn()) {
            // Language set, but user not logged in -> Go to Login
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        } else {
            // User logged in: Route according to Role
            String role = sessionManager.getUserRole();
            if (Constants.ROLE_ADMIN.equalsIgnoreCase(role)) {
                // To Admin Dashboard
                try {
                    Class<?> adminClass = Class.forName("com.agrosmart.activities.admin.AdminDashboardActivity");
                    startActivity(new Intent(SplashActivity.this, adminClass));
                } catch (ClassNotFoundException e) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
            } else {
                // To Farmer Dashboard
                try {
                    Class<?> farmerClass = Class.forName("com.agrosmart.activities.farmer.FarmerDashboardActivity");
                    startActivity(new Intent(SplashActivity.this, farmerClass));
                } catch (ClassNotFoundException e) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
            }
        }
        finish(); // Close SplashActivity so user cannot press back into splash
    }
}
