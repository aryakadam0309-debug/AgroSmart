package com.agrosmart.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.databinding.ItemCropClusterBinding;
import com.agrosmart.models.CropCluster;

import java.util.ArrayList;
import java.util.List;

public class CropClusterAdapter extends RecyclerView.Adapter<CropClusterAdapter.ViewHolder> {

    private List<CropCluster> items = new ArrayList<>();
    private final OnClusterActionListener listener;

    public interface OnClusterActionListener {
        void onPublishAlert(CropCluster cluster);
    }

    public CropClusterAdapter(OnClusterActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<CropCluster> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCropClusterBinding binding = ItemCropClusterBinding.inflate(
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
        private final ItemCropClusterBinding binding;

        ViewHolder(ItemCropClusterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CropCluster item) {
            binding.tvClusterTitle.setText(item.getCropName() + ": " + item.getProblemCategory());
            binding.tvFarmersCount.setText(item.getDistinctFarmerCount() + " Distinct Farmers (" + item.getTotalReportCount() + " Reports)");

            String loc = "📍 District: " + (item.getDistrict() != null ? item.getDistrict() : "Pune") +
                    " (Village: " + (item.getVillage() != null ? item.getVillage() : "Local") + ")";
            binding.tvClusterLocation.setText(loc);

            if (item.isPotentialCluster()) {
                binding.tvClusterStatusBadge.setText("⚠️ POTENTIAL CLUSTER FLAGGED");
                binding.tvClusterStatusBadge.setTextColor(Color.parseColor("#B45309"));
            } else {
                binding.tvClusterStatusBadge.setText("🔍 OBSERVING (2 REPORTS)");
                binding.tvClusterStatusBadge.setTextColor(Color.parseColor("#1D4ED8"));
            }

            if (item.getWarningNote() != null) {
                binding.tvClusterDisclaimer.setText(item.getWarningNote());
            }

            if (item.getSampleSymptoms() != null && !item.getSampleSymptoms().isEmpty()) {
                StringBuilder sb = new StringBuilder("Reported symptoms: ");
                for (int i = 0; i < item.getSampleSymptoms().size(); i++) {
                    sb.append(item.getSampleSymptoms().get(i));
                    if (i < item.getSampleSymptoms().size() - 1) sb.append("; ");
                }
                binding.tvSampleSymptoms.setText(sb.toString());
            } else {
                binding.tvSampleSymptoms.setText("Reported symptoms logged across local farms.");
            }

            binding.btnPublishAlertFromCluster.setOnClickListener(v -> {
                if (listener != null) listener.onPublishAlert(item);
            });
        }
    }
}
