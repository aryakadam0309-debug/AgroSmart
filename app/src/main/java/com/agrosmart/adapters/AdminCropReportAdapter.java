package com.agrosmart.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.databinding.ItemAdminCropReportBinding;
import com.agrosmart.models.DiseaseReport;

import java.util.ArrayList;
import java.util.List;

public class AdminCropReportAdapter extends RecyclerView.Adapter<AdminCropReportAdapter.ViewHolder> {

    private List<DiseaseReport> items = new ArrayList<>();
    private final OnTriageClickListener listener;

    public interface OnTriageClickListener {
        void onTriageClick(DiseaseReport report);
    }

    public AdminCropReportAdapter(OnTriageClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<DiseaseReport> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminCropReportBinding binding = ItemAdminCropReportBinding.inflate(
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
        private final ItemAdminCropReportBinding binding;

        ViewHolder(ItemAdminCropReportBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(DiseaseReport item) {
            String cropTitle = item.getCropName() != null ? item.getCropName() : "Crop";
            if (item.getVariety() != null && !item.getVariety().isEmpty()) {
                cropTitle += " (" + item.getVariety() + ")";
            }
            binding.tvAdminCropName.setText(cropTitle);

            String farmerInfo = "Farmer: " + (item.getFarmerName() != null ? item.getFarmerName() : "Local Farmer") +
                    " • " + (item.getFarmerPhone() != null ? item.getFarmerPhone() : "No Contact");
            binding.tvAdminFarmerDetails.setText(farmerInfo);

            String status = item.getStatus() != null ? item.getStatus() : "PENDING_REVIEW";
            if ("DUPLICATE".equalsIgnoreCase(status) && item.getDuplicateOfReportId() != null) {
                binding.tvAdminStatusBadge.setText("DUPLICATE OF #" + item.getDuplicateOfReportId());
            } else {
                binding.tvAdminStatusBadge.setText(status.replace("_", " "));
            }

            if ("VERIFIED".equalsIgnoreCase(status) || "VERIFIED_ALERT".equalsIgnoreCase(status)) {
                binding.tvAdminStatusBadge.setTextColor(Color.parseColor("#15803D"));
            } else if ("RESOLVED".equalsIgnoreCase(status)) {
                binding.tvAdminStatusBadge.setTextColor(Color.parseColor("#0F766E"));
            } else if ("UNDER_REVIEW".equalsIgnoreCase(status)) {
                binding.tvAdminStatusBadge.setTextColor(Color.parseColor("#1D4ED8"));
            } else if ("NOT_VERIFIED".equalsIgnoreCase(status) || "DUPLICATE".equalsIgnoreCase(status)) {
                binding.tvAdminStatusBadge.setTextColor(Color.parseColor("#6B7280"));
            } else {
                binding.tvAdminStatusBadge.setTextColor(Color.parseColor("#D97706"));
            }

            binding.tvAdminCategoryBadge.setText(item.getProblemCategory() != null ? item.getProblemCategory() : "Reported Problem");
            binding.tvAdminGrowthStage.setText(item.getGrowthStage() != null ? item.getGrowthStage() : "Stage: N/A");

            StringBuilder locBuilder = new StringBuilder();
            if (item.getVillage() != null && !item.getVillage().isEmpty()) locBuilder.append(item.getVillage()).append(", ");
            if (item.getTaluka() != null && !item.getTaluka().isEmpty() && !item.getTaluka().equalsIgnoreCase(item.getVillage())) locBuilder.append(item.getTaluka()).append(", ");
            if (item.getDistrict() != null && !item.getDistrict().isEmpty()) locBuilder.append(item.getDistrict());
            String loc = locBuilder.toString();
            binding.tvAdminLocation.setText("📍 " + (loc.isEmpty() ? "Local Area" : loc));

            binding.tvAdminSymptoms.setText(item.getSymptoms() != null ? item.getSymptoms() : "No details.");
            binding.tvAdminReportId.setText("ID: " + (item.getId() != null ? item.getId() : "N/A"));
            binding.tvAdminReportDate.setText(item.getReportedAt() != null ? item.getReportedAt() : "Recent");

            boolean hasPreview = (item.getAdminGuidance() != null && !item.getAdminGuidance().isEmpty()) ||
                    (item.getReviewNote() != null && !item.getReviewNote().isEmpty());
            if (hasPreview) {
                binding.layoutAdminAdvisoryPreview.setVisibility(View.VISIBLE);
                binding.tvAdminAdvisoryText.setText("Guidance: " + (item.getAdminGuidance() != null ? item.getAdminGuidance() : ""));
                binding.tvAdminReviewNoteText.setText("Note: " + (item.getReviewNote() != null ? item.getReviewNote() : ""));
            } else {
                binding.layoutAdminAdvisoryPreview.setVisibility(View.GONE);
            }

            binding.btnTriageReport.setOnClickListener(v -> {
                if (listener != null) listener.onTriageClick(item);
            });
        }
    }
}
