package com.agrosmart.activities.farmer;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.agrosmart.activities.BaseActivity;
import com.agrosmart.databinding.ActivityIotDeviceSetupBinding;
import com.agrosmart.utils.SessionManager;

public class IotDeviceSetupActivity extends BaseActivity {

    private ActivityIotDeviceSetupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIotDeviceSetupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());
    }
}
