package com.agrosmart.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.R;
import com.agrosmart.databinding.ItemFarmingAdvisoryBinding;
import com.agrosmart.models.FarmingAdvisory;

import java.util.ArrayList;
import java.util.List;

public class FarmingAdvisoryAdapter extends RecyclerView.Adapter<FarmingAdvisoryAdapter.AdvisoryViewHolder> {

    private List<FarmingAdvisory> items = new ArrayList<>();

    public void setItems(List<FarmingAdvisory> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdvisoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFarmingAdvisoryBinding binding = ItemFarmingAdvisoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new AdvisoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdvisoryViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class AdvisoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemFarmingAdvisoryBinding binding;

        public AdvisoryViewHolder(ItemFarmingAdvisoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(FarmingAdvisory item) {
            if (item == null) return;
            Context context = binding.getRoot().getContext();

            String activity = item.getActivity() != null ? item.getActivity() : "Advisory";

            if (activity.toLowerCase().contains("sow")) {
                binding.tvActivityIcon.setText("🌱");
                binding.tvActivityTitle.setText(context.getString(R.string.advisory_sowing));
            } else if (activity.toLowerCase().contains("spray")) {
                binding.tvActivityIcon.setText("🧪");
                binding.tvActivityTitle.setText(context.getString(R.string.advisory_spraying));
            } else if (activity.toLowerCase().contains("harvest")) {
                binding.tvActivityIcon.setText("🌾");
                binding.tvActivityTitle.setText(context.getString(R.string.advisory_harvesting));
            } else {
                binding.tvActivityIcon.setText("📋");
                binding.tvActivityTitle.setText(activity + " " + context.getString(R.string.details));
            }

            String status = item.getStatus() != null ? item.getStatus() : "Suitable";

            // Dynamic color tinting based on status severity
            if ("Avoid/Delay".equalsIgnoreCase(status) || status.toLowerCase().contains("avoid")) {
                binding.tvStatusBadge.setText(context.getString(R.string.status_avoid_delay));
                binding.tvStatusBadge.setBackgroundColor(Color.parseColor("#FFEBEE")); // Light red
                binding.tvStatusBadge.setTextColor(Color.parseColor("#D32F2F"));
                binding.boxAction.setBackgroundColor(Color.parseColor("#FFEBEE"));
            } else if ("Caution".equalsIgnoreCase(status) || status.toLowerCase().contains("caution")) {
                binding.tvStatusBadge.setText(context.getString(R.string.status_caution));
                binding.tvStatusBadge.setBackgroundColor(Color.parseColor("#FFF3E0")); // Light orange
                binding.tvStatusBadge.setTextColor(Color.parseColor("#E65100"));
                binding.boxAction.setBackgroundColor(Color.parseColor("#FFF3E0"));
            } else {
                binding.tvStatusBadge.setText(context.getString(R.string.status_suitable));
                binding.tvStatusBadge.setBackgroundColor(Color.parseColor("#E8F5E9")); // Light green
                binding.tvStatusBadge.setTextColor(Color.parseColor("#2E7D32"));
                binding.boxAction.setBackgroundColor(Color.parseColor("#F0F7F2"));
            }

            binding.tvTriggerMetrics.setText(item.getTriggerMetrics() != null ? item.getTriggerMetrics() : "");
            binding.tvExplanation.setText(item.getExplanation() != null ? item.getExplanation() : "");
            binding.tvRecommendedAction.setText(item.getRecommendedAction() != null ? item.getRecommendedAction() : "");

            String period = item.getForecastDate() != null ? item.getForecastDate() : context.getString(R.string.today_label);
            binding.tvForecastDate.setText(String.format(context.getString(R.string.period_label), period));
        }
    }
}
