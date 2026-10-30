package com.agrosmart.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.databinding.ItemTractorBinding;
import com.agrosmart.models.Tractor;

import java.util.ArrayList;
import java.util.List;

public class TractorAdapter extends RecyclerView.Adapter<TractorAdapter.TractorViewHolder> {

    public interface OnTractorClickListener {
        void onBookClick(Tractor tractor);
    }

    private final List<Tractor> tractorList = new ArrayList<>();
    private final OnTractorClickListener listener;

    public TractorAdapter(OnTractorClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Tractor> list) {
        tractorList.clear();
        if (list != null) {
            tractorList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TractorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTractorBinding binding = ItemTractorBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TractorViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TractorViewHolder holder, int position) {
        holder.bind(tractorList.get(position));
    }

    @Override
    public int getItemCount() {
        return tractorList.size();
    }

    class TractorViewHolder extends RecyclerView.ViewHolder {
        private final ItemTractorBinding binding;

        TractorViewHolder(ItemTractorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Tractor tractor) {
            binding.tvTractorModel.setText(tractor.getModelName());
            binding.tvHpBadge.setText(tractor.getHp());

            if (tractor.getDistanceKm() != null) {
                binding.tvDistanceBadge.setVisibility(android.view.View.VISIBLE);
                binding.tvDistanceBadge.setText(String.format(java.util.Locale.US, "📍 %.1f km away", tractor.getDistanceKm()));
            } else {
                binding.tvDistanceBadge.setVisibility(android.view.View.GONE);
            }

            String displayLoc = tractor.getLocation();
            if (tractor.getVillage() != null && tractor.getDistrict() != null) {
                displayLoc = tractor.getVillage() + ", " + tractor.getDistrict();
            } else if (displayLoc == null) {
                displayLoc = "Nearby Area";
            }

            binding.tvLocationOwner.setText("📍 " + displayLoc + " • Owner: " +
                    (tractor.getOwnerName() != null ? tractor.getOwnerName() : "Partner Farmer"));
            binding.tvImplements.setText("🚜 Implements: " +
                    (tractor.getImplementsIncluded() != null ? tractor.getImplementsIncluded() : "Standard Hitch"));
            binding.tvRateHourly.setText(String.format("₹%,.0f / hr", tractor.getHourlyRate()));
            binding.tvRateDaily.setText(String.format("Daily: ₹%,.0f / day", tractor.getDailyRate()));

            binding.btnBookNow.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBookClick(tractor);
                }
            });

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBookClick(tractor);
                }
            });
        }
    }
}
