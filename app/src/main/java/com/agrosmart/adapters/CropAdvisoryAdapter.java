package com.agrosmart.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.databinding.ItemCropAdvisoryBinding;
import com.agrosmart.models.CropAdvisory;

import java.util.ArrayList;
import java.util.List;

public class CropAdvisoryAdapter extends RecyclerView.Adapter<CropAdvisoryAdapter.ViewHolder> {

    private List<CropAdvisory> items = new ArrayList<>();

    public CropAdvisoryAdapter() {}

    public void setItems(List<CropAdvisory> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCropAdvisoryBinding binding = ItemCropAdvisoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCropAdvisoryBinding binding;

        ViewHolder(ItemCropAdvisoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CropAdvisory item) {
            binding.tvAdvisoryTitle.setText(item.getTitle() != null ? item.getTitle() : "Agricultural Advisory");

            String source = item.getSourceName() != null ? item.getSourceName() : "Official University Advisory";
            if (source.contains("PDKV")) {
                binding.tvAdvisorySourceBadge.setText("🎓 Dr. PDKV Akola");
                binding.tvAdvisorySourceBadge.setTextColor(Color.parseColor("#1E40AF"));
            } else if (source.contains("CROPSAP")) {
                binding.tvAdvisorySourceBadge.setText("🏛️ CROPSAP Maharashtra");
                binding.tvAdvisorySourceBadge.setTextColor(Color.parseColor("#15803D"));
            } else {
                binding.tvAdvisorySourceBadge.setText("📋 State Advisory");
                binding.tvAdvisorySourceBadge.setTextColor(Color.parseColor("#4B5563"));
            }

            binding.tvAdvisoryCoverage.setText(item.getLocationCoverage() != null ? item.getLocationCoverage() : "Maharashtra");

            String cropInfo = "🌱 " + (item.getCropName() != null ? item.getCropName() : "Crop");
            if (item.getVariety() != null && !item.getVariety().isEmpty()) {
                cropInfo += " • Variety: " + item.getVariety();
            }
            binding.tvAdvisoryCropAndVariety.setText(cropInfo);

            String catAndVal = "Category: " + (item.getProblemCategory() != null ? item.getProblemCategory() : "General");
            if (item.getValidityPeriod() != null) {
                catAndVal += " • " + item.getValidityPeriod();
            }
            binding.tvAdvisoryCategoryAndValidity.setText(catAndVal);

            binding.tvAdvisoryTextEnglish.setText(item.getAdvisoryText() != null ? item.getAdvisoryText() : "");

            if (item.getAdvisoryTextMarathi() != null && !item.getAdvisoryTextMarathi().isEmpty()) {
                binding.tvAdvisoryTextMarathi.setVisibility(View.VISIBLE);
                binding.tvAdvisoryTextMarathi.setText(item.getAdvisoryTextMarathi());
            } else {
                binding.tvAdvisoryTextMarathi.setVisibility(View.GONE);
            }

            String citation = "Source: " + source;
            if (item.getPublicationDate() != null) {
                citation += " (" + item.getPublicationDate() + ")";
            }
            binding.tvAdvisoryCitation.setText(citation);

            final String portalUrl = item.getSourceUrl();
            if (portalUrl != null && !portalUrl.isEmpty()) {
                binding.btnOpenOfficialPortal.setVisibility(View.VISIBLE);
                binding.btnOpenOfficialPortal.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(portalUrl));
                        v.getContext().startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(v.getContext(), "Could not open link: " + portalUrl, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                binding.btnOpenOfficialPortal.setVisibility(View.GONE);
            }
        }
    }
}
