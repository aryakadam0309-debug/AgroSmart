package com.agrosmart.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {

    @GET("weather")
    Call<ResponseBody> getCurrentWeatherByCoords(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("units") String units,
            @Query("appid") String apiKey
    );

    @GET("weather")
    Call<ResponseBody> getCurrentWeatherByCity(
            @Query("q") String city,
            @Query("units") String units,
            @Query("appid") String apiKey
    );

    @GET("forecast")
    Call<ResponseBody> getForecastByCoords(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("units") String units,
            @Query("appid") String apiKey
    );
}
