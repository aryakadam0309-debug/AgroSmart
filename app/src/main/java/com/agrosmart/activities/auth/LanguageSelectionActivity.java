package com.agrosmart.activities.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.agrosmart.R;
import com.agrosmart.activities.BaseActivity;
import com.agrosmart.activities.admin.AdminDashboardActivity;
import com.agrosmart.activities.farmer.FarmerDashboardActivity;
import com.agrosmart.databinding.ActivityLanguageSelectionBinding;
import com.agrosmart.utils.Constants;
import com.agrosmart.utils.LocaleHelper;
import com.agrosmart.utils.SessionManager;

public class LanguageSelectionActivity extends BaseActivity {

    private ActivityLanguageSelectionBinding binding;
    private SessionManager sessionManager;
    private String selectedLanguage = Constants.LANG_ENGLISH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLanguageSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        selectedLanguage = sessionManager.getLanguage();

        updateSelectionUI(selectedLanguage);

        binding.cardEnglish.setOnClickListener(v -> {
            selectedLanguage = Constants.LANG_ENGLISH;
            updateSelectionUI(selectedLanguage);
        });

        binding.cardHindi.setOnClickListener(v -> {
            selectedLanguage = Constants.LANG_HINDI;
            updateSelectionUI(selectedLanguage);
        });

        binding.cardMarathi.setOnClickListener(v -> {
            selectedLanguage = Constants.LANG_MARATHI;
            updateSelectionUI(selectedLanguage);
        });

        binding.btnContinue.setOnClickListener(v -> {
            sessionManager.setLanguage(selectedLanguage);
            LocaleHelper.setLocale(this, selectedLanguage);

            Intent intent;
            if (sessionManager.isLoggedIn()) {
                if (Constants.ROLE_ADMIN.equals(sessionManager.getUserRole())) {
                    intent = new Intent(LanguageSelectionActivity.this, AdminDashboardActivity.class);
                } else {
                    intent = new Intent(LanguageSelectionActivity.this, FarmerDashboardActivity.class);
                }
            } else {
                intent = new Intent(LanguageSelectionActivity.this, LoginActivity.class);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void updateSelectionUI(String lang) {
        int activeBorder = ContextCompat.getColor(this, R.color.forest_green_primary);
        int inactiveBorder = ContextCompat.getColor(this, R.color.divider_line);

        binding.rbEnglish.setChecked(Constants.LANG_ENGLISH.equals(lang));
        binding.cardEnglish.setStrokeColor(Constants.LANG_ENGLISH.equals(lang) ? activeBorder : inactiveBorder);
        binding.cardEnglish.setStrokeWidth(Constants.LANG_ENGLISH.equals(lang) ? 6 : 2);

        binding.rbHindi.setChecked(Constants.LANG_HINDI.equals(lang));
        binding.cardHindi.setStrokeColor(Constants.LANG_HINDI.equals(lang) ? activeBorder : inactiveBorder);
        binding.cardHindi.setStrokeWidth(Constants.LANG_HINDI.equals(lang) ? 6 : 2);

        binding.rbMarathi.setChecked(Constants.LANG_MARATHI.equals(lang));
        binding.cardMarathi.setStrokeColor(Constants.LANG_MARATHI.equals(lang) ? activeBorder : inactiveBorder);
        binding.cardMarathi.setStrokeWidth(Constants.LANG_MARATHI.equals(lang) ? 6 : 2);
    }
}
