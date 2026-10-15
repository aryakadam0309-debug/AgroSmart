package com.agrosmart.activities.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.agrosmart.R;
import com.agrosmart.databinding.ActivityLoginBinding;
import com.agrosmart.utils.Constants;
import com.agrosmart.utils.LocaleHelper;
import com.agrosmart.utils.SessionManager;
import com.agrosmart.utils.ValidationUtils;
import com.agrosmart.viewmodels.AuthViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthViewModel authViewModel;
    private String selectedRole = Constants.ROLE_FARMER;

    @Override
    protected void attachBaseContext(Context newBase) {
        SessionManager session = new SessionManager(newBase);
        super.attachBaseContext(LocaleHelper.setLocale(newBase, session.getLanguage()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        setupRoleToggle();
        setupObservers();
        setupClickListeners();
    }

    private void setupRoleToggle() {
        binding.toggleRoleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnRoleAdmin) {
                    selectedRole = Constants.ROLE_ADMIN;
                    binding.tvRegisterLink.setVisibility(View.GONE);
                } else {
                    selectedRole = Constants.ROLE_FARMER;
                    binding.tvRegisterLink.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setupObservers() {
        authViewModel.getIsLoading().observe(this, loading -> {
            binding.btnLogin.setEnabled(!loading);
            binding.btnLogin.setText(loading ? "Authenticating..." : getString(R.string.login));
        });

        authViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });

        authViewModel.getAuthUserSuccess().observe(this, user -> {
            if (user != null) {
                Toast.makeText(LoginActivity.this, "Welcome " + user.getFullName(), Toast.LENGTH_SHORT).show();
                navigateToDashboard(user.getRole());
            }
        });

        authViewModel.getPasswordResetSuccess().observe(this, message -> {
            if (message != null) {
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Password Reset")
                        .setMessage(message)
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

    private void setupClickListeners() {
        binding.btnLogin.setOnClickListener(v -> performLogin());

        binding.tvRegisterLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        binding.tvForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());
    }

    private void performLogin() {
        String identifier = binding.etIdentifier.getText() != null ? binding.etIdentifier.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(identifier)) {
            binding.tilIdentifier.setError("Enter mobile number or email");
            return;
        } else {
            binding.tilIdentifier.setError(null);
        }

        if (!ValidationUtils.isValidPassword(password)) {
            binding.tilPassword.setError("Password must be at least 6 characters");
            return;
        } else {
            binding.tilPassword.setError(null);
        }

        authViewModel.login(identifier, password, selectedRole);
    }

    private void showForgotPasswordDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_forgot_password, null);
        TextInputLayout tilPhone = dialogView.findViewById(R.id.tilResetPhone);
        TextInputEditText etPhone = dialogView.findViewById(R.id.etResetPhone);
        TextInputLayout tilNewPassword = dialogView.findViewById(R.id.tilNewPassword);
        TextInputEditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);

        // Pre-fill phone if entered on main screen
        String currentIdentifier = binding.etIdentifier.getText() != null ? binding.etIdentifier.getText().toString().trim() : "";
        if (ValidationUtils.isValidPhone(currentIdentifier)) {
            etPhone.setText(ValidationUtils.normalizePhone(currentIdentifier));
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.reset_password)
                .setView(dialogView)
                .setPositiveButton("Reset", null)
                .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
                String newPass = etNewPassword.getText() != null ? etNewPassword.getText().toString().trim() : "";

                if (!ValidationUtils.isValidPhone(phone)) {
                    tilPhone.setError("Enter valid 10-digit mobile number");
                    return;
                } else {
                    tilPhone.setError(null);
                }

                if (!ValidationUtils.isValidPassword(newPass)) {
                    tilNewPassword.setError("Password must be at least 6 characters");
                    return;
                } else {
                    tilNewPassword.setError(null);
                }

                authViewModel.resetPassword(phone, newPass);
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void navigateToDashboard(String role) {
        Intent intent;
        if (Constants.ROLE_ADMIN.equals(role)) {
            try {
                Class<?> adminClass = Class.forName("com.agrosmart.activities.admin.AdminDashboardActivity");
                intent = new Intent(this, adminClass);
            } catch (ClassNotFoundException e) {
                Toast.makeText(this, "Admin Dashboard in Phase 10", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            try {
                Class<?> farmerClass = Class.forName("com.agrosmart.activities.farmer.FarmerDashboardActivity");
                intent = new Intent(this, farmerClass);
            } catch (ClassNotFoundException e) {
                Toast.makeText(this, "Farmer Dashboard in Phase 9", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
