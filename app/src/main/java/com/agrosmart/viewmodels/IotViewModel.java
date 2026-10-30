package com.agrosmart.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.agrosmart.models.IotAlert;
import com.agrosmart.models.IotDevice;
import com.agrosmart.models.IrrigationSetting;
import com.agrosmart.models.PumpCommand;
import com.agrosmart.models.SensorReading;
import com.agrosmart.repository.IotRepository;

import java.util.List;

public class IotViewModel extends AndroidViewModel {

    private final IotRepository repository;
    private String currentDeviceId = "ESP32-AGRO-01";

    public IotViewModel(@NonNull Application application) {
        super(application);
        this.repository = IotRepository.getInstance(application);
        refresh();
    }

    public void setDeviceId(String deviceId) {
        this.currentDeviceId = deviceId;
        refresh();
    }

    public String getCurrentDeviceId() {
        return currentDeviceId;
    }

    public void refresh() {
        repository.refreshAll(currentDeviceId);
    }

    public void pollLatest() {
        repository.fetchLatestReadingFromNetwork(currentDeviceId);
        repository.fetchHistoryFromNetwork(currentDeviceId, 20);
    }

    public LiveData<IotDevice> getDevice() {
        return repository.getDeviceLiveData();
    }

    public LiveData<SensorReading> getLatestReading() {
        return repository.getLatestReadingLiveData();
    }

    public LiveData<List<SensorReading>> getHistory() {
        return repository.getHistoryLiveData();
    }

    public LiveData<IrrigationSetting> getSettings() {
        return repository.getSettingsLiveData();
    }

    public LiveData<PumpCommand> getLatestPumpCommand() {
        return repository.getPumpCommandLiveData();
    }

    public LiveData<List<IotAlert>> getAlerts() {
        return repository.getAlertsLiveData();
    }

    public LiveData<Boolean> getIsOnline() {
        return repository.getIsOnlineLiveData();
    }

    public LiveData<Boolean> getIsLoading() {
        return repository.getIsLoadingLiveData();
    }

    public LiveData<String> getStatusMessage() {
        return repository.getStatusMessageLiveData();
    }

    public void togglePump(boolean turnOn, Integer durationSeconds, IotRepository.OperationCallback callback) {
        String command = turnOn ? "PUMP_ON" : "PUMP_OFF";
        repository.triggerPumpCommand(currentDeviceId, command, durationSeconds, "MANUAL", callback);
    }

    public void saveSettings(IrrigationSetting setting, IotRepository.OperationCallback callback) {
        repository.saveSettings(setting, callback);
    }

    public void updateLocation(String location, IotRepository.OperationCallback callback) {
        repository.updateDeviceLocation(currentDeviceId, location, callback);
    }
}

