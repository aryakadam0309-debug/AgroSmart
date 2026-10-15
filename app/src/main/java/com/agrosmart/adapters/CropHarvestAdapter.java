package com.agrosmart.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.R;
import com.agrosmart.databinding.ItemHarvestBinding;
import com.agrosmart.models.CropHarvest;

import java.util.ArrayList;
import java.util.List;

public class CropHarvestAdapter extends RecyclerView.Adapter<CropHarvestAdapter.HarvestViewHolder> {

    public interface OnHarvestClickListener {
        void onHarvestClick(CropHarvest harvest);
    }

    private final List<CropHarvest> items = new ArrayList<>();
    private final OnHarvestClickListener listener;

    public CropHarvestAdapter(OnHarvestClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<CropHarvest> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HarvestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHarvestBinding binding = ItemHarvestBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new HarvestViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HarvestViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class HarvestViewHolder extends RecyclerView.ViewHolder {
        private final ItemHarvestBinding binding;

        HarvestViewHolder(ItemHarvestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CropHarvest h) {
            binding.tvHarvestCropName.setText(h.getCropName());
            binding.tvHarvestVarietyDate.setText("Variety: " + h.getVariety() + " • Harvested: " + h.getHarvestDate());
            binding.tvStorageLocation.setText("📍 Storage: " + h.getStorageLocation());

            binding.tvHarvestBreakdown.setText(String.format("Total: %.0f Qtl | Sold: %.0f Qtl",
                    h.getHarvestedQuintals(), h.getSoldQuintals()));
            binding.tvExpectedRate.setText(String.format("Target: ₹%,.0f/Qtl", h.getExpectedPricePerQtl()));

            if (h.getStockRemainingQuintals() > 0) {
                binding.tvRemainingBadge.setText(String.format("%.0f Qtl Left", h.getStockRemainingQuintals()));
                binding.tvRemainingBadge.setBackgroundResource(R.drawable.bg_trend_up);
                binding.tvRemainingBadge.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.status_success));
            } else {
                binding.tvRemainingBadge.setText("SOLD OUT");
                binding.tvRemainingBadge.setBackgroundResource(R.drawable.bg_trend_stable);
                binding.tvRemainingBadge.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.text_muted));
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onHarvestClick(h);
                }
            });
        }
    }
}
