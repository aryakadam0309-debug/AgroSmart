package com.agrosmart.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.databinding.ItemNotificationBinding;
import com.agrosmart.models.NotificationItem;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotifViewHolder> {

    private final List<NotificationItem> items = new ArrayList<>();

    public void setItems(List<NotificationItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new NotifViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class NotifViewHolder extends RecyclerView.ViewHolder {
        private final ItemNotificationBinding binding;

        NotifViewHolder(ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(NotificationItem n) {
            binding.tvNotifTitle.setText(n.getTitle());
            binding.tvNotifMessage.setText(n.getMessage());
            binding.tvNotifTime.setText(n.getTimestamp());
        }
    }
}
