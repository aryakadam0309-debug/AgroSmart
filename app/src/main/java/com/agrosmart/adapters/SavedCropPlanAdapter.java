package com.agrosmart.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.R;
import com.agrosmart.models.SavedCropPlanModel;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class SavedCropPlanAdapter extends RecyclerView.Adapter<SavedCropPlanAdapter.ViewHolder> {

    public interface OnPlanActionListener {
        void onPlantedToggle(SavedCropPlanModel plan, boolean isPlanted);
        void onDelete(SavedCropPlanModel plan);
    }

    private List<SavedCropPlanModel> list = new ArrayList<>();
    private final OnPlanActionListener listener;

    public SavedCropPlanAdapter(OnPlanActionListener listener) {
        this.listener = listener;
    }

    public void setPlans(List<SavedCropPlanModel> plans) {
        this.list = plans != null ? plans : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_crop_plan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavedCropPlanModel plan = list.get(position);

        String seasonDist = (plan.getSeason() != null ? plan.getSeason() : "Season") + " • " +
                (plan.getDistrict() != null ? plan.getDistrict() + " District" : "Maharashtra");
        holder.tvSavedSeasonDistrict.setText(seasonDist);
        holder.tvSavedDate.setText(plan.getCreatedDate() != null ? plan.getCreatedDate() : "");

        String cropTitle = (plan.getCropName() != null ? plan.getCropName() : "Crop") +
                (plan.getVariety() != null && !plan.getVariety().isEmpty() ? " (" + plan.getVariety() + ")" : "");
        holder.tvSavedCropName.setText(cropTitle);

        String planTypeStr = "BALANCED_BEST_FIT".equalsIgnoreCase(plan.getPlanType()) ? "Plan A: Balanced Best Fit" :
                ("LOW_WATER_RESILIENT".equalsIgnoreCase(plan.getPlanType()) ? "Plan B: Low-Water Resilient Fit" : "Plan C: High-Value Diversification");
        holder.tvSavedPlanType.setText(planTypeStr);

        String costStr = plan.getEstimatedCostPerAcre() != null ? "₹" + Math.round(plan.getEstimatedCostPerAcre()) + "/ac" : "N/A";
        String yieldStr = plan.getTargetYield() != null ? plan.getTargetYield() : "N/A";
        holder.tvSavedYieldCost.setText("Target: " + yieldStr + " | Cost: " + costStr);

        holder.tvSavedSource.setText(plan.getUniversitySource() != null ? plan.getUniversitySource() : "ICAR / SAU Recommended");

        // Planted Chip
        holder.chipPlantedStatus.setOnCheckedChangeListener(null);
        holder.chipPlantedStatus.setChecked(plan.getIsPlanted());
        holder.chipPlantedStatus.setText(plan.getIsPlanted() ? "PLANTED IN FIELD" : "NOT PLANTED");
        holder.chipPlantedStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            plan.setIsPlanted(isChecked);
            holder.chipPlantedStatus.setText(isChecked ? "PLANTED IN FIELD" : "NOT PLANTED");
            if (listener != null) {
                listener.onPlantedToggle(plan, isChecked);
            }
        });

        // Delete Button
        holder.btnDeletePlan.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(plan);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSavedSeasonDistrict;
        TextView tvSavedDate;
        TextView tvSavedCropName;
        TextView tvSavedPlanType;
        TextView tvSavedYieldCost;
        TextView tvSavedSource;
        Chip chipPlantedStatus;
        ImageView btnDeletePlan;

        ViewHolder(View itemView) {
            super(itemView);
            tvSavedSeasonDistrict = itemView.findViewById(R.id.tvSavedSeasonDistrict);
            tvSavedDate = itemView.findViewById(R.id.tvSavedDate);
            tvSavedCropName = itemView.findViewById(R.id.tvSavedCropName);
            tvSavedPlanType = itemView.findViewById(R.id.tvSavedPlanType);
            tvSavedYieldCost = itemView.findViewById(R.id.tvSavedYieldCost);
            tvSavedSource = itemView.findViewById(R.id.tvSavedSource);
            chipPlantedStatus = itemView.findViewById(R.id.chipPlantedStatus);
            btnDeletePlan = itemView.findViewById(R.id.btnDeletePlan);
        }
    }
}
