package com.agrosmart.repository;

import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.agrosmart.models.NotificationItem;
import com.agrosmart.network.ApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationRepository {

    private static final String TAG = "NotificationRepo";
    private static NotificationRepository instance;
    private final List<NotificationItem> notifications = new ArrayList<>();
    private final MutableLiveData<List<NotificationItem>> notifLiveData = new MutableLiveData<>();

    public static synchronized NotificationRepository getInstance() {
        if (instance == null) {
            instance = new NotificationRepository();
        }
        return instance;
    }

    private NotificationRepository() {
        dispatchNotifications();
    }

    private void dispatchNotifications() {
        try {
            if (Looper.getMainLooper() != null && Looper.myLooper() == Looper.getMainLooper()) {
                notifLiveData.setValue(new ArrayList<>(notifications));
            } else {
                notifLiveData.postValue(new ArrayList<>(notifications));
            }
        } catch (Exception ignored) {
            // JVM Unit testing environment fallback
        }
    }

    public LiveData<List<NotificationItem>> getNotifications() {
        return notifLiveData;
    }

    public void loadNotifications(String phone) {
        ApiClient.getInstance().getApi().getNotifications(phone)
                .enqueue(new Callback<List<NotificationItem>>() {
                    @Override
                    public void onResponse(Call<List<NotificationItem>> call, Response<List<NotificationItem>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            synchronized (NotificationRepository.this) {
                                notifications.clear();
                                notifications.addAll(response.body());
                            }
                            dispatchNotifications();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<NotificationItem>> call, Throwable t) {
                        Log.w(TAG, "Backend unreachable for notifications: " + t.getMessage());
                    }
                });
    }

    public synchronized void addNotification(NotificationItem item) {
        if (item != null) {
            notifications.add(0, item);
            dispatchNotifications();

            // Persist to MySQL
            ApiClient.getInstance().getApi().createNotification(item)
                    .enqueue(new Callback<NotificationItem>() {
                        @Override
                        public void onResponse(Call<NotificationItem> call, Response<NotificationItem> response) {
                            Log.i(TAG, "Notification saved to MySQL: " + item.getId());
                        }

                        @Override
                        public void onFailure(Call<NotificationItem> call, Throwable t) {
                            Log.w(TAG, "Failed to save notification to backend: " + t.getMessage());
                        }
                    });
        }
    }

    public synchronized void addNotification(String id, String title, String message, String timestamp, String type, boolean isRead) {
        addNotification(new NotificationItem(id, title, message, timestamp, type, isRead));
    }

    public synchronized void markAllAsRead(String phone) {
        for (NotificationItem n : notifications) {
            n.setRead(true);
        }
        dispatchNotifications();

        ApiClient.getInstance().getApi().markAllNotificationsAsRead(phone)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        Log.i(TAG, "All notifications marked as read on MySQL");
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        Log.w(TAG, "Failed to mark notifications read on backend: " + t.getMessage());
                    }
                });
    }

    public synchronized void markAllAsRead() {
        markAllAsRead(null);
    }

    public synchronized int getUnreadCount() {
        int count = 0;
        for (NotificationItem n : notifications) {
            if (!n.isRead()) {
                count++;
            }
        }
        return count;
    }
}
