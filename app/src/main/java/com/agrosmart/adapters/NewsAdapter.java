package com.agrosmart.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.agrosmart.R;
import com.agrosmart.activities.farmer.NewsDetailActivity;
import com.agrosmart.databinding.ItemNewsBinding;
import com.agrosmart.models.NewsItem;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<NewsItem> newsList = new ArrayList<>();
    private final Context context;

    public NewsAdapter(Context context) {
        this.context = context;
    }

    public void setNewsList(List<NewsItem> list) {
        this.newsList = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNewsBinding binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NewsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        holder.bind(newsList.get(position));
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {
        private final ItemNewsBinding binding;

        public NewsViewHolder(ItemNewsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(NewsItem item) {
            binding.tvNewsTitle.setText(item.getTitle());
            binding.tvNewsDesc.setText(item.getShortDescription());
            binding.tvNewsCategory.setText(item.getCategory());
            binding.tvNewsDate.setText(item.getPublishedDate() + " • " + item.getSourceName());

            Glide.with(context)
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.bg_gradient_header)
                    .error(R.drawable.bg_gradient_header)
                    .transform(new RoundedCorners(24))
                    .into(binding.ivNewsThumb);

            binding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(context, NewsDetailActivity.class);
                intent.putExtra("extra_news_item", item);
                context.startActivity(intent);
            });
        }
    }
}
