package com.agrosmart.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.agrosmart.models.User;
import com.agrosmart.repository.AuthRepository;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository repository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);
    private final MutableLiveData<User> authUserSuccess = new MutableLiveData<>(null);
    private final MutableLiveData<String> passwordResetSuccess = new MutableLiveData<>(null);

    public AuthViewModel(@NonNull Application application) {
        super(application);
        this.repository = new AuthRepository(application);
    }

    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<User> getAuthUserSuccess() { return authUserSuccess; }
    public LiveData<String> getPasswordResetSuccess() { return passwordResetSuccess; }

    public void login(String identifier, String password, String role) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        repository.login(identifier, password, role, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                isLoading.postValue(false);
                authUserSuccess.postValue(user);
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }

    public void register(String fullName, String phone, String email, String password, String location, double farmSize) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        repository.register(fullName, phone, email, password, location, farmSize, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                isLoading.postValue(false);
                authUserSuccess.postValue(user);
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }

    public void resetPassword(String phone, String newPassword) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        repository.resetPassword(phone, newPassword, new AuthRepository.ResetCallback() {
            @Override
            public void onSuccess(String message) {
                isLoading.postValue(false);
                passwordResetSuccess.postValue(message);
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }
}
