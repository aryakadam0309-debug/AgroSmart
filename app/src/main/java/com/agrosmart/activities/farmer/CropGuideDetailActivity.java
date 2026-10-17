package com.agrosmart.activities.farmer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.agrosmart.activities.BaseActivity;
import com.agrosmart.databinding.ActivityCropGuideDetailBinding;
import com.agrosmart.models.CropGuide;

public class CropGuideDetailActivity extends BaseActivity {

    private ActivityCropGuideDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCropGuideDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbarDetail.setNavigationOnClickListener(v -> finish());

        CropGuide guide = (CropGuide) getIntent().getSerializableExtra("EXTRA_CROP_GUIDE");
        if (guide != null) {
            bindData(guide);
        }
    }

    private void bindData(CropGuide guide) {
        binding.detailCropName.setText(guide.getCropName());
        binding.detailScientificName.setText(guide.getScientificName());
        binding.detailQuickStats.setText(String.format("Season: %s • Duration: %s • Yield: %s",
                guide.getSeason(), guide.getDuration(), guide.getExpectedYield()));

        binding.detailSoil.setText("🌱 Soil: " + guide.getSoilType());
        binding.detailClimate.setText("🌡️ Optimal Temperature: " + guide.getTemperature());
        binding.detailWater.setText("💧 Water Need: " + guide.getWaterRequirement());

        binding.detailGrowthStages.setText(guide.getGrowthStages());
        binding.detailNpk.setText("🧪 Fertilizer Advisory: " + guide.getNpkAdvisory());
        binding.detailBestPractices.setText("💡 Agronomic Advisory: " + guide.getBestPractices());
    }
}
