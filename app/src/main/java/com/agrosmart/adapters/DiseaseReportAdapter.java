package com.agrosmart.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.R;
import com.agrosmart.models.DiseaseReport;

import java.util.ArrayList;
import java.util.List;

public class DiseaseReportAdapter extends RecyclerView.Adapter<DiseaseReportAdapter.ViewHolder> {

    private final List<DiseaseReport> items = new ArrayList<>();
    private final OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemClick(DiseaseReport item);
    }

    public DiseaseReportAdapter(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setItems(List<DiseaseReport> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_disease_report, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiseaseReport item = items.get(position);
        holder.tvTitle.setText(item.getCropName() + " (" + (item.getAffectedPart() != null ? item.getAffectedPart() : "Plant") + ")");
        holder.tvMeta.setText((item.getVillage() != null ? item.getVillage() + ", " : "") +
                (item.getDistrict() != null ? item.getDistrict() : "Local") + " • " +
                (item.getReportedAt() != null ? item.getReportedAt() : "") + " • " +
                (item.getSeverity() != null ? item.getSeverity() : ""));

        holder.tvSymptoms.setText("Symptoms: " + (item.getSymptoms() != null ? item.getSymptoms() : ""));

        String status = item.getStatus() != null ? item.getStatus() : "REPORTED";
        holder.tvStatus.setText(status);

        if ("VERIFIED_ALERT".equalsIgnoreCase(status)) {
            holder.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFDCFCE7)); // Green
            holder.tvStatus.setTextColor(0xFF15803D);
        } else if ("UNDER_REVIEW".equalsIgnoreCase(status)) {
            holder.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFEF3C7)); // Orange
            holder.tvStatus.setTextColor(0xFFB45309);
        } else {
            holder.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFF3F4F6)); // Grey
            holder.tvStatus.setTextColor(0xFF4B5563);
        }

        if (item.getAdminAdvisory() != null && !item.getAdminAdvisory().trim().isEmpty()) {
            holder.tvAdvisory.setVisibility(View.VISIBLE);
            holder.tvAdvisory.setText("Officer Advisory: " + item.getAdminAdvisory());
        } else {
            holder.tvAdvisory.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvStatus, tvMeta, tvSymptoms, tvAdvisory;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvReportCropTitle);
            tvStatus = itemView.findViewById(R.id.tvReportStatus);
            tvMeta = itemView.findViewById(R.id.tvReportMeta);
            tvSymptoms = itemView.findViewById(R.id.tvReportSymptoms);
            tvAdvisory = itemView.findViewById(R.id.tvReportAdvisory);
        }
    }
}
