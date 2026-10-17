package com.agrosmart.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.agrosmart.models.NotificationItem;

import java.util.ArrayList;
import java.util.List;

public class NotificationRepository {

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
        initSampleNotifications();
    }

    private void initSampleNotifications() {
        notifications.clear();
        notifications.add(new NotificationItem(
                "N01", "🌧️ Rain Advisory for Western Maharashtra",
                "Light to moderate thundershowers expected in next 24 hours. Ensure drainage channels are clear.",
                "Today, 10:00 AM", "WEATHER", false
        ));
        notifications.add(new NotificationItem(
                "N02", "📈 Soybean Mandi Price Surge",
                "Indore Mandi recorded ₹4,820/Qtl (+₹75). Peak demand window for selling.",
                "Today, 08:30 AM", "PRICE", false
        ));
        notifications.add(new NotificationItem(
                "N03", "🏛️ PM-KUSUM Solar Scheme Deadline",
                "Application window for 90% solar water pump subsidy closes in 5 days.",
                "Yesterday", "SCHEME", true
        ));
        notifications.add(new NotificationItem(
                "N04", "🚜 Tractor Rental Confirmed",
                "Your booking #BK-1001 for Mahindra 575 DI is confirmed for 15 Sep.",
                "2 days ago", "BOOKING", true
        ));
        dispatchNotifications();
    }

    private void dispatchNotifications() {
        try {
            if (android.os.Looper.getMainLooper() != null && android.os.Looper.myLooper() == android.os.Looper.getMainLooper()) {
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

    public synchronized void addNotification(NotificationItem item) {
        if (item != null) {
            notifications.add(0, item);
            dispatchNotifications();
        }
    }

    public synchronized void addNotification(String id, String title, String message, String timestamp, String type, boolean isRead) {
        addNotification(new NotificationItem(id, title, message, timestamp, type, isRead));
    }

    public synchronized void markAllAsRead() {
        for (NotificationItem n : notifications) {
            n.setRead(true);
        }
        if (android.os.Looper.myLooper() == android.os.Looper.getMainLooper()) {
            notifLiveData.setValue(new ArrayList<>(notifications));
        } else {
            notifLiveData.postValue(new ArrayList<>(notifications));
        }
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
