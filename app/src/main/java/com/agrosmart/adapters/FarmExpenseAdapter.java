package com.agrosmart.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.R;
import com.agrosmart.models.FarmExpense;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FarmExpenseAdapter extends RecyclerView.Adapter<FarmExpenseAdapter.ViewHolder> {

    private final List<FarmExpense> items = new ArrayList<>();
    private final OnItemDeleteListener deleteListener;

    public interface OnItemDeleteListener {
        void onDelete(FarmExpense item);
    }

    public FarmExpenseAdapter(OnItemDeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public void setItems(List<FarmExpense> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_farm_expense, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FarmExpense item = items.get(position);
        boolean isRev = "REVENUE".equalsIgnoreCase(item.getEntryType());

        holder.tvTitle.setText(item.getTitle() != null ? item.getTitle() : "Farm Entry");
        holder.tvSubtitle.setText((item.getCategory() != null ? item.getCategory() : "General") +
                " • " + (item.getEntryDate() != null ? item.getEntryDate() : ""));

        double amt = item.getAmount() != null ? item.getAmount() : 0.0;
        if (isRev) {
            holder.tvIcon.setText("💰");
            holder.tvIcon.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFDCFCE7)); // Green
            holder.tvAmount.setText(String.format(Locale.getDefault(), "+ ₹ %,.2f", amt));
            holder.tvAmount.setTextColor(0xFF15803D); // Green
        } else {
            holder.tvIcon.setText("💸");
            holder.tvIcon.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFEE2E2)); // Red
            holder.tvAmount.setText(String.format(Locale.getDefault(), "- ₹ %,.2f", amt));
            holder.tvAmount.setTextColor(0xFFDC2626); // Red
        }

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onDelete(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcon, tvTitle, tvSubtitle, tvAmount, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvIcon = itemView.findViewById(R.id.tvEntryIcon);
            tvTitle = itemView.findViewById(R.id.tvEntryTitle);
            tvSubtitle = itemView.findViewById(R.id.tvEntrySubtitle);
            tvAmount = itemView.findViewById(R.id.tvEntryAmount);
            btnDelete = itemView.findViewById(R.id.btnDeleteEntry);
        }
    }
}
