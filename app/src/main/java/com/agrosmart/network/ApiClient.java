package com.agrosmart.network;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/";
    // Default USB reverse port-forwarding IP (http://127.0.0.1:8080/api/)
    // With automatic fallback to Wi-Fi LAN IP if USB is unplugged
    private static final String BACKEND_BASE_URL = "http://127.0.0.1:8080/api/";
    private static final String WIFI_FALLBACK_HOST = "10.10.115.182";

    private static Retrofit weatherRetrofit = null;
    private static Retrofit backendRetrofit = null;
    private static ApiClient instance = null;
    private AgroSmartApiService apiService = null;

    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public AgroSmartApiService getApi() {
        if (apiService == null) {
            apiService = getBackendClient().create(AgroSmartApiService.class);
        }
        return apiService;
    }

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

            // Interceptor to automatically fallback between USB (127.0.0.1) and Wi-Fi LAN IP
            Interceptor connectionFallbackInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    try {
                        return chain.proceed(request);
                    } catch (IOException primaryException) {
                        HttpUrl url = request.url();
                        String currentHost = url.host();
                        String targetHost = (currentHost.equals("127.0.0.1") || currentHost.equals("localhost"))
                                ? WIFI_FALLBACK_HOST
                                : "127.0.0.1";

                        HttpUrl fallbackUrl = url.newBuilder().host(targetHost).build();
                        Request fallbackRequest = request.newBuilder().url(fallbackUrl).build();
                        try {
                            return chain.proceed(fallbackRequest);
                        } catch (IOException fallbackException) {
                            throw primaryException;
                        }
                    }
                }
            };

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(connectionFallbackInterceptor)
                    .addInterceptor(logging)
                    .connectTimeout(6, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
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
