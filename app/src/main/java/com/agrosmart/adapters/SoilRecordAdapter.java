package com.agrosmart.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.R;
import com.agrosmart.models.SoilHealthRecord;

import java.util.ArrayList;
import java.util.List;

public class SoilRecordAdapter extends RecyclerView.Adapter<SoilRecordAdapter.ViewHolder> {

    private final List<SoilHealthRecord> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(SoilHealthRecord item);
    }

    public SoilRecordAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<SoilHealthRecord> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_soil_record, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SoilHealthRecord item = items.get(position);
        holder.tvFieldName.setText(item.getFieldName() != null ? item.getFieldName() : "Main Farm Plot");
        holder.tvRating.setText(item.getFertilityRating() != null ? item.getFertilityRating() : "BALANCED");
        holder.tvDate.setText(item.getSampleDate() != null ? item.getSampleDate() : "");
        holder.tvPh.setText("pH: " + (item.getPh() != null ? item.getPh() : "--"));
        holder.tvOc.setText("OC: " + (item.getOrganicCarbon() != null ? item.getOrganicCarbon() + "%" : "--"));
        holder.tvNpk.setText("NPK: " +
                (item.getNitrogenKgHa() != null ? item.getNitrogenKgHa().intValue() : "--") + "-" +
                (item.getPhosphorusKgHa() != null ? item.getPhosphorusKgHa().intValue() : "--") + "-" +
                (item.getPotassiumKgHa() != null ? item.getPotassiumKgHa().intValue() : "--"));
        holder.tvAdvisory.setText(item.getAdvisoryNotes() != null ? item.getAdvisoryNotes() : "");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFieldName, tvRating, tvDate, tvPh, tvOc, tvNpk, tvAdvisory;

        ViewHolder(View itemView) {
            super(itemView);
            tvFieldName = itemView.findViewById(R.id.tvItemFieldName);
            tvRating = itemView.findViewById(R.id.tvItemRating);
            tvDate = itemView.findViewById(R.id.tvItemDate);
            tvPh = itemView.findViewById(R.id.tvItemPh);
            tvOc = itemView.findViewById(R.id.tvItemOc);
            tvNpk = itemView.findViewById(R.id.tvItemNpk);
            tvAdvisory = itemView.findViewById(R.id.tvItemAdvisory);
        }
    }
}
