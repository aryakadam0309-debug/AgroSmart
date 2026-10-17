package com.agrosmart.activities.farmer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.agrosmart.activities.BaseActivity;
import com.agrosmart.databinding.ActivitySchemeDetailBinding;
import com.agrosmart.models.GovernmentScheme;
import com.agrosmart.utils.SessionManager;

public class SchemeDetailActivity extends BaseActivity {

    private ActivitySchemeDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySchemeDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnSchemeDetailBack.setOnClickListener(v -> finish());

        GovernmentScheme scheme = (GovernmentScheme) getIntent().getSerializableExtra("extra_scheme_item");
        if (scheme != null) {
            binding.tvDetailSchemeName.setText(scheme.getSchemeName());
            binding.tvDetailCategoryBadge.setText(scheme.getCategory());
            binding.tvDetailBenefits.setText(scheme.getBenefits());
            binding.tvDetailEligibility.setText(scheme.getEligibility());
            binding.tvDetailDocuments.setText(scheme.getRequiredDocuments());
            binding.tvDetailProcess.setText(scheme.getApplicationProcess());
            binding.tvDetailDates.setText("Important Timeline: " + scheme.getImportantDates());

            binding.btnOpenPortal.setOnClickListener(v -> {
                if (scheme.getOfficialPortalUrl() != null && !scheme.getOfficialPortalUrl().isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(scheme.getOfficialPortalUrl()));
                    startActivity(browserIntent);
                } else {
                    Toast.makeText(this, "Portal URL not available", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
