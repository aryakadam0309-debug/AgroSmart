package com.agrosmart.network;

import com.agrosmart.models.AgriOrder;
import com.agrosmart.models.CropDisease;
import com.agrosmart.models.CropGuide;
import com.agrosmart.models.CropHarvest;
import com.agrosmart.models.CropPrice;
import com.agrosmart.models.FertilizerItem;
import com.agrosmart.models.GovernmentScheme;
import com.agrosmart.models.MarketProduct;
import com.agrosmart.models.NewsItem;
import com.agrosmart.models.NotificationItem;
import com.agrosmart.models.Tractor;
import com.agrosmart.models.TractorBooking;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AgroSmartApiService {

    // Authentication & User Identity (MySQL Persisted)
    @POST("auth/login")
    Call<Map<String, Object>> loginUser(
            @Body Map<String, String> credentials
    );

    @POST("auth/register")
    Call<Map<String, Object>> registerUser(
            @Body Map<String, Object> userData
    );

    @POST("auth/google")
    Call<Map<String, Object>> googleAuth(
            @Body Map<String, String> googlePayload
    );

    @PUT("auth/profile")
    Call<Map<String, Object>> updateProfile(
            @Body Map<String, Object> profileData
    );

    // Mandi Bhav / Crop Prices
    @GET("crops/prices")
    Call<List<CropPrice>> getCropPrices(
            @Query("search") String search,
            @Query("state") String state
    );

    // Tractors Fleet
    @GET("tractors")
    Call<List<Tractor>> getTractors(
            @Query("brand") String brand
    );

    @POST("tractors/book")
    Call<TractorBooking> createBooking(
            @Body TractorBooking booking
    );

    @GET("tractors/bookings")
    Call<List<TractorBooking>> getFarmerBookings(
            @Query("phone") String phone
    );

    @PUT("tractors/bookings/{id}/status")
    Call<TractorBooking> updateBookingStatus(
            @Path("id") String id,
            @Query("status") String status
    );

    // Admin Dashboard Stats
    @GET("admin/stats")
    Call<Map<String, Object>> getAdminStats();

    // Marketplace Products & Orders
    @GET("marketplace/products")
    Call<List<MarketProduct>> getProducts(
            @Query("category") String category,
            @Query("search") String search
    );

    @GET("marketplace/products/{id}")
    Call<MarketProduct> getProductById(
            @Path("id") String id
    );

    @POST("marketplace/orders")
    Call<AgriOrder> createOrder(
            @Body AgriOrder order
    );

    @GET("marketplace/orders")
    Call<List<AgriOrder>> getOrders(
            @Query("phone") String phone
    );

    // Inventory Management (Fertilizers & Crop Harvests)
    @GET("inventory/fertilizers")
    Call<List<FertilizerItem>> getFertilizers(
            @Query("phone") String phone
    );

    @POST("inventory/fertilizers")
    Call<FertilizerItem> addFertilizer(
            @Body FertilizerItem item
    );

    @DELETE("inventory/fertilizers/{id}")
    Call<Void> deleteFertilizer(
            @Path("id") String id
    );

    @GET("inventory/harvests")
    Call<List<CropHarvest>> getHarvests(
            @Query("phone") String phone
    );

    @POST("inventory/harvests")
    Call<CropHarvest> addHarvest(
            @Body CropHarvest item
    );

    @DELETE("inventory/harvests/{id}")
    Call<Void> deleteHarvest(
            @Path("id") String id
    );

    // Notifications & Announcements
    @GET("notifications")
    Call<List<NotificationItem>> getNotifications(
            @Query("phone") String phone
    );

    @POST("notifications")
    Call<NotificationItem> createNotification(
            @Body NotificationItem item
    );

    @PUT("notifications/{id}/read")
    Call<Void> markNotificationAsRead(
            @Path("id") String id
    );

    @PUT("notifications/read-all")
    Call<Map<String, Object>> markAllNotificationsAsRead(
            @Query("phone") String phone
    );

    // Agri News & Government Schemes
    @GET("news")
    Call<List<NewsItem>> getNews(
            @Query("category") String category
    );

    @GET("schemes")
    Call<List<GovernmentScheme>> getSchemes(
            @Query("category") String category
    );

    // Crop Diseases & Cultivation Guides
    @GET("crops/diseases")
    Call<List<CropDisease>> getCropDiseases(
            @Query("crop") String crop
    );

    @GET("crops/guides")
    Call<List<CropGuide>> getCropGuides();

    // IoT Smart Farming & Irrigation Module
    @GET("iot/status/{deviceId}")
    Call<Map<String, Object>> getDeviceStatus(
            @Path("deviceId") String deviceId
    );

    @GET("iot/reading/latest/{deviceId}")
    Call<com.agrosmart.models.SensorReading> getLatestSensorReading(
            @Path("deviceId") String deviceId
    );

    @GET("iot/reading/history/{deviceId}")
    Call<List<com.agrosmart.models.SensorReading>> getSensorHistory(
            @Path("deviceId") String deviceId,
            @Query("limit") Integer limit
    );

    @GET("iot/irrigation/settings/{deviceId}")
    Call<com.agrosmart.models.IrrigationSetting> getIrrigationSettings(
            @Path("deviceId") String deviceId
    );

    @POST("iot/irrigation/settings")
    Call<com.agrosmart.models.IrrigationSetting> saveIrrigationSettings(
            @Body com.agrosmart.models.IrrigationSetting setting
    );

    @POST("iot/pump/command")
    Call<Map<String, Object>> sendPumpCommand(
            @Body com.agrosmart.models.PumpCommand command
    );

    @GET("iot/alerts/{deviceId}")
    Call<List<com.agrosmart.models.IotAlert>> getIotAlerts(
            @Path("deviceId") String deviceId
    );
}

