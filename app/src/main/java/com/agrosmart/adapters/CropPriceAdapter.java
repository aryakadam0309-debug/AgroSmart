package com.agrosmart.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.R;
import com.agrosmart.databinding.ItemCropPriceBinding;
import com.agrosmart.models.CropPrice;

import java.util.ArrayList;
import java.util.List;

public class CropPriceAdapter extends RecyclerView.Adapter<CropPriceAdapter.PriceViewHolder> {

    public interface OnPriceClickListener {
        void onPriceClick(CropPrice price);
    }

    private final List<CropPrice> items = new ArrayList<>();
    private final OnPriceClickListener listener;

    public CropPriceAdapter(OnPriceClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<CropPrice> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PriceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCropPriceBinding binding = ItemCropPriceBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new PriceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class PriceViewHolder extends RecyclerView.ViewHolder {
        private final ItemCropPriceBinding binding;

        PriceViewHolder(ItemCropPriceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CropPrice price) {
            Context ctx = binding.getRoot().getContext();

            binding.tvCropName.setText(price.getCropName());
            binding.tvCategoryBadge.setText(price.getCategory());
            binding.tvVarietyArrival.setText("Variety: " + price.getVariety() + " • Arrivals: " + price.getArrivalQuantity());
            binding.tvMarketLocation.setText("📍 " + price.getMarketName() + ", " + price.getState());
            binding.tvModalPrice.setText(String.format("₹%,.0f %s", price.getModalPrice(), price.getPriceUnit()));
            binding.tvPriceRange.setText(String.format("Min: ₹%,.0f  |  Max: ₹%,.0f", price.getMinPrice(), price.getMaxPrice()));
            binding.tvUpdatedTime.setText("Updated: " + price.getUpdatedDate());

            if ("UP".equalsIgnoreCase(price.getTrend())) {
                binding.tvTrend.setBackground(ContextCompat.getDrawable(ctx, R.drawable.bg_trend_up));
                binding.tvTrend.setTextColor(ContextCompat.getColor(ctx, R.color.status_success));
                binding.tvTrend.setText(String.format("▲ +₹%.0f", Math.abs(price.getPriceChange())));
            } else if ("DOWN".equalsIgnoreCase(price.getTrend())) {
                binding.tvTrend.setBackground(ContextCompat.getDrawable(ctx, R.drawable.bg_trend_down));
                binding.tvTrend.setTextColor(ContextCompat.getColor(ctx, R.color.status_error));
                binding.tvTrend.setText(String.format("▼ -₹%.0f", Math.abs(price.getPriceChange())));
            } else {
                binding.tvTrend.setBackground(ContextCompat.getDrawable(ctx, R.drawable.bg_trend_stable));
                binding.tvTrend.setTextColor(ContextCompat.getColor(ctx, R.color.text_muted));
                binding.tvTrend.setText("● Stable");
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPriceClick(price);
                }
            });
        }
    }
}
