package com.agrosmart.activities.farmer;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.agrosmart.databinding.ActivityIotDeviceSetupBinding;
import com.agrosmart.utils.LocaleHelper;
import com.agrosmart.utils.SessionManager;

public class IotDeviceSetupActivity extends AppCompatActivity {

    private ActivityIotDeviceSetupBinding binding;

    @Override
    protected void attachBaseContext(Context newBase) {
        SessionManager session = new SessionManager(newBase);
        super.attachBaseContext(LocaleHelper.setLocale(newBase, session.getLanguage()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIotDeviceSetupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());
    }
}
