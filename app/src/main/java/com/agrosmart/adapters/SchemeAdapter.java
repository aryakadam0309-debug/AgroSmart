package com.agrosmart.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.activities.farmer.SchemeDetailActivity;
import com.agrosmart.databinding.ItemSchemeBinding;
import com.agrosmart.models.GovernmentScheme;

import java.util.ArrayList;
import java.util.List;

public class SchemeAdapter extends RecyclerView.Adapter<SchemeAdapter.SchemeViewHolder> {

    private List<GovernmentScheme> schemes = new ArrayList<>();
    private final Context context;

    public SchemeAdapter(Context context) {
        this.context = context;
    }

    public void setSchemes(List<GovernmentScheme> list) {
        this.schemes = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SchemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSchemeBinding binding = ItemSchemeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SchemeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SchemeViewHolder holder, int position) {
        holder.bind(schemes.get(position));
    }

    @Override
    public int getItemCount() {
        return schemes.size();
    }

    class SchemeViewHolder extends RecyclerView.ViewHolder {
        private final ItemSchemeBinding binding;

        public SchemeViewHolder(ItemSchemeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(GovernmentScheme item) {
            binding.tvSchemeName.setText(item.getSchemeName());
            binding.tvSchemeDesc.setText(item.getDescription());
            binding.tvSchemeBenefits.setText("🎁 " + item.getBenefits());
            binding.tvSchemeCategory.setText(item.getCategory());

            binding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(context, SchemeDetailActivity.class);
                intent.putExtra("extra_scheme_item", item);
                context.startActivity(intent);
            });
        }
    }
}
