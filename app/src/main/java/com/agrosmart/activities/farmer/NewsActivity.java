package com.agrosmart.activities.farmer;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.R;
import com.agrosmart.activities.BaseActivity;
import com.agrosmart.adapters.NewsAdapter;
import com.agrosmart.databinding.ActivityNewsBinding;
import com.agrosmart.utils.SessionManager;
import com.agrosmart.viewmodels.NewsViewModel;

public class NewsActivity extends BaseActivity {

    private ActivityNewsBinding binding;
    private NewsViewModel newsViewModel;
    private NewsAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        newsViewModel = new ViewModelProvider(this).get(NewsViewModel.class);

        setupRecyclerView();
        setupObservers();
        setupFilters();

        binding.btnBack.setOnClickListener(v -> finish());
        newsViewModel.loadNews("All");
    }

    private void setupRecyclerView() {
        newsAdapter = new NewsAdapter(this);
        binding.rvNews.setLayoutManager(new LinearLayoutManager(this));
        binding.rvNews.setAdapter(newsAdapter);
    }

    private void setupObservers() {
        newsViewModel.getNewsList().observe(this, list -> {
            if (list != null) {
                newsAdapter.setNewsList(list);
            }
        });
    }

    private void setupFilters() {
        binding.chipGroupCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipGovt) {
                newsViewModel.loadNews("Government");
            } else if (id == R.id.chipCrops) {
                newsViewModel.loadNews("Crops");
            } else if (id == R.id.chipMarkets) {
                newsViewModel.loadNews("Markets");
            } else if (id == R.id.chipTech) {
                newsViewModel.loadNews("Agri-Tech");
            } else {
                newsViewModel.loadNews("All");
            }
        });
    }
}
