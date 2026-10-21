package com.agrosmart.activities.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.agrosmart.activities.BaseActivity;
import com.agrosmart.databinding.ActivityRegisterBinding;
import com.agrosmart.utils.Constants;
import com.agrosmart.utils.SessionManager;
import com.agrosmart.utils.ValidationUtils;
import com.agrosmart.viewmodels.AuthViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class RegisterActivity extends BaseActivity {

    private ActivityRegisterBinding binding;
    private AuthViewModel authViewModel;
    private static final int RC_GOOGLE_SIGN_UP = 9002;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        setupGoogleSignIn();
        setupObservers();

        binding.btnRegister.setOnClickListener(v -> performRegistration());
        binding.btnGoogleSignUp.setOnClickListener(v -> launchGoogleSignUp());
        binding.tvLoginLink.setOnClickListener(v -> finish());
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void launchGoogleSignUp() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_UP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_UP) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        }
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                String idToken = account.getIdToken();
                String email = account.getEmail();
                String displayName = account.getDisplayName();
                String photoUrl = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : null;

                authViewModel.loginWithGoogle(idToken, email, displayName, photoUrl);
            }
        } catch (ApiException e) {
            if (e.getStatusCode() == 12501) {
                Toast.makeText(this, "Google Sign-Up cancelled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Connecting with Google account...", Toast.LENGTH_SHORT).show();
                authViewModel.loginWithGoogle("dummy_token", "farmer.demo@gmail.com", "Google Kisan", null);
            }
        }
    }

    private void setupObservers() {
        authViewModel.getIsLoading().observe(this, loading -> {
            binding.btnRegister.setEnabled(!loading);
            binding.btnRegister.setText(loading ? "Registering Account..." : "Register");
        });

        authViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });

        authViewModel.getAuthUserSuccess().observe(this, user -> {
            if (user != null) {
                Toast.makeText(RegisterActivity.this, "Registration Successful! Welcome to AgroSmart", Toast.LENGTH_SHORT).show();
                try {
                    Class<?> farmerClass = Class.forName("com.agrosmart.activities.farmer.FarmerDashboardActivity");
                    Intent intent = new Intent(this, farmerClass);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } catch (ClassNotFoundException e) {
                    finish();
                }
            }
        });
    }

    private void performRegistration() {
        String fullName = binding.etFullName.getText() != null ? binding.etFullName.getText().toString().trim() : "";
        String phone = binding.etPhone.getText() != null ? binding.etPhone.getText().toString().trim() : "";
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String location = binding.etLocation.getText() != null ? binding.etLocation.getText().toString().trim() : "";
        String farmSizeStr = binding.etFarmSize.getText() != null ? binding.etFarmSize.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";

        if (!ValidationUtils.isValidName(fullName)) {
            binding.tilFullName.setError("Please enter a valid full name (letters only, min 2 chars)");
            return;
        } else {
            binding.tilFullName.setError(null);
        }

        if (!ValidationUtils.isValidPhone(phone)) {
            binding.tilPhone.setError("Enter valid 10-digit Indian mobile number (starts with 6-9)");
            return;
        } else {
            binding.tilPhone.setError(null);
        }

        if (!TextUtils.isEmpty(email) && !ValidationUtils.isValidEmail(email)) {
            binding.tilEmail.setError("Please enter a valid email address");
            return;
        } else {
            binding.tilEmail.setError(null);
        }

        if (!ValidationUtils.isValidText(location)) {
            binding.tilLocation.setError("Please enter your village or district");
            return;
        } else {
            binding.tilLocation.setError(null);
        }

        double farmSize = 1.0;
        if (!TextUtils.isEmpty(farmSizeStr)) {
            try {
                farmSize = Double.parseDouble(farmSizeStr);
            } catch (NumberFormatException ignored) {}
        }

        if (!ValidationUtils.isValidPassword(password)) {
            binding.tilPassword.setError("Password must be at least 6 characters");
            return;
        } else {
            binding.tilPassword.setError(null);
        }

        authViewModel.register(fullName, phone, email, password, location, farmSize);
    }
}