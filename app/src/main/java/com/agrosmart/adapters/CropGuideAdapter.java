package com.agrosmart.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.databinding.ItemCropGuideBinding;
import com.agrosmart.models.CropGuide;

import java.util.ArrayList;
import java.util.List;

public class CropGuideAdapter extends RecyclerView.Adapter<CropGuideAdapter.GuideViewHolder> {

    public interface OnGuideClickListener {
        void onGuideClick(CropGuide guide);
    }

    private final List<CropGuide> items = new ArrayList<>();
    private final OnGuideClickListener listener;

    public CropGuideAdapter(OnGuideClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<CropGuide> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GuideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCropGuideBinding binding = ItemCropGuideBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new GuideViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GuideViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class GuideViewHolder extends RecyclerView.ViewHolder {
        private final ItemCropGuideBinding binding;

        GuideViewHolder(ItemCropGuideBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CropGuide guide) {
            binding.tvCropGuideName.setText(guide.getCropName());
            binding.tvScientificName.setText(guide.getScientificName());
            binding.tvSeasonChip.setText(guide.getSeason() + " Season");
            binding.tvDuration.setText(guide.getDuration());
            binding.tvYield.setText(guide.getExpectedYield());
            binding.tvWater.setText(guide.getWaterRequirement().contains("Rainfed") ? "Rainfed" : "Irrigated");

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onGuideClick(guide);
                }
            });
        }
    }
}
