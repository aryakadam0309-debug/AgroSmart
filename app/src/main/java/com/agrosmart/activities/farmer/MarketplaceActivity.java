package com.agrosmart.activities.farmer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.adapters.MarketProductAdapter;
import com.agrosmart.databinding.ActivityMarketplaceBinding;
import com.agrosmart.viewmodels.MarketplaceViewModel;
import com.google.android.material.chip.Chip;

public class MarketplaceActivity extends AppCompatActivity {

    private ActivityMarketplaceBinding binding;
    private MarketplaceViewModel viewModel;
    private MarketProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMarketplaceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MarketplaceViewModel.class);

        binding.toolbarMarket.setNavigationOnClickListener(v -> finish());

        setupRecyclerView();
        setupFilters();
        observeViewModel();

        binding.fabViewCart.setOnClickListener(v -> {
            startActivity(new Intent(this, CartActivity.class));
        });
    }

    private void setupRecyclerView() {
        adapter = new MarketProductAdapter(product -> {
            viewModel.addToCart(product);
            Toast.makeText(this, product.getName() + " added to Cart!", Toast.LENGTH_SHORT).show();
        });
        binding.rvMarketProducts.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMarketProducts.setAdapter(adapter);
    }

    private void setupFilters() {
        binding.etSearchMarket.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.search(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        binding.chipGroupMarket.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                viewModel.filterByCategory("All Products");
                return;
            }
            int id = checkedIds.get(0);
            Chip chip = findViewById(id);
            if (chip != null) {
                viewModel.filterByCategory(chip.getText().toString());
            }
        });
    }

    private void observeViewModel() {
        viewModel.getProducts().observe(this, list -> {
            adapter.setItems(list);
        });

        viewModel.getCart().observe(this, cart -> {
            int totalItems = 0;
            if (cart != null) {
                for (com.agrosmart.models.CartItem ci : cart) {
                    totalItems += ci.getQuantity();
                }
            }
            binding.fabViewCart.setText("Cart (" + totalItems + ")");
        });
    }
}
