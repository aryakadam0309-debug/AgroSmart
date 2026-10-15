package com.agrosmart.network;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/";
    // 10.0.2.2 is standard Android emulator loopback alias to host machine localhost:8080
    private static final String BACKEND_BASE_URL = "http://10.0.2.2:8080/api/";

    private static Retrofit weatherRetrofit = null;
    private static Retrofit backendRetrofit = null;

    public static Retrofit getClient() {
        return getWeatherClient();
    }

    public static Retrofit getWeatherClient() {
        if (weatherRetrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build();

            weatherRetrofit = new Retrofit.Builder()
                    .baseUrl(WEATHER_BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return weatherRetrofit;
    }

    public static Retrofit getBackendClient() {
        if (backendRetrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build();

            backendRetrofit = new Retrofit.Builder()
                    .baseUrl(BACKEND_BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return backendRetrofit;
    }
}
