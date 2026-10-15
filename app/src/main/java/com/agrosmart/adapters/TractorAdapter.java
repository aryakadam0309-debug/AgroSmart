package com.agrosmart.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.databinding.ItemTractorBinding;
import com.agrosmart.models.Tractor;

import java.util.ArrayList;
import java.util.List;

public class TractorAdapter extends RecyclerView.Adapter<TractorAdapter.TractorViewHolder> {

    public interface OnTractorBookListener {
        void onBookClick(Tractor tractor);
    }

    private final List<Tractor> items = new ArrayList<>();
    private final OnTractorBookListener listener;

    public TractorAdapter(OnTractorBookListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Tractor> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
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
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class TractorViewHolder extends RecyclerView.ViewHolder {
        private final ItemTractorBinding binding;

        TractorViewHolder(ItemTractorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Tractor tractor) {
            binding.tvTractorModel.setText(tractor.getModelName());
            binding.tvLocationOwner.setText("📍 " + tractor.getLocation() + " • Owner: " + tractor.getOwnerName());
            binding.tvHpBadge.setText(tractor.getHp());
            binding.tvImplements.setText("🚜 Implements: " + tractor.getImplementsIncluded());
            binding.tvRateHourly.setText(String.format("₹%,.0f / hr", tractor.getHourlyRate()));
            binding.tvRateDaily.setText(String.format("Daily: ₹%,.0f / day", tractor.getDailyRate()));

            binding.btnBookNow.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBookClick(tractor);
                }
            });
        }
    }
}
