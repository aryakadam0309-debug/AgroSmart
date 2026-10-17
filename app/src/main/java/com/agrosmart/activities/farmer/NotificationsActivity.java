package com.agrosmart.activities.farmer;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.activities.BaseActivity;
import com.agrosmart.adapters.NotificationAdapter;
import com.agrosmart.databinding.ActivityNotificationsBinding;
import com.agrosmart.repository.NotificationRepository;

public class NotificationsActivity extends BaseActivity {

    private ActivityNotificationsBinding binding;
    private NotificationAdapter adapter;
    private NotificationRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = NotificationRepository.getInstance();

        binding.toolbarNotifications.setNavigationOnClickListener(v -> finish());

        adapter = new NotificationAdapter();
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        binding.rvNotifications.setAdapter(adapter);

        repository.getNotifications().observe(this, list -> {
            adapter.setItems(list);
            if (list == null || list.isEmpty()) {
                binding.tvEmptyNotifs.setVisibility(View.VISIBLE);
                binding.rvNotifications.setVisibility(View.GONE);
            } else {
                binding.tvEmptyNotifs.setVisibility(View.GONE);
                binding.rvNotifications.setVisibility(View.VISIBLE);
            }
        });
    }
}
