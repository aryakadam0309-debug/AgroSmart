package com.agrosmart.activities.farmer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.agrosmart.activities.BaseActivity;
import com.agrosmart.databinding.ActivityCropDiseaseDetailBinding;
import com.agrosmart.models.CropDisease;

public class CropDiseaseDetailActivity extends BaseActivity {

    private ActivityCropDiseaseDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCropDiseaseDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbarDiseaseDetail.setNavigationOnClickListener(v -> finish());

        CropDisease disease = (CropDisease) getIntent().getSerializableExtra("EXTRA_DISEASE");
        if (disease != null) {
            bindData(disease);
        }
    }

    private void bindData(CropDisease disease) {
        binding.detailDiseaseName.setText(disease.getDiseaseName());
        binding.detailCropAndPathogen.setText(disease.getCropName() + " • Pathogen: " + disease.getCausalOrganism());
        binding.detailConditions.setText("⚠️ Favorable Weather: " + disease.getFavorableConditions());
        binding.detailSymptoms.setText(disease.getSymptoms());
        binding.detailPrevention.setText(disease.getPreventiveMeasures());
        binding.detailOrganic.setText(disease.getOrganicRemedy());
        binding.detailChemical.setText(disease.getChemicalControl());
    }
}
