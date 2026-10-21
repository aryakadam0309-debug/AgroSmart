package com.agrosmart.network;

import com.agrosmart.models.CropPrice;
import com.agrosmart.models.Tractor;
import com.agrosmart.models.TractorBooking;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
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

