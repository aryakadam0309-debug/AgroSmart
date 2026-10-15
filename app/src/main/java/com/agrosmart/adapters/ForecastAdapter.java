package com.agrosmart.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.databinding.ItemForecastBinding;
import com.agrosmart.models.ForecastItem;

import java.util.ArrayList;
import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    private List<ForecastItem> items = new ArrayList<>();

    public void setItems(List<ForecastItem> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemForecastBinding binding = ItemForecastBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ForecastViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ForecastViewHolder extends RecyclerView.ViewHolder {
        private final ItemForecastBinding binding;

        public ForecastViewHolder(ItemForecastBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ForecastItem item) {
            binding.tvDay.setText(item.getDay());
            binding.tvDate.setText(item.getDate());
            binding.tvCondition.setText(item.getCondition());
            binding.tvTemp.setText(String.format("%.0f°C", item.getTemp()));
            binding.tvMinMax.setText(String.format("H: %.0f° • L: %.0f°", item.getMaxTemp(), item.getMinTemp()));
        }
    }
}
