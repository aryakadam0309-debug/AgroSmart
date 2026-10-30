package com.agrosmart.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.R;
import com.agrosmart.databinding.ItemSensorReadingBinding;
import com.agrosmart.models.SensorReading;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        if (position >= 0 && position < readings.size()) {
            holder.bind(readings.get(position));
        }
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
            if (reading == null) return;
            Context ctx = binding.getRoot().getContext();

            binding.tvRecordedAt.setText(reading.getRecordedAt() != null ? reading.getRecordedAt() : ctx.getString(R.string.recent_label));
            binding.tvMoisturePct.setText(String.format(Locale.US, "%.1f%%", reading.getSoilMoisturePct()));
            binding.tvRawAdc.setText("ADC: " + reading.getSoilMoistureRaw());

            String status = reading.getMoistureStatus() != null ? reading.getMoistureStatus().toUpperCase() : "NORMAL";

            if ("DRY".equals(status)) {
                binding.tvMoistureBadge.setText(ctx.getString(R.string.iot_status_dry));
                binding.tvMoistureBadge.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFCDD2")));
                binding.tvMoistureBadge.setTextColor(Color.parseColor("#C62828"));
            } else if ("WET".equals(status)) {
                binding.tvMoistureBadge.setText(ctx.getString(R.string.iot_status_wet));
                binding.tvMoistureBadge.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B3E5FC")));
                binding.tvMoistureBadge.setTextColor(Color.parseColor("#0277BD"));
            } else {
                binding.tvMoistureBadge.setText(ctx.getString(R.string.iot_status_moderate));
                binding.tvMoistureBadge.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C8E6C9")));
                binding.tvMoistureBadge.setTextColor(Color.parseColor("#2E7D32"));
            }

            if (reading.getTemperatureC() != null) {
                binding.tvTemperature.setText(String.format(Locale.US, "%.1f°C", reading.getTemperatureC()));
            } else {
                binding.tvTemperature.setText("--");
            }

            if (reading.getHumidityPct() != null) {
                binding.tvHumidity.setText(String.format(Locale.US, "%.1f%%", reading.getHumidityPct()));
            } else {
                binding.tvHumidity.setText("--");
            }

            binding.tvSensorStatus.setText(ctx.getString(R.string.status_label) + ": " + (reading.getSensorStatus() != null ? reading.getSensorStatus() : "OK"));
        }
    }
}
