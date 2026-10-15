package com.agrosmart.activities.farmer;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.R;
import com.agrosmart.adapters.SchemeAdapter;
import com.agrosmart.databinding.ActivitySchemeBinding;
import com.agrosmart.utils.LocaleHelper;
import com.agrosmart.utils.SessionManager;
import com.agrosmart.viewmodels.SchemeViewModel;

public class SchemeActivity extends AppCompatActivity {

    private ActivitySchemeBinding binding;
    private SchemeViewModel schemeViewModel;
    private SchemeAdapter schemeAdapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        SessionManager session = new SessionManager(newBase);
        super.attachBaseContext(LocaleHelper.setLocale(newBase, session.getLanguage()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySchemeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        schemeViewModel = new ViewModelProvider(this).get(SchemeViewModel.class);

        setupRecyclerView();
        setupObservers();
        setupSearchAndFilters();

        binding.btnBack.setOnClickListener(v -> finish());
        schemeViewModel.filterCategory("All");
    }

    private void setupRecyclerView() {
        schemeAdapter = new SchemeAdapter(this);
        binding.rvSchemes.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSchemes.setAdapter(schemeAdapter);
    }

    private void setupObservers() {
        schemeViewModel.getSchemesList().observe(this, list -> {
            if (list != null) {
                schemeAdapter.setSchemes(list);
            }
        });
    }

    private void setupSearchAndFilters() {
        binding.etSearchScheme.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                schemeViewModel.search(s != null ? s.toString() : "");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.chipGroupSchemeCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipFinancial) {
                schemeViewModel.filterCategory("Financial");
            } else if (id == R.id.chipSubsidy) {
                schemeViewModel.filterCategory("Subsidy");
            } else if (id == R.id.chipInsurance) {
                schemeViewModel.filterCategory("Insurance");
            } else if (id == R.id.chipMachinery) {
                schemeViewModel.filterCategory("Machinery");
            } else {
                schemeViewModel.filterCategory("All");
            }
        });
    }
}
