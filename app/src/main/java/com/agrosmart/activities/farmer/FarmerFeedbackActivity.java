package com.agrosmart.activities.farmer;

import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.agrosmart.activities.BaseActivity;
import com.agrosmart.databinding.ActivityFarmerFeedbackBinding;
import com.agrosmart.models.FarmerFeedback;
import com.agrosmart.repository.FeedbackRepository;
import com.agrosmart.utils.SessionManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FarmerFeedbackActivity extends BaseActivity {

    private ActivityFarmerFeedbackBinding binding;
    private FeedbackRepository repository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFarmerFeedbackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        repository = FeedbackRepository.getInstance(this);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        String meta = "Device: " + Build.MANUFACTURER + " " + Build.MODEL + " (Android " + Build.VERSION.RELEASE + ") • App v1.0";
        binding.tvDeviceMeta.setText(meta);

        binding.btnSubmitFeedback.setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {
        int rating = (int) binding.ratingBar.getRating();
        String category = binding.spnFeedbackCategory.getSelectedItem() != null ?
                binding.spnFeedbackCategory.getSelectedItem().toString() : "General";
        String comments = binding.etFeedbackComments.getText().toString().trim();

        if (comments.isEmpty()) {
            Toast.makeText(this, "Please write your suggestions or comments", Toast.LENGTH_SHORT).show();
            return;
        }

        FarmerFeedback feedback = new FarmerFeedback();
        feedback.setId("FB-" + System.currentTimeMillis());
        feedback.setFarmerPhone(sessionManager.getUserPhone());
        feedback.setFarmerName(sessionManager.getUserName());
        feedback.setRating(rating);
        feedback.setCategory(category);
        feedback.setComments(comments);
        feedback.setAppVersion("AgroSmart 1.0 (Android " + Build.VERSION.RELEASE + ")");
        feedback.setSubmittedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));

        repository.submitFeedback(feedback, new FeedbackRepository.SingleFeedbackCallback() {
            @Override
            public void onSuccess(FarmerFeedback result) {
                Toast.makeText(FarmerFeedbackActivity.this, "Thank you! Your feedback has been recorded.", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(FarmerFeedbackActivity.this, "Error saving feedback: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
