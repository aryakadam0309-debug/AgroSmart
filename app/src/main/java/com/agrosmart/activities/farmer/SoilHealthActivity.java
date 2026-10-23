package com.agrosmart.activities.farmer;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.activities.BaseActivity;
import com.agrosmart.adapters.SoilRecordAdapter;
import com.agrosmart.databinding.ActivitySoilHealthBinding;
import com.agrosmart.models.SoilHealthRecord;
import com.agrosmart.repository.AdvisoryRepository;
import com.agrosmart.utils.SessionManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SoilHealthActivity extends BaseActivity {

    private ActivitySoilHealthBinding binding;
    private AdvisoryRepository repository;
    private SessionManager sessionManager;
    private SoilRecordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySoilHealthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        repository = AdvisoryRepository.getInstance(this);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        adapter = new SoilRecordAdapter(item -> {
            binding.cardSoilResult.setVisibility(View.VISIBLE);
            binding.tvFertilityBadge.setText(item.getFertilityRating() != null ? item.getFertilityRating() : "BALANCED");
            binding.tvAdvisoryText.setText(item.getAdvisoryNotes() != null ? item.getAdvisoryNotes() : "Optimal nutrient values observed.");
        });

        binding.rvSoilRecords.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSoilRecords.setAdapter(adapter);

        binding.btnAnalyzeSoil.setOnClickListener(v -> evaluateAndSaveSoil());

        loadHistory();
    }

    private void loadHistory() {
        String phone = sessionManager.getUserPhone();
        repository.getSoilRecords(phone, new AdvisoryRepository.SoilCallback() {
            @Override
            public void onLoaded(List<SoilHealthRecord> list) {
                adapter.setItems(list);
                if (list == null || list.isEmpty()) {
                    binding.tvEmptySoilRecords.setVisibility(View.VISIBLE);
                    binding.rvSoilRecords.setVisibility(View.GONE);
                } else {
                    binding.tvEmptySoilRecords.setVisibility(View.GONE);
                    binding.rvSoilRecords.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String error) {
                binding.tvEmptySoilRecords.setVisibility(View.VISIBLE);
            }
        });
    }

    private void evaluateAndSaveSoil() {
        String fieldName = binding.etFieldName.getText().toString().trim();
        if (fieldName.isEmpty()) fieldName = "Main Farm Acre";

        String phStr = binding.etSoilPh.getText().toString().trim();
        String ecStr = binding.etSoilEc.getText().toString().trim();
        String ocStr = binding.etOrganicCarbon.getText().toString().trim();
        String nStr = binding.etNitrogen.getText().toString().trim();
        String pStr = binding.etPhosphorus.getText().toString().trim();
        String kStr = binding.etPotassium.getText().toString().trim();
        String znStr = binding.etZinc.getText().toString().trim();
        String feStr = binding.etIron.getText().toString().trim();

        double ph = phStr.isEmpty() ? 7.0 : Double.parseDouble(phStr);
        double ec = ecStr.isEmpty() ? 0.4 : Double.parseDouble(ecStr);
        double oc = ocStr.isEmpty() ? 0.55 : Double.parseDouble(ocStr);
        double n = nStr.isEmpty() ? 260.0 : Double.parseDouble(nStr);
        double p = pStr.isEmpty() ? 15.0 : Double.parseDouble(pStr);
        double k = kStr.isEmpty() ? 210.0 : Double.parseDouble(kStr);
        double zn = znStr.isEmpty() ? 0.6 : Double.parseDouble(znStr);
        double fe = feStr.isEmpty() ? 4.5 : Double.parseDouble(feStr);

        SoilHealthRecord record = new SoilHealthRecord();
        record.setId("SHC-" + System.currentTimeMillis());
        record.setFarmerPhone(sessionManager.getUserPhone());
        record.setFieldName(fieldName);
        record.setSampleDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        record.setPh(ph);
        record.setElectricalConductivity(ec);
        record.setOrganicCarbon(oc);
        record.setNitrogenKgHa(n);
        record.setPhosphorusKgHa(p);
        record.setPotassiumKgHa(k);
        record.setZincPpm(zn);
        record.setIronPpm(fe);

        repository.saveSoilRecord(record, new AdvisoryRepository.SingleSoilCallback() {
            @Override
            public void onSuccess(SoilHealthRecord saved) {
                binding.cardSoilResult.setVisibility(View.VISIBLE);
                binding.tvFertilityBadge.setText(saved.getFertilityRating() != null ? saved.getFertilityRating() : "ANALYZED");
                binding.tvAdvisoryText.setText(saved.getAdvisoryNotes() != null ? saved.getAdvisoryNotes() : "Optimal nutrient balance.");
                Toast.makeText(SoilHealthActivity.this, "Soil Health Card saved to farm records!", Toast.LENGTH_SHORT).show();
                loadHistory();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(SoilHealthActivity.this, "Error evaluating soil: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
