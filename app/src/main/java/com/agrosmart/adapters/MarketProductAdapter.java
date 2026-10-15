package com.agrosmart.adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.databinding.ItemMarketProductBinding;
import com.agrosmart.models.MarketProduct;

import java.util.ArrayList;
import java.util.List;

public class MarketProductAdapter extends RecyclerView.Adapter<MarketProductAdapter.ProductViewHolder> {

    public interface OnAddToCartListener {
        void onAddToCart(MarketProduct product);
    }

    private final List<MarketProduct> items = new ArrayList<>();
    private final OnAddToCartListener listener;

    public MarketProductAdapter(OnAddToCartListener listener) {
        this.listener = listener;
    }

    public void setItems(List<MarketProduct> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMarketProductBinding binding = ItemMarketProductBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ItemMarketProductBinding binding;

        ProductViewHolder(ItemMarketProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(MarketProduct p) {
            binding.tvProductName.setText(p.getName());
            binding.tvProductBrand.setText(p.getBrand() + " • " + p.getPackageSize());
            binding.tvProductCategoryBadge.setText(p.getCategory());
            binding.tvProductDescription.setText(p.getDescription());
            binding.tvProductPrice.setText(String.format("₹%,.0f", p.getPrice()));

            binding.tvProductOriginalPrice.setText(String.format("₹%,.0f", p.getOriginalPrice()));
            binding.tvProductOriginalPrice.setPaintFlags(binding.tvProductOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            binding.btnAddToCart.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddToCart(p);
                }
            });
        }
    }
}
