package com.agrosmart.activities.admin;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.R;
import com.agrosmart.activities.BaseActivity;
import com.agrosmart.adapters.SchemeAdapter;
import com.agrosmart.databinding.ActivityManageSchemesBinding;
import com.agrosmart.models.GovernmentScheme;
import com.agrosmart.repository.SchemeRepository;
import com.agrosmart.utils.Constants;
import com.agrosmart.utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class ManageSchemesActivity extends BaseActivity {

    private ActivityManageSchemesBinding binding;
    private SchemeAdapter adapter;
    private SchemeRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageSchemesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SessionManager sessionManager = new SessionManager(this);
        if (!Constants.ROLE_ADMIN.equals(sessionManager.getUserRole())) {
            Toast.makeText(this, "Access Denied: Administrator role required.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        repository = SchemeRepository.getInstance();

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        setupRecyclerView();
        loadSchemes();

        binding.fabAddScheme.setOnClickListener(v -> showAddSchemeDialog());
    }

    private void setupRecyclerView() {
        adapter = new SchemeAdapter(this);
        binding.rvSchemes.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSchemes.setAdapter(adapter);
    }

    private void loadSchemes() {
        List<GovernmentScheme> list = repository.getAllSchemes();
        adapter.setSchemes(list);
        binding.tvCountHeader.setText("Active Government Schemes (" + list.size() + ")");
    }

    private void showAddSchemeDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_scheme, null);

        EditText etName = dialogView.findViewById(R.id.etSchemeName);
        EditText etCat = dialogView.findViewById(R.id.etCategory);
        EditText etDesc = dialogView.findViewById(R.id.etDescription);
        EditText etBenefits = dialogView.findViewById(R.id.etBenefits);
        EditText etElig = dialogView.findViewById(R.id.etEligibility);
        EditText etUrl = dialogView.findViewById(R.id.etPortalUrl);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Publish New Government Scheme")
                .setView(dialogView)
                .setPositiveButton("Publish", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String cat = etCat.getText().toString().trim();
                    String desc = etDesc.getText().toString().trim();
                    String benefits = etBenefits.getText().toString().trim();
                    String elig = etElig.getText().toString().trim();
                    String url = etUrl.getText().toString().trim();

                    if (name.isEmpty() || desc.isEmpty()) {
                        Toast.makeText(this, "Scheme name and description are required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    long id = System.currentTimeMillis();
                    GovernmentScheme newScheme = new GovernmentScheme(
                            id,
                            name,
                            cat.isEmpty() ? "Financial" : cat,
                            desc,
                            benefits.isEmpty() ? "Subsidies & Direct Financial Assistance" : benefits,
                            elig.isEmpty() ? "All eligible farmers with valid land records (7/12 extract)" : elig,
                            "1. Aadhaar Card\n2. Land Record (7/12)\n3. Bank Passbook",
                            "Apply online via state portal or visit local Common Service Centre (CSC)",
                            url.isEmpty() ? "https://agricoop.nic.in" : url,
                            "Ongoing state & central scheme",
                            true
                    );

                    repository.addScheme(newScheme);
                    loadSchemes();
                    Toast.makeText(this, "Scheme published successfully! All farmers can view it.", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
