package com.agrosmart.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.R;
import com.agrosmart.databinding.ItemForecastBinding;
import com.agrosmart.models.ForecastItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
            if (item == null) return;
            Context context = binding.getRoot().getContext();

            binding.tvDay.setText(item.getDay());
            binding.tvDate.setText(item.getDate());
            binding.tvCondition.setText(item.getCondition());
            binding.tvTemp.setText(String.format(Locale.getDefault(), "%.0f°C", item.getTemp()));
            binding.tvMinMax.setText(String.format(Locale.getDefault(), "H: %.0f° • L: %.0f°", item.getMaxTemp(), item.getMinTemp()));

            // Metrics: Rain & Wind/Humidity
            binding.tvRainMetrics.setText(String.format(Locale.getDefault(), "🌧️ %d%% • %.1f mm",
                    item.getRainProbability(), item.getExpectedRainfallMm()));
            binding.tvWindHumidity.setText(String.format(Locale.getDefault(), "💨 %.1f km/h • 💧 %d%%",
                    item.getWindSpeed(), item.getHumidity()));

            // Farming Risk Indicator Chip
            String risk = item.getRiskLevel() != null ? item.getRiskLevel().toUpperCase() : "LOW";
            if (item.isHeavyRain() || "HIGH".equalsIgnoreCase(risk)) {
                binding.tvRiskBadge.setVisibility(android.view.View.VISIBLE);
                binding.tvRiskBadge.setText(context.getString(R.string.high_risk_badge));
                binding.tvRiskBadge.setBackgroundColor(Color.parseColor("#FFCDD2"));
                binding.tvRiskBadge.setTextColor(Color.parseColor("#B71C1C"));
            } else if ("MODERATE".equalsIgnoreCase(risk)) {
                binding.tvRiskBadge.setVisibility(android.view.View.VISIBLE);
                binding.tvRiskBadge.setText(context.getString(R.string.moderate_risk_badge));
                binding.tvRiskBadge.setBackgroundColor(Color.parseColor("#FFE0B2"));
                binding.tvRiskBadge.setTextColor(Color.parseColor("#E65100"));
            } else {
                binding.tvRiskBadge.setVisibility(android.view.View.GONE);
                binding.tvRiskBadge.setText("");
            }
        }
    }
}
