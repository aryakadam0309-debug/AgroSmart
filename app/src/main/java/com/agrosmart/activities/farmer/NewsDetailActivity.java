package com.agrosmart.activities.farmer;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.agrosmart.R;
import com.agrosmart.databinding.ActivityNewsDetailBinding;
import com.agrosmart.models.NewsItem;
import com.agrosmart.utils.LocaleHelper;
import com.agrosmart.utils.SessionManager;

public class NewsDetailActivity extends AppCompatActivity {

    private ActivityNewsDetailBinding binding;

    @Override
    protected void attachBaseContext(Context newBase) {
        SessionManager session = new SessionManager(newBase);
        super.attachBaseContext(LocaleHelper.setLocale(newBase, session.getLanguage()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewsDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnDetailBack.setOnClickListener(v -> finish());

        NewsItem item = (NewsItem) getIntent().getSerializableExtra("extra_news_item");
        if (item != null) {
            binding.tvDetailTitle.setText(item.getTitle());
            binding.tvDetailCategory.setText(item.getCategory());
            binding.tvDetailDate.setText(item.getPublishedDate());
            binding.tvDetailSource.setText("Source: " + item.getSourceName());
            binding.tvDetailContent.setText(item.getFullContent());

            Glide.with(this)
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.bg_gradient_header)
                    .error(R.drawable.bg_gradient_header)
                    .into(binding.ivDetailHero);
        }
    }
}
