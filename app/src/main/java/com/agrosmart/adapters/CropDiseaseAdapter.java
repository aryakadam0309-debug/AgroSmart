package com.agrosmart.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.databinding.ItemCropDiseaseBinding;
import com.agrosmart.models.CropDisease;

import java.util.ArrayList;
import java.util.List;

public class CropDiseaseAdapter extends RecyclerView.Adapter<CropDiseaseAdapter.DiseaseViewHolder> {

    public interface OnDiseaseClickListener {
        void onDiseaseClick(CropDisease disease);
    }

    private final List<CropDisease> items = new ArrayList<>();
    private final OnDiseaseClickListener listener;

    public CropDiseaseAdapter(OnDiseaseClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<CropDisease> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DiseaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCropDiseaseBinding binding = ItemCropDiseaseBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new DiseaseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DiseaseViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class DiseaseViewHolder extends RecyclerView.ViewHolder {
        private final ItemCropDiseaseBinding binding;

        DiseaseViewHolder(ItemCropDiseaseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CropDisease disease) {
            binding.tvDiseaseCrop.setText("Crop: " + disease.getCropName());
            binding.tvDiseaseTitle.setText(disease.getDiseaseName());
            binding.tvSeverityBadge.setText(disease.getSeverity());
            binding.tvCausalOrganism.setText("Pathogen: " + disease.getCausalOrganism());
            binding.tvSymptomsPreview.setText("Symptoms: " + disease.getSymptoms());

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDiseaseClick(disease);
                }
            });
        }
    }
}
