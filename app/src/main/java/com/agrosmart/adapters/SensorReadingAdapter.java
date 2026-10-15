package com.agrosmart.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.R;
import com.agrosmart.databinding.ItemSensorReadingBinding;
import com.agrosmart.models.SensorReading;

import java.util.ArrayList;
import java.util.List;

public class SensorReadingAdapter extends RecyclerView.Adapter<SensorReadingAdapter.ViewHolder> {

    private List<SensorReading> readings = new ArrayList<>();

    public void setReadings(List<SensorReading> list) {
        this.readings = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSensorReadingBinding binding = ItemSensorReadingBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(readings.get(position));
    }

    @Override
    public int getItemCount() {
        return readings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemSensorReadingBinding binding;

        public ViewHolder(ItemSensorReadingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(SensorReading reading) {
            binding.tvRecordedAt.setText(reading.getRecordedAt() != null ? reading.getRecordedAt() : "Recent");
            binding.tvMoisturePct.setText(String.format("%.1f%%", reading.getSoilMoisturePct()));
            binding.tvRawAdc.setText("ADC: " + reading.getSoilMoistureRaw());

            String status = reading.getMoistureStatus() != null ? reading.getMoistureStatus().toUpperCase() : "NORMAL";
            binding.tvMoistureBadge.setText(status);

            if ("DRY".equals(status)) {
                binding.tvMoistureBadge.setBackgroundColor(Color.parseColor("#FFEBEE"));
                binding.tvMoistureBadge.setTextColor(Color.parseColor("#C62828"));
            } else if ("WET".equals(status)) {
                binding.tvMoistureBadge.setBackgroundColor(Color.parseColor("#E1F5FE"));
                binding.tvMoistureBadge.setTextColor(Color.parseColor("#0277BD"));
            } else {
                binding.tvMoistureBadge.setBackgroundColor(Color.parseColor("#E8F5E9"));
                binding.tvMoistureBadge.setTextColor(Color.parseColor("#2E7D32"));
            }

            if (reading.getTemperatureC() != null) {
                binding.tvTemperature.setText(String.format("%.1f°C", reading.getTemperatureC()));
            } else {
                binding.tvTemperature.setText("--");
            }

            if (reading.getHumidityPct() != null) {
                binding.tvHumidity.setText(String.format("%.1f%%", reading.getHumidityPct()));
            } else {
                binding.tvHumidity.setText("--");
            }

            binding.tvSensorStatus.setText("Status: " + (reading.getSensorStatus() != null ? reading.getSensorStatus() : "OK"));
        }
    }
}
