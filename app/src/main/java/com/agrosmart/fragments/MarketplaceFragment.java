package com.agrosmart.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.agrosmart.databinding.FragmentMarketplaceBinding;

public class MarketplaceFragment extends Fragment {

    private FragmentMarketplaceBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMarketplaceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBrowseProducts.setOnClickListener(v -> {
            try {
                Class<?> target = Class.forName("com.agrosmart.activities.farmer.MarketplaceActivity");
                startActivity(new Intent(requireContext(), target));
            } catch (ClassNotFoundException e) {
                Toast.makeText(requireContext(), "Marketplace catalog ready in Phase 20", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
