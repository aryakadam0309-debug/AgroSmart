package com.agrosmart.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.databinding.ItemCropAlertBinding;
import com.agrosmart.models.CropAlert;

import java.util.ArrayList;
import java.util.List;

public class CropAlertAdapter extends RecyclerView.Adapter<CropAlertAdapter.ViewHolder> {

    private List<CropAlert> items = new ArrayList<>();
    private final OnAlertClickListener listener;

    public interface OnAlertClickListener {
        void onAlertClick(CropAlert alert);
    }

    public CropAlertAdapter(OnAlertClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<CropAlert> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCropAlertBinding binding = ItemCropAlertBinding.inflate(
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
        private final ItemCropAlertBinding binding;

        ViewHolder(ItemCropAlertBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CropAlert item) {
            binding.tvAlertTitle.setText(item.getAlertTitle() != null ? item.getAlertTitle() : "Community Crop Alert");
            binding.tvAlertSummary.setText(item.getSummary() != null ? item.getSummary() : "");

            // Severity Badge
            String sev = item.getSeverity() != null ? item.getSeverity().toUpperCase() : "ADVISORY";
            binding.tvSeverityBadge.setText("🚨 " + sev);
            if ("HIGH".equals(sev) || "SEVERE".equals(sev)) {
                binding.tvSeverityBadge.setTextColor(Color.parseColor("#DC2626"));
            } else if ("MODERATE".equals(sev)) {
                binding.tvSeverityBadge.setTextColor(Color.parseColor("#D97706"));
            } else {
                binding.tvSeverityBadge.setTextColor(Color.parseColor("#2563EB"));
            }

            // Cluster report count
            if (item.getReportCount() > 0) {
                binding.tvClusterCountBadge.setVisibility(View.VISIBLE);
                binding.tvClusterCountBadge.setText(item.getReportCount() + " Verified Reports");
            } else {
                binding.tvClusterCountBadge.setVisibility(View.GONE);
            }

            // Status
            String status = item.getStatus() != null ? item.getStatus() : "ACTIVE";
            binding.tvAlertStatus.setText(status);
            if ("ACTIVE".equalsIgnoreCase(status)) {
                binding.tvAlertStatus.setTextColor(Color.parseColor("#15803D"));
            } else {
                binding.tvAlertStatus.setTextColor(Color.parseColor("#6B7280"));
            }

            // Crop + Region
            String loc = (item.getVillage() != null && !item.getVillage().isEmpty() ? item.getVillage() + ", " : "") +
                    (item.getDistrict() != null ? item.getDistrict() : "District");
            binding.tvCropAndRegion.setText("🌱 " + (item.getCropName() != null ? item.getCropName() : "Crop") + " • 📍 " + loc);
            binding.tvPublishedDate.setText(item.getPublishedAt() != null ? item.getPublishedAt() : "Recent");

            // Recommended actions
            if (item.getRecommendedActions() != null && !item.getRecommendedActions().isEmpty()) {
                binding.tvRecommendedActions.setText(item.getRecommendedActions());
            } else {
                binding.tvRecommendedActions.setText("Field agronomist evaluation ongoing. Observe crop regularly and report any rapid symptom spread.");
            }

            // Authority
            binding.tvAuthority.setText("🏛️ Verified Authority: " + (item.getAuthority() != null ? item.getAuthority() : "Official Agronomy Cell"));

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onAlertClick(item);
            });
        }
    }
}
