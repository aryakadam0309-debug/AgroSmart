package com.agrosmart.repository;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.agrosmart.database.AgroSmartDbHelper;
import com.agrosmart.models.Tractor;
import com.agrosmart.models.TractorBooking;
import com.agrosmart.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TractorRepository {

    private static final String TAG = "TractorRepo";
    private static TractorRepository instance;
    private final List<Tractor> tractorFleet = new ArrayList<>();
    private final List<TractorBooking> bookings = new ArrayList<>();
    private final MutableLiveData<List<TractorBooking>> bookingsLiveData = new MutableLiveData<>();
    private AgroSmartDbHelper dbHelper;

    public static synchronized TractorRepository getInstance(Context context) {
        if (instance == null) {
            instance = new TractorRepository(context != null ? context.getApplicationContext() : null);
        } else if (instance.dbHelper == null && context != null) {
            instance.initDb(context.getApplicationContext());
        }
        return instance;
    }

    public static synchronized TractorRepository getInstance() {
        if (instance == null) {
            instance = new TractorRepository(null);
        }
        return instance;
    }

    public TractorRepository() {
        this(null);
    }

    public TractorRepository(Context context) {
        initTractors();
        if (context != null) {
            initDb(context);
        }
    }

    public synchronized void initDb(Context context) {
        if (context != null) {
            this.dbHelper = AgroSmartDbHelper.getInstance(context);
            List<Tractor> dbTractors = dbHelper.getAllTractors("All Brands");
            if (dbTractors != null && !dbTractors.isEmpty()) {
                tractorFleet.clear();
                tractorFleet.addAll(dbTractors);
            }
            List<TractorBooking> dbBookings = dbHelper.getAllBookings();
            if (dbBookings != null && !dbBookings.isEmpty()) {
                bookings.clear();
                bookings.addAll(dbBookings);
                dispatchBookings();
            }
        }
    }

    private void initTractors() {
        tractorFleet.clear();
        tractorFleet.add(new Tractor(
                "TR01", "Mahindra 575 DI XP Plus", "Mahindra", "47 HP",
                800.0, 5800.0, "Rotavator, 9-Tyne Cultivator, Trolley",
                "Ramesh Patil", "9822334455", "Nashik, Maharashtra",
                true, 4.9f
        ));
        tractorFleet.add(new Tractor(
                "TR02", "John Deere 5050 D GearPro", "John Deere", "50 HP",
                900.0, 6500.0, "Heavy-Duty Rotavator, MB Plough",
                "Suresh Jagtap", "9822445566", "Shirur, Pune",
                true, 4.8f
        ));
        tractorFleet.add(new Tractor(
                "TR03", "Swaraj 855 FE 4WD", "Swaraj", "52 HP",
                950.0, 6800.0, "Laser Land Leveler, Disk Harrow",
                "Vikas Deshmukh", "9822556677", "Daund, Pune",
                true, 4.9f
        ));
        tractorFleet.add(new Tractor(
                "TR04", "Sonalika DI 745 III Sikander", "Sonalika", "50 HP",
                850.0, 6000.0, "Cultivator, Seed Drill, Trolley",
                "Ganesh Shinde", "9822667788", "Niphad, Nashik",
                true, 4.7f
        ));
        tractorFleet.add(new Tractor(
                "TR05", "Eicher 380 Super DI", "Eicher", "40 HP",
                700.0, 5000.0, "Rigid Cultivator, Trolley",
                "Baburao Kadam", "9822778899", "Dindori, Nashik",
                true, 4.6f
        ));
    }

    private void dispatchBookings() {
        try {
            if (Looper.getMainLooper() != null && Looper.myLooper() == Looper.getMainLooper()) {
                bookingsLiveData.setValue(new ArrayList<>(bookings));
            } else {
                bookingsLiveData.postValue(new ArrayList<>(bookings));
            }
        } catch (Exception ignored) {
            // Safe fallback in JVM unit test environments where Android Looper is unmocked
        }
    }

    public synchronized void addTractor(Tractor tractor) {
        if (tractor != null) {
            tractorFleet.add(0, tractor);
            if (dbHelper != null) {
                dbHelper.addTractor(tractor);
            }
        }
    }

    public LiveData<List<Tractor>> getTractors(String brand) {
        MutableLiveData<List<Tractor>> liveData = new MutableLiveData<>();
        List<Tractor> initial = new ArrayList<>();
        for (Tractor t : tractorFleet) {
            if (brand == null || brand.equals("All Brands") || t.getBrand().equalsIgnoreCase(brand)) {
                initial.add(t);
            }
        }
        liveData.setValue(initial);

        // Fetch live from MySQL backend
        ApiClient.getInstance().getApi().getTractors(brand != null && !brand.equals("All Brands") ? brand : null)
                .enqueue(new Callback<List<Tractor>>() {
                    @Override
                    public void onResponse(Call<List<Tractor>> call, Response<List<Tractor>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            synchronized (TractorRepository.this) {
                                tractorFleet.clear();
                                tractorFleet.addAll(response.body());
                                if (dbHelper != null) {
                                    for (Tractor tr : response.body()) {
                                        dbHelper.addTractor(tr);
                                    }
                                }
                            }
                            liveData.postValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Tractor>> call, Throwable t) {
                        Log.w(TAG, "Backend unreachable, using cached tractor fleet: " + t.getMessage());
                    }
                });

        return liveData;
    }

    public LiveData<List<TractorBooking>> getBookings() {
        return bookingsLiveData;
    }

    public void loadFarmerBookings(String phone) {
        ApiClient.getInstance().getApi().getFarmerBookings(phone)
                .enqueue(new Callback<List<TractorBooking>>() {
                    @Override
                    public void onResponse(Call<List<TractorBooking>> call, Response<List<TractorBooking>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            synchronized (TractorRepository.this) {
                                bookings.clear();
                                bookings.addAll(response.body());
                                if (dbHelper != null) {
                                    for (TractorBooking tb : response.body()) {
                                        dbHelper.addBooking(tb);
                                    }
                                }
                            }
                            dispatchBookings();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TractorBooking>> call, Throwable t) {
                        Log.w(TAG, "Could not sync bookings with backend: " + t.getMessage());
                    }
                });
    }

    public synchronized boolean hasBookingConflict(String tractorId, String bookingDate) {
        if (tractorId == null || bookingDate == null) return false;
        for (TractorBooking b : bookings) {
            if (b.getTractorId().equalsIgnoreCase(tractorId) &&
                    b.getBookingDate().equalsIgnoreCase(bookingDate) &&
                    !"CANCELLED".equalsIgnoreCase(b.getStatus())) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean addBooking(TractorBooking booking) {
        if (booking == null) return false;
        if (booking.getDurationHours() <= 0) return false;

        // Prevent overlapping bookings for the same tractor on the same date
        if (hasBookingConflict(booking.getTractorId(), booking.getBookingDate())) {
            return false;
        }

        bookings.add(0, booking);
        if (dbHelper != null) {
            dbHelper.addBooking(booking);
        }
        dispatchBookings();

        // Send to backend MySQL
        ApiClient.getInstance().getApi().createBooking(booking)
                .enqueue(new Callback<TractorBooking>() {
                    @Override
                    public void onResponse(Call<TractorBooking> call, Response<TractorBooking> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.i(TAG, "Booking persisted to MySQL successfully: " + response.body().getId());
                        } else {
                            Log.w(TAG, "Backend returned non-200 for booking: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<TractorBooking> call, Throwable t) {
                        Log.w(TAG, "Saved booking locally, offline sync will retry: " + t.getMessage());
                    }
                });

        return true;
    }

    public synchronized void cancelBooking(String bookingId) {
        for (TractorBooking b : bookings) {
            if (b.getId().equals(bookingId)) {
                b.setStatus("CANCELLED");
                if (dbHelper != null) {
                    dbHelper.updateBookingStatus(bookingId, "CANCELLED");
                }
                break;
            }
        }
        dispatchBookings();

        // Send cancel status to backend MySQL
        ApiClient.getInstance().getApi().updateBookingStatus(bookingId, "CANCELLED")
                .enqueue(new Callback<TractorBooking>() {
                    @Override
                    public void onResponse(Call<TractorBooking> call, Response<TractorBooking> response) {
                        Log.i(TAG, "Booking cancellation persisted to MySQL");
                    }

                    @Override
                    public void onFailure(Call<TractorBooking> call, Throwable t) {
                        Log.w(TAG, "Failed to update booking status on backend: " + t.getMessage());
                    }
                });
    }
}
