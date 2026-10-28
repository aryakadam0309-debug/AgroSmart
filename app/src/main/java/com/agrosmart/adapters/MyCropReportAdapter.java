package com.agrosmart.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.databinding.ItemMyCropReportBinding;
import com.agrosmart.models.DiseaseReport;

import java.util.ArrayList;
import java.util.List;

public class MyCropReportAdapter extends RecyclerView.Adapter<MyCropReportAdapter.ViewHolder> {

    private List<DiseaseReport> items = new ArrayList<>();
    private final OnReportClickListener listener;

    public interface OnReportClickListener {
        void onReportClick(DiseaseReport report);
    }

    public MyCropReportAdapter(OnReportClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<DiseaseReport> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMyCropReportBinding binding = ItemMyCropReportBinding.inflate(
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
        private final ItemMyCropReportBinding binding;

        ViewHolder(ItemMyCropReportBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(DiseaseReport item) {
            String cropTitle = item.getCropName() != null ? item.getCropName() : "Crop";
            if (item.getVariety() != null && !item.getVariety().isEmpty()) {
                cropTitle += " (" + item.getVariety() + ")";
            }
            binding.tvReportCrop.setText(cropTitle);
            binding.tvReportId.setText("Ref: " + (item.getId() != null ? item.getId() : "N/A"));

            // Status Badge
            String status = item.getStatus() != null ? item.getStatus() : "PENDING_REVIEW";
            if ("DUPLICATE".equalsIgnoreCase(status) && item.getDuplicateOfReportId() != null) {
                binding.tvReportStatus.setText("DUPLICATE OF #" + item.getDuplicateOfReportId());
            } else {
                binding.tvReportStatus.setText(status.replace("_", " "));
            }

            if ("VERIFIED".equalsIgnoreCase(status) || "VERIFIED_ALERT".equalsIgnoreCase(status)) {
                binding.tvReportStatus.setTextColor(Color.parseColor("#15803D"));
            } else if ("RESOLVED".equalsIgnoreCase(status)) {
                binding.tvReportStatus.setTextColor(Color.parseColor("#0F766E"));
            } else if ("UNDER_REVIEW".equalsIgnoreCase(status)) {
                binding.tvReportStatus.setTextColor(Color.parseColor("#1D4ED8"));
            } else if ("NOT_VERIFIED".equalsIgnoreCase(status) || "DUPLICATE".equalsIgnoreCase(status)) {
                binding.tvReportStatus.setTextColor(Color.parseColor("#6B7280"));
            } else {
                binding.tvReportStatus.setTextColor(Color.parseColor("#D97706")); // Pending
            }

            // Category & Stage
            binding.tvProblemCategory.setText(item.getProblemCategory() != null && !item.getProblemCategory().isEmpty() ?
                    item.getProblemCategory() : "Reported Problem");
            binding.tvGrowthStage.setText(item.getGrowthStage() != null && !item.getGrowthStage().isEmpty() ?
                    item.getGrowthStage() : "Stage: N/A");

            StringBuilder locBuilder = new StringBuilder();
            if (item.getVillage() != null && !item.getVillage().isEmpty()) locBuilder.append(item.getVillage()).append(", ");
            if (item.getTaluka() != null && !item.getTaluka().isEmpty() && !item.getTaluka().equalsIgnoreCase(item.getVillage())) locBuilder.append(item.getTaluka()).append(", ");
            if (item.getDistrict() != null && !item.getDistrict().isEmpty()) locBuilder.append(item.getDistrict());
            String loc = locBuilder.toString();
            binding.tvLocation.setText("📍 " + (loc.isEmpty() ? "Local Area" : loc));

            binding.tvSymptoms.setText(item.getSymptoms() != null ? item.getSymptoms() : "No extra symptoms recorded.");
            binding.tvReportDate.setText("Reported: " + (item.getReportedAt() != null ? item.getReportedAt() : "Recent"));

            // Advisory / Guidance if reviewed
            boolean hasGuidance = (item.getAdminGuidance() != null && !item.getAdminGuidance().isEmpty()) ||
                    (item.getAdminAdvisory() != null && !item.getAdminAdvisory().isEmpty());

            if (hasGuidance) {
                binding.layoutAdvisory.setVisibility(View.VISIBLE);
                String guidance = item.getAdminGuidance() != null ? item.getAdminGuidance() : item.getAdminAdvisory();
                binding.tvAdvisoryText.setText(guidance);
                binding.tvAdvisorySource.setText(item.getAdminSource() != null && !item.getAdminSource().isEmpty() ?
                        "[" + item.getAdminSource() + "]" : "[Official Agronomy Advisory]");

                if (item.getReviewNote() != null && !item.getReviewNote().isEmpty()) {
                    binding.tvReviewNote.setVisibility(View.VISIBLE);
                    binding.tvReviewNote.setText("Review Note: " + item.getReviewNote());
                } else {
                    binding.tvReviewNote.setVisibility(View.GONE);
                }
            } else {
                binding.layoutAdvisory.setVisibility(View.GONE);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onReportClick(item);
            });
        }
    }
}
