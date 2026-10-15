package com.agrosmart.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.databinding.ItemCartProductBinding;
import com.agrosmart.models.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface OnQuantityChangeListener {
        void onQuantityChange(CartItem item, int newQuantity);
    }

    private final List<CartItem> items = new ArrayList<>();
    private final OnQuantityChangeListener listener;

    public CartAdapter(OnQuantityChangeListener listener) {
        this.listener = listener;
    }

    public void setItems(List<CartItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartProductBinding binding = ItemCartProductBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CartViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private final ItemCartProductBinding binding;

        CartViewHolder(ItemCartProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CartItem item) {
            binding.tvCartItemName.setText(item.getProduct().getName());
            binding.tvCartItemPrice.setText(String.format("₹%,.0f each (Total: ₹%,.0f)",
                    item.getProduct().getPrice(), item.getSubtotal()));
            binding.tvCartQty.setText(String.valueOf(item.getQuantity()));

            binding.btnQtyMinus.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onQuantityChange(item, item.getQuantity() - 1);
                }
            });

            binding.btnQtyPlus.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onQuantityChange(item, item.getQuantity() + 1);
                }
            });
        }
    }
}
