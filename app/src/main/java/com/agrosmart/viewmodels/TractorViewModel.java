package com.agrosmart.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.agrosmart.models.Tractor;
import com.agrosmart.models.TractorBooking;
import com.agrosmart.repository.TractorRepository;

import java.util.List;

public class TractorViewModel extends ViewModel {

    private final TractorRepository repository;
    private final MutableLiveData<List<Tractor>> tractors = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private String currentBrand = "All Brands";

    public TractorViewModel() {
        this.repository = TractorRepository.getInstance();
        loadTractors();
    }

    public LiveData<List<Tractor>> getTractors() { return tractors; }
    public LiveData<List<TractorBooking>> getBookings() { return repository.getBookings(); }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void loadTractors() {
        isLoading.setValue(true);
        LiveData<List<Tractor>> res = repository.getTractors(currentBrand);
        tractors.setValue(res.getValue());
        isLoading.setValue(false);
    }

    public void filterByBrand(String brand) {
        this.currentBrand = brand;
        loadTractors();
    }

    public boolean submitBooking(TractorBooking booking) {
        return repository.addBooking(booking);
    }

    public void cancelBooking(String bookingId) {
        repository.cancelBooking(bookingId);
    }
}
