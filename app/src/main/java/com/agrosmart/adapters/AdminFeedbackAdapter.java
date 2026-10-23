package com.agrosmart.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.R;
import com.agrosmart.models.FarmerFeedback;

import java.util.ArrayList;
import java.util.List;

public class AdminFeedbackAdapter extends RecyclerView.Adapter<AdminFeedbackAdapter.ViewHolder> {

    private final List<FarmerFeedback> items = new ArrayList<>();

    public void setItems(List<FarmerFeedback> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_feedback, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FarmerFeedback item = items.get(position);
        holder.tvFarmerName.setText(item.getFarmerName() != null ? item.getFarmerName() : "Farmer (" + item.getFarmerPhone() + ")");

        int r = item.getRating() != null ? item.getRating() : 5;
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < r; i++) stars.append("⭐");
        holder.tvRating.setText(stars.toString());

        holder.tvMeta.setText((item.getCategory() != null ? item.getCategory() : "General") +
                " • " + (item.getSubmittedAt() != null ? item.getSubmittedAt() : "") +
                " • " + (item.getAppVersion() != null ? item.getAppVersion() : ""));

        holder.tvComments.setText(item.getComments() != null ? item.getComments() : "");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFarmerName, tvRating, tvMeta, tvComments;

        ViewHolder(View itemView) {
            super(itemView);
            tvFarmerName = itemView.findViewById(R.id.tvFeedbackFarmerName);
            tvRating = itemView.findViewById(R.id.tvFeedbackRating);
            tvMeta = itemView.findViewById(R.id.tvFeedbackMeta);
            tvComments = itemView.findViewById(R.id.tvFeedbackComments);
        }
    }
}
