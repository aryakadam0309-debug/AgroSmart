package com.agrosmart.activities.admin;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.activities.BaseActivity;
import com.agrosmart.adapters.AdminFeedbackAdapter;
import com.agrosmart.databinding.ActivityAdminFeedbackBinding;
import com.agrosmart.models.FarmerFeedback;
import com.agrosmart.repository.FeedbackRepository;

import java.util.List;

public class AdminFeedbackActivity extends BaseActivity {

    private ActivityAdminFeedbackBinding binding;
    private FeedbackRepository repository;
    private AdminFeedbackAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminFeedbackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = FeedbackRepository.getInstance(this);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        adapter = new AdminFeedbackAdapter();
        binding.rvAdminFeedback.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAdminFeedback.setAdapter(adapter);

        loadFeedback();
    }

    private void loadFeedback() {
        repository.getAllFeedback(new FeedbackRepository.FeedbackCallback() {
            @Override
            public void onLoaded(List<FarmerFeedback> list) {
                adapter.setItems(list);
                if (list == null || list.isEmpty()) {
                    binding.tvEmptyAdminFeedback.setVisibility(View.VISIBLE);
                    binding.rvAdminFeedback.setVisibility(View.GONE);
                } else {
                    binding.tvEmptyAdminFeedback.setVisibility(View.GONE);
                    binding.rvAdminFeedback.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String error) {
                binding.tvEmptyAdminFeedback.setVisibility(View.VISIBLE);
            }
        });
    }
}
