package com.agrosmart.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.agrosmart.database.AgroSmartDbHelper;
import com.agrosmart.models.IotAlert;
import com.agrosmart.models.IotDevice;
import com.agrosmart.models.IrrigationSetting;
import com.agrosmart.models.PumpCommand;
import com.agrosmart.models.SensorReading;
import com.agrosmart.network.ApiClient;
import com.agrosmart.network.AgroSmartApiService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IotRepository {

    private static IotRepository instance;
    private final AgroSmartDbHelper dbHelper;
    private final AgroSmartApiService apiService;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final MutableLiveData<IotDevice> deviceLiveData = new MutableLiveData<>();
    private final MutableLiveData<SensorReading> latestReadingLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<SensorReading>> historyLiveData = new MutableLiveData<>();
    private final MutableLiveData<IrrigationSetting> settingsLiveData = new MutableLiveData<>();
    private final MutableLiveData<PumpCommand> pumpCommandLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<IotAlert>> alertsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isOnlineLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> statusMessageLiveData = new MutableLiveData<>();

    public interface OperationCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public static synchronized IotRepository getInstance(Context context) {
        if (instance == null) {
            instance = new IotRepository(context.getApplicationContext());
        }
        return instance;
    }

    private IotRepository(Context context) {
        this.dbHelper = AgroSmartDbHelper.getInstance(context);
        this.apiService = ApiClient.getBackendClient().create(AgroSmartApiService.class);
    }

    public LiveData<IotDevice> getDeviceLiveData() { return deviceLiveData; }
    public LiveData<SensorReading> getLatestReadingLiveData() { return latestReadingLiveData; }
    public LiveData<List<SensorReading>> getHistoryLiveData() { return historyLiveData; }
    public LiveData<IrrigationSetting> getSettingsLiveData() { return settingsLiveData; }
    public LiveData<PumpCommand> getPumpCommandLiveData() { return pumpCommandLiveData; }
    public LiveData<List<IotAlert>> getAlertsLiveData() { return alertsLiveData; }
    public LiveData<Boolean> getIsOnlineLiveData() { return isOnlineLiveData; }
    public LiveData<Boolean> getIsLoadingLiveData() { return isLoadingLiveData; }
    public LiveData<String> getStatusMessageLiveData() { return statusMessageLiveData; }

    public void loadLocalCache(String deviceId) {
        executorService.execute(() -> {
            IotDevice dev = dbHelper.getIotDevice(deviceId);
            SensorReading latest = dbHelper.getLatestSensorReading(deviceId);
            List<SensorReading> history = dbHelper.getSensorReadingsHistory(deviceId, 20);
            IrrigationSetting settings = dbHelper.getIrrigationSetting(deviceId);
            PumpCommand cmd = dbHelper.getLatestPumpCommand(deviceId);
            List<IotAlert> alerts = dbHelper.getActiveAlerts(deviceId);

            mainHandler.post(() -> {
                if (dev != null) deviceLiveData.setValue(dev);
                if (latest != null) latestReadingLiveData.setValue(latest);
                if (history != null) historyLiveData.setValue(history);
                if (settings != null) settingsLiveData.setValue(settings);
                if (cmd != null) pumpCommandLiveData.setValue(cmd);
                if (alerts != null) alertsLiveData.setValue(alerts);
            });
        });
    }

    public void refreshAll(String deviceId) {
        isLoadingLiveData.setValue(true);
        // Step 1: Load local cache immediately so UI is responsive
        loadLocalCache(deviceId);

        // Step 2: Query network backend
        apiService.getDeviceStatus(deviceId).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                isLoadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> body = response.body();
                    boolean online = Boolean.TRUE.equals(body.get("isOnline"));
                    isOnlineLiveData.setValue(online);
                    statusMessageLiveData.setValue("Device online and synced with backend");
                    // Sync readings, settings, alerts
                    fetchLatestReadingFromNetwork(deviceId);
                    fetchHistoryFromNetwork(deviceId, 20);
                    fetchSettingsFromNetwork(deviceId);
                    fetchAlertsFromNetwork(deviceId);
                } else {
                    isOnlineLiveData.setValue(false);
                    statusMessageLiveData.setValue("Running in Offline Cache Mode");
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                isLoadingLiveData.setValue(false);
                isOnlineLiveData.setValue(false);
                statusMessageLiveData.setValue("Backend unreachable. Using local SQLite cache.");
            }
        });
    }

    public void fetchLatestReadingFromNetwork(String deviceId) {
        apiService.getLatestSensorReading(deviceId).enqueue(new Callback<SensorReading>() {
            @Override
            public void onResponse(Call<SensorReading> call, Response<SensorReading> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SensorReading r = response.body();
                    latestReadingLiveData.setValue(r);
                    executorService.execute(() -> dbHelper.insertSensorReading(r));
                }
            }

            @Override
            public void onFailure(Call<SensorReading> call, Throwable t) {
                // Keep local cached reading
            }
        });
    }

    public void fetchHistoryFromNetwork(String deviceId, int limit) {
        apiService.getSensorHistory(deviceId, limit).enqueue(new Callback<List<SensorReading>>() {
            @Override
            public void onResponse(Call<List<SensorReading>> call, Response<List<SensorReading>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<SensorReading> list = response.body();
                    historyLiveData.setValue(list);
                    executorService.execute(() -> {
                        for (SensorReading r : list) {
                            dbHelper.insertSensorReading(r);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<SensorReading>> call, Throwable t) {
                // Retain local history
            }
        });
    }

    public void fetchSettingsFromNetwork(String deviceId) {
        apiService.getIrrigationSettings(deviceId).enqueue(new Callback<IrrigationSetting>() {
            @Override
            public void onResponse(Call<IrrigationSetting> call, Response<IrrigationSetting> response) {
                if (response.isSuccessful() && response.body() != null) {
                    IrrigationSetting s = response.body();
                    settingsLiveData.setValue(s);
                    executorService.execute(() -> dbHelper.saveIrrigationSetting(s));
                }
            }

            @Override
            public void onFailure(Call<IrrigationSetting> call, Throwable t) {
                // Keep local settings
            }
        });
    }

    public void saveSettings(IrrigationSetting setting, OperationCallback callback) {
        isLoadingLiveData.setValue(true);
        // Optimistically update local
        executorService.execute(() -> dbHelper.saveIrrigationSetting(setting));
        settingsLiveData.setValue(setting);

        apiService.saveIrrigationSettings(setting).enqueue(new Callback<IrrigationSetting>() {
            @Override
            public void onResponse(Call<IrrigationSetting> call, Response<IrrigationSetting> response) {
                isLoadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    IrrigationSetting saved = response.body();
                    settingsLiveData.setValue(saved);
                    executorService.execute(() -> dbHelper.saveIrrigationSetting(saved));
                    if (callback != null) callback.onSuccess("Irrigation settings updated successfully");
                } else {
                    if (callback != null) callback.onSuccess("Settings saved locally in offline cache");
                }
            }

            @Override
            public void onFailure(Call<IrrigationSetting> call, Throwable t) {
                isLoadingLiveData.setValue(false);
                if (callback != null) callback.onSuccess("Saved locally in SQLite (backend offline)");
            }
        });
    }

    public void triggerPumpCommand(String deviceId, String command, Integer durationSeconds, String triggerSource, OperationCallback callback) {
        isLoadingLiveData.setValue(true);
        String cmdId = "CMD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String now = sdf.format(new Date());

        boolean requestedState = "PUMP_ON".equalsIgnoreCase(command);
        PumpCommand localCmd = new PumpCommand(cmdId, deviceId, command, durationSeconds, triggerSource,
                requestedState, null, "PENDING", null, now, null);

        // Update local state first
        pumpCommandLiveData.setValue(localCmd);
        executorService.execute(() -> dbHelper.insertPumpCommand(localCmd));

        apiService.sendPumpCommand(localCmd).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                isLoadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> body = response.body();
                    String status = (String) body.get("status");
                    Boolean confirmed = (Boolean) body.get("confirmedState");
                    String msg = (String) body.get("message");

                    localCmd.setStatus(status != null ? status : "SENT");
                    localCmd.setConfirmedState(confirmed);
                    pumpCommandLiveData.setValue(localCmd);
                    executorService.execute(() -> dbHelper.updatePumpCommandStatus(localCmd.getCommandId(), localCmd.getStatus(), localCmd.getConfirmedState()));

                    if (callback != null) callback.onSuccess(msg != null ? msg : "Command queued for ESP32 station");
                } else {
                    if (callback != null) callback.onError("Backend rejected command: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                isLoadingLiveData.setValue(false);
                // In offline mode, simulate local execution response
                localCmd.setStatus("EXECUTED_OFFLINE_SIM");
                localCmd.setConfirmedState(requestedState);
                pumpCommandLiveData.setValue(localCmd);
                executorService.execute(() -> dbHelper.updatePumpCommandStatus(localCmd.getCommandId(), "EXECUTED_OFFLINE_SIM", requestedState));
                if (callback != null) callback.onSuccess("Pump command updated locally in offline mode");
            }
        });
    }

    public void fetchAlertsFromNetwork(String deviceId) {
        apiService.getIotAlerts(deviceId).enqueue(new Callback<List<IotAlert>>() {
            @Override
            public void onResponse(Call<List<IotAlert>> call, Response<List<IotAlert>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<IotAlert> list = response.body();
                    alertsLiveData.setValue(list);
                    executorService.execute(() -> {
                        for (IotAlert a : list) {
                            dbHelper.insertIotAlert(a);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<IotAlert>> call, Throwable t) {
                // Retain local alerts
            }
        });
    }

    public void updateDeviceLocation(String deviceId, String location, OperationCallback callback) {
        executorService.execute(() -> {
            boolean ok = dbHelper.updateIotDeviceLocation(deviceId, location);
            IotDevice current = deviceLiveData.getValue();
            if (current != null) {
                current.setFieldLocation(location);
            } else {
                current = dbHelper.getIotDevice(deviceId);
            }
            final IotDevice updatedDev = current;
            mainHandler.post(() -> {
                if (updatedDev != null) {
                    deviceLiveData.setValue(updatedDev);
                }
                if (ok && callback != null) {
                    callback.onSuccess("Location updated successfully");
                } else if (!ok && callback != null) {
                    callback.onError("Failed to update location in database");
                }
            });
        });
    }
}
