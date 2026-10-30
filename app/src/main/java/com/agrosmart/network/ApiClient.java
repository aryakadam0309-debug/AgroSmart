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
    // With automatic fallback to Wi-Fi / Hotspot LAN IP (10.10.189.191)
    private static final String BACKEND_BASE_URL = "http://10.10.211.218:8080/api/";
    private static final String WIFI_FALLBACK_HOST = "127.0.0.1";
    private static volatile String preferredHost = "10.10.211.218";

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

    public static AgroSmartApiService getApiService(android.content.Context context) {
        return getInstance().getApi();
    }

    public static AgroSmartApiService getApiService() {
        return getInstance().getApi();
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

            // Interceptor to seamlessly route between Wi-Fi Hotspot IP (10.10.189.52) and USB reverse (127.0.0.1)
            Interceptor connectionFallbackInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request originalRequest = chain.request();
                    HttpUrl originalUrl = originalRequest.url();

                    // If preferredHost is different from request URL host, rewrite first
                    Request requestToTry = originalRequest;
                    if (!originalUrl.host().equals(preferredHost)) {
                        HttpUrl rewrittenUrl = originalUrl.newBuilder().host(preferredHost).build();
                        requestToTry = originalRequest.newBuilder().url(rewrittenUrl).build();
                    }

                    try {
                        return chain.proceed(requestToTry);
                    } catch (IOException primaryException) {
                        // Alternate host
                        String altHost = preferredHost.equals(WIFI_FALLBACK_HOST) ? "127.0.0.1" : WIFI_FALLBACK_HOST;
                        HttpUrl fallbackUrl = originalUrl.newBuilder().host(altHost).build();
                        Request fallbackRequest = originalRequest.newBuilder().url(fallbackUrl).build();
                        try {
                            Response fallbackResponse = chain.proceed(fallbackRequest);
                            // Switch preferredHost so subsequent requests avoid timeout
                            preferredHost = altHost;
                            return fallbackResponse;
                        } catch (IOException fallbackException) {
                            throw primaryException;
                        }
                    }
                }
            };

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(connectionFallbackInterceptor)
                    .addInterceptor(logging)
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
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

