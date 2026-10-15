package com.agrosmart.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.R;
import com.agrosmart.databinding.ItemFertilizerBinding;
import com.agrosmart.models.FertilizerItem;

import java.util.ArrayList;
import java.util.List;

public class FertilizerAdapter extends RecyclerView.Adapter<FertilizerAdapter.FertilizerViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(FertilizerItem item);
    }

    private final List<FertilizerItem> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public FertilizerAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<FertilizerItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FertilizerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFertilizerBinding binding = ItemFertilizerBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new FertilizerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FertilizerViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class FertilizerViewHolder extends RecyclerView.ViewHolder {
        private final ItemFertilizerBinding binding;

        FertilizerViewHolder(ItemFertilizerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(FertilizerItem item) {
            binding.tvFertilizerName.setText(item.getName());
            binding.tvFertilizerCategory.setText(item.getCategory() + " • Purchased: " + item.getPurchaseDate());

            double totalKg = item.getQuantityBags() * item.getBagWeightKg();
            binding.tvStockQuantity.setText(String.format("%d Bags (%.0f Kg)", item.getQuantityBags(), totalKg));
            binding.tvCostSummary.setText(String.format("₹%,.1f / bag", item.getCostPerBag()));

            if (item.isLowStock()) {
                binding.tvStockBadge.setText("LOW STOCK");
                binding.tvStockBadge.setBackgroundResource(R.drawable.bg_trend_down);
                binding.tvStockBadge.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.status_error));
            } else {
                binding.tvStockBadge.setText("IN STOCK");
                binding.tvStockBadge.setBackgroundResource(R.drawable.bg_trend_up);
                binding.tvStockBadge.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.status_success));
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
