package com.agrosmart.activities.farmer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.agrosmart.databinding.ActivityOrderSuccessBinding;

public class OrderSuccessActivity extends AppCompatActivity {

    private ActivityOrderSuccessBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderSuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String orderId = getIntent().getStringExtra("EXTRA_ORDER_ID");
        double amount = getIntent().getDoubleExtra("EXTRA_AMOUNT", 0.0);
        String method = getIntent().getStringExtra("EXTRA_METHOD");

        binding.tvSuccessOrderId.setText("Order #" + (orderId != null ? orderId : ""));
        binding.tvSuccessSummary.setText(String.format("Total: ₹%,.0f • %s", amount, method != null ? method : ""));

        binding.btnBackToDashboard.setOnClickListener(v -> finish());
    }
}
