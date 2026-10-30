package com.agrosmart.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.agrosmart.database.AgroSmartDbHelper;
import com.agrosmart.models.Tractor;
import com.agrosmart.models.TractorBooking;
import com.agrosmart.network.ApiClient;
import com.agrosmart.utils.SessionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TractorRepository {

    private static final String TAG = "TractorRepository";
    private final AgroSmartDbHelper dbHelper;
    private final SessionManager sessionManager;
    private final MutableLiveData<List<Tractor>> tractorsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<TractorBooking>> bookingsLiveData = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(String message, int statusCode);
    }

    public TractorRepository(Context context) {
        this.dbHelper = AgroSmartDbHelper.getInstance(context);
        this.sessionManager = new SessionManager(context);
    }

    public LiveData<List<Tractor>> getTractorsLiveData() {
        return tractorsLiveData;
    }

    public LiveData<List<TractorBooking>> getBookingsLiveData() {
        return bookingsLiveData;
    }

    public void loadTractors(String brand) {
        // Load local SQLite tractors first
        executor.execute(() -> {
            List<Tractor> local = dbHelper.getAllTractors(brand);
            mainHandler.post(() -> tractorsLiveData.setValue(local));
        });

        // Sync from Spring Boot backend
        ApiClient.getInstance().getApi().getTractors(brand)
                .enqueue(new Callback<List<Tractor>>() {
                    @Override
                    public void onResponse(Call<List<Tractor>> call, Response<List<Tractor>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Tractor> remote = response.body();
                            tractorsLiveData.postValue(remote);
                            executor.execute(() -> {
                                for (Tractor t : remote) {
                                    dbHelper.addTractor(t);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Tractor>> call, Throwable t) {
                        Log.w(TAG, "Backend unreachable, using SQLite local tractor catalog: " + t.getMessage());
                    }
                });
    }

    public void confirmBooking(TractorBooking booking, RepositoryCallback<TractorBooking> callback) {
        String phone = sessionManager.getUserPhone();
        String name = sessionManager.getUserName();
        if (phone == null || phone.trim().isEmpty()) {
            phone = "9876543210";
        }
        if (name == null || name.trim().isEmpty()) {
            name = "Farmer";
        }
        booking.setFarmerPhone(phone);
        booking.setFarmerName(name);
        booking.setStatus("CONFIRMED");
        booking.setPaymentMethod("DIRECT_WITH_OWNER");
        booking.setPaymentStatus("DIRECT_WITH_OWNER");

        ApiClient.getInstance().getApi().createBooking(booking)
                .enqueue(new Callback<TractorBooking>() {
                    @Override
                    public void onResponse(Call<TractorBooking> call, Response<TractorBooking> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            TractorBooking created = response.body();
                            executor.execute(() -> dbHelper.addBooking(created));
                            if (callback != null) callback.onSuccess(created);
                        } else {
                            String err = "Sorry, this tractor has just been booked for your selected dates. Please refresh availability or choose another tractor or date range.";
                            try {
                                if (response.errorBody() != null) {
                                    String raw = response.errorBody().string();
                                    if (raw.contains("Sorry, this tractor has just been booked") || raw.contains("error")) {
                                        err = raw;
                                    }
                                }
                            } catch (Exception ignored) {}
                            if (callback != null) callback.onError(err, response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<TractorBooking> call, Throwable t) {
                        Log.w(TAG, "Backend unreachable for booking confirmation, caching locally: " + t.getMessage());
                        if (booking.getId() == null || booking.getId().isEmpty()) {
                            booking.setId("TB-LOCAL-" + System.currentTimeMillis());
                        }
                        executor.execute(() -> dbHelper.addBooking(booking));
                        mainHandler.post(() -> {
                            if (callback != null) callback.onSuccess(booking);
                        });
                    }
                });
    }

    public void holdAndCreateBooking(TractorBooking booking, RepositoryCallback<TractorBooking> callback) {
        String phone = sessionManager.getUserPhone();
        String name = sessionManager.getUserName();
        booking.setFarmerPhone(phone);
        booking.setFarmerName(name);

        ApiClient.getInstance().getApi().createBooking(booking)
                .enqueue(new Callback<TractorBooking>() {
                    @Override
                    public void onResponse(Call<TractorBooking> call, Response<TractorBooking> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            TractorBooking created = response.body();
                            executor.execute(() -> dbHelper.addBooking(created));
                            if (callback != null) callback.onSuccess(created);
                        } else {
                            String err = "Date conflict: Tractor already reserved on this date.";
                            try {
                                if (response.errorBody() != null) err = response.errorBody().string();
                            } catch (Exception ignored) {}
                            if (callback != null) callback.onError(err, response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<TractorBooking> call, Throwable t) {
                        Log.w(TAG, "Backend unreachable for booking hold, falling back to local SQLite: " + t.getMessage());
                        executor.execute(() -> dbHelper.addBooking(booking));
                        mainHandler.post(() -> {
                            if (callback != null) callback.onSuccess(booking);
                        });
                    }
                });
    }

    public void createPaymentOrder(String bookingId, RepositoryCallback<Map<String, Object>> callback) {
        String phone = sessionManager.getUserPhone();
        Map<String, String> payload = new HashMap<>();
        payload.put("bookingId", bookingId);
        payload.put("phone", phone);

        ApiClient.getInstance().getApi().createTractorPaymentOrder(payload)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (callback != null) callback.onSuccess(response.body());
                        } else {
                            String err = "Unable to create payment order.";
                            try {
                                if (response.errorBody() != null) err = response.errorBody().string();
                            } catch (Exception ignored) {}
                            if (callback != null) callback.onError(err, response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        Log.w(TAG, "Backend unreachable for payment order, creating local order details: " + t.getMessage());
                        Map<String, Object> fallbackOrder = new HashMap<>();
                        fallbackOrder.put("bookingId", bookingId);
                        fallbackOrder.put("orderId", "order_local_" + System.currentTimeMillis());
                        fallbackOrder.put("amount", 210000L);
                        fallbackOrder.put("currency", "INR");
                        fallbackOrder.put("keyId", "rzp_test_AgroSmartDemo123");
                        mainHandler.post(() -> {
                            if (callback != null) callback.onSuccess(fallbackOrder);
                        });
                    }
                });
    }

    public void verifyPayment(String bookingId, String paymentId, String orderId, String signature,
                              RepositoryCallback<TractorBooking> callback) {
        String phone = sessionManager.getUserPhone();
        Map<String, String> payload = new HashMap<>();
        payload.put("bookingId", bookingId);
        payload.put("paymentId", paymentId);
        payload.put("orderId", orderId);
        payload.put("signature", signature);
        payload.put("phone", phone);

        ApiClient.getInstance().getApi().verifyTractorPayment(payload)
                .enqueue(new Callback<TractorBooking>() {
                    @Override
                    public void onResponse(Call<TractorBooking> call, Response<TractorBooking> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            TractorBooking confirmed = response.body();
                            executor.execute(() -> dbHelper.addBooking(confirmed));
                            if (callback != null) callback.onSuccess(confirmed);
                        } else {
                            String err = "Payment verification failed.";
                            try {
                                if (response.errorBody() != null) err = response.errorBody().string();
                            } catch (Exception ignored) {}
                            if (callback != null) callback.onError(err, response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<TractorBooking> call, Throwable t) {
                        Log.w(TAG, "Backend unreachable for payment verification, updating local booking: " + t.getMessage());
                        executor.execute(() -> {
                            dbHelper.updateBookingStatus(bookingId, "CONFIRMED");
                            List<TractorBooking> all = dbHelper.getAllBookings();
                            TractorBooking target = null;
                            for (TractorBooking b : all) {
                                if (bookingId.equalsIgnoreCase(b.getId())) {
                                    target = b;
                                    break;
                                }
                            }
                            if (target == null) {
                                target = new TractorBooking();
                                target.setId(bookingId);
                                target.setStatus("CONFIRMED");
                            }
                            final TractorBooking confirmed = target;
                            mainHandler.post(() -> {
                                if (callback != null) callback.onSuccess(confirmed);
                            });
                        });
                    }
                });
    }

    public void loadFarmerBookings() {
        String phone = sessionManager.getUserPhone();
        executor.execute(() -> {
            List<TractorBooking> local = dbHelper.getAllBookings();
            mainHandler.post(() -> bookingsLiveData.setValue(local));
        });

        ApiClient.getInstance().getApi().getFarmerBookings(phone)
                .enqueue(new Callback<List<TractorBooking>>() {
                    @Override
                    public void onResponse(Call<List<TractorBooking>> call, Response<List<TractorBooking>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<TractorBooking> remote = response.body();
                            bookingsLiveData.postValue(remote);
                            executor.execute(() -> {
                                for (TractorBooking b : remote) {
                                    dbHelper.addBooking(b);
                                }
                            });
                        } else {
                            executor.execute(() -> {
                                List<TractorBooking> local = dbHelper.getAllBookings();
                                mainHandler.post(() -> bookingsLiveData.setValue(local));
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TractorBooking>> call, Throwable t) {
                        Log.w(TAG, "Backend unreachable for bookings: " + t.getMessage());
                        executor.execute(() -> {
                            List<TractorBooking> local = dbHelper.getAllBookings();
                            mainHandler.post(() -> bookingsLiveData.setValue(local));
                        });
                    }
                });
    }

    public void submitUpiPayment(String bookingId, String upiTxnRef, String paymentMethod, RepositoryCallback<TractorBooking> callback) {
        String phone = sessionManager.getUserPhone();
        Map<String, String> payload = new HashMap<>();
        payload.put("upiTransactionRef", upiTxnRef);
        payload.put("paymentMethod", paymentMethod != null ? paymentMethod : "DIRECT_UPI");
        payload.put("phone", phone);

        ApiClient.getInstance().getApi().submitUpiPayment(bookingId, payload)
                .enqueue(new Callback<TractorBooking>() {
                    @Override
                    public void onResponse(Call<TractorBooking> call, Response<TractorBooking> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            TractorBooking updated = response.body();
                            executor.execute(() -> {
                                dbHelper.addBooking(updated);
                                dbHelper.updateBookingStatus(bookingId, "AWAITING_VERIFICATION");
                            });
                            loadFarmerBookings();
                            if (callback != null) callback.onSuccess(updated);
                        } else {
                            String err = "Submission failed";
                            try {
                                if (response.errorBody() != null) err = response.errorBody().string();
                            } catch (Exception ignored) {}
                            if (callback != null) callback.onError(err, response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<TractorBooking> call, Throwable t) {
                        executor.execute(() -> {
                            TractorBooking local = null;
                            for (TractorBooking b : dbHelper.getAllBookings()) {
                                if (bookingId.equalsIgnoreCase(b.getId())) {
                                    local = b;
                                    break;
                                }
                            }
                            if (local != null) {
                                local.setUpiTransactionRef(upiTxnRef);
                                local.setPaymentMethod(paymentMethod);
                                local.setStatus("AWAITING_VERIFICATION");
                                local.setPaymentStatus("PAYMENT_SUBMITTED");
                                dbHelper.addBooking(local);
                            }
                            final TractorBooking res = local;
                            mainHandler.post(() -> {
                                if (callback != null) callback.onSuccess(res);
                            });
                        });
                    }
                });
    }

    public void verifyDirectPayment(String bookingId, boolean approved, String notes, RepositoryCallback<TractorBooking> callback) {
        String phone = sessionManager.getUserPhone();
        Map<String, Object> payload = new HashMap<>();
        payload.put("approved", approved);
        payload.put("notes", notes != null ? notes : "");
        payload.put("phone", phone);

        ApiClient.getInstance().getApi().verifyDirectPayment(bookingId, payload)
                .enqueue(new Callback<TractorBooking>() {
                    @Override
                    public void onResponse(Call<TractorBooking> call, Response<TractorBooking> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            TractorBooking updated = response.body();
                            executor.execute(() -> {
                                dbHelper.addBooking(updated);
                                dbHelper.updateBookingStatus(bookingId, updated.getStatus());
                            });
                            if (callback != null) callback.onSuccess(updated);
                        } else {
                            String err = "Verification failed";
                            try {
                                if (response.errorBody() != null) err = response.errorBody().string();
                            } catch (Exception ignored) {}
                            if (callback != null) callback.onError(err, response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<TractorBooking> call, Throwable t) {
                        if (callback != null) callback.onError("Network error: " + t.getMessage(), 500);
                    }
                });
    }

    public void cancelBooking(String bookingId, String reason, RepositoryCallback<TractorBooking> callback) {
        String phone = sessionManager.getUserPhone();
        Map<String, String> payload = new HashMap<>();
        payload.put("reason", reason);
        payload.put("phone", phone);

        ApiClient.getInstance().getApi().cancelBooking(bookingId, payload)
                .enqueue(new Callback<TractorBooking>() {
                    @Override
                    public void onResponse(Call<TractorBooking> call, Response<TractorBooking> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            TractorBooking cancelled = response.body();
                            executor.execute(() -> dbHelper.updateBookingStatus(bookingId, "CANCELLED"));
                            loadFarmerBookings();
                            if (callback != null) callback.onSuccess(cancelled);
                        } else {
                            String err = "Cancellation failed.";
                            try {
                                if (response.errorBody() != null) err = response.errorBody().string();
                            } catch (Exception ignored) {}
                            if (callback != null) callback.onError(err, response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<TractorBooking> call, Throwable t) {
                        if (callback != null) callback.onError("Network error: " + t.getMessage(), 500);
                    }
                });
    }

    public void registerTractor(Tractor tractor, RepositoryCallback<Tractor> callback) {
        executor.execute(() -> dbHelper.addTractor(tractor));
        ApiClient.getInstance().getApi().addTractor(tractor)
                .enqueue(new Callback<Tractor>() {
                    @Override
                    public void onResponse(Call<Tractor> call, Response<Tractor> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Tractor saved = response.body();
                            executor.execute(() -> dbHelper.addTractor(saved));
                            if (callback != null) callback.onSuccess(saved);
                        } else {
                            if (callback != null) callback.onSuccess(tractor);
                        }
                    }

                    @Override
                    public void onFailure(Call<Tractor> call, Throwable t) {
                        if (callback != null) callback.onSuccess(tractor);
                    }
                });
    }

    public void loadAllTractorsAdmin(RepositoryCallback<List<Tractor>> callback) {
        ApiClient.getInstance().getApi().getAllTractorsAdmin()
                .enqueue(new Callback<List<Tractor>>() {
                    @Override
                    public void onResponse(Call<List<Tractor>> call, Response<List<Tractor>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (callback != null) callback.onSuccess(response.body());
                        } else {
                            executor.execute(() -> {
                                List<Tractor> local = dbHelper.getAllTractors(null);
                                mainHandler.post(() -> {
                                    if (callback != null) callback.onSuccess(local);
                                });
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Tractor>> call, Throwable t) {
                        executor.execute(() -> {
                            List<Tractor> local = dbHelper.getAllTractors(null);
                            mainHandler.post(() -> {
                                if (callback != null) callback.onSuccess(local);
                            });
                        });
                    }
                });
    }

    public void setTractorApproval(String tractorId, boolean approve, RepositoryCallback<Tractor> callback) {
        ApiClient.getInstance().getApi().approveTractor(tractorId, approve)
                .enqueue(new Callback<Tractor>() {
                    @Override
                    public void onResponse(Call<Tractor> call, Response<Tractor> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Tractor updated = response.body();
                            executor.execute(() -> dbHelper.addTractor(updated));
                            if (callback != null) callback.onSuccess(updated);
                        } else {
                            if (callback != null) callback.onError("Failed to update status", response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Tractor> call, Throwable t) {
                        if (callback != null) callback.onError(t.getMessage(), 500);
                    }
                });
    }

    public void searchNearbyTractors(double lat, double lon, Double radiusKm, String bookingDate, String brand, RepositoryCallback<List<Tractor>> callback) {
        ApiClient.getInstance().getApi().getNearbyTractors(lat, lon, radiusKm, bookingDate, brand)
                .enqueue(new Callback<List<Tractor>>() {
                    @Override
                    public void onResponse(Call<List<Tractor>> call, Response<List<Tractor>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Tractor> remote = response.body();
                            tractorsLiveData.postValue(remote);
                            if (callback != null) callback.onSuccess(remote);
                        } else {
                            fallbackLocalNearbySearch(lat, lon, radiusKm, brand, callback);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Tractor>> call, Throwable t) {
                        Log.w(TAG, "Backend unreachable for nearby tractors, falling back to SQLite: " + t.getMessage());
                        fallbackLocalNearbySearch(lat, lon, radiusKm, brand, callback);
                    }
                });
    }

    private void fallbackLocalNearbySearch(double lat, double lon, Double radiusKm, String brand, RepositoryCallback<List<Tractor>> callback) {
        executor.execute(() -> {
            List<Tractor> local = dbHelper.getAllTractors(brand);
            List<Tractor> filtered = new java.util.ArrayList<>();
            boolean isAll = (radiusKm == null) || (radiusKm > 500.0);
            double maxR = isAll ? 10000.0 : radiusKm;
            for (Tractor t : local) {
                if (!t.isAvailable() || !"APPROVED".equalsIgnoreCase(t.getApprovalStatus())) continue;
                if (t.getLatitude() != null && t.getLongitude() != null) {
                    double dist = calculateHaversineDistanceKm(lat, lon, t.getLatitude(), t.getLongitude());
                    if (isAll || dist <= maxR) {
                        t.setDistanceKm(Math.round(dist * 10.0) / 10.0);
                        filtered.add(t);
                    }
                }
            }
            filtered.sort((a, b) -> Double.compare(
                    a.getDistanceKm() != null ? a.getDistanceKm() : Double.MAX_VALUE,
                    b.getDistanceKm() != null ? b.getDistanceKm() : Double.MAX_VALUE
            ));
            mainHandler.post(() -> {
                tractorsLiveData.setValue(filtered);
                if (callback != null) callback.onSuccess(filtered);
            });
        });
    }

    public void loadAdminActiveBookings(RepositoryCallback<List<TractorBooking>> callback) {
        ApiClient.getInstance().getApi().getAdminActiveBookings()
                .enqueue(new Callback<List<TractorBooking>>() {
                    @Override
                    public void onResponse(Call<List<TractorBooking>> call, Response<List<TractorBooking>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (callback != null) callback.onSuccess(response.body());
                        } else {
                            if (callback != null) callback.onError("Failed to fetch active bookings", response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TractorBooking>> call, Throwable t) {
                        executor.execute(() -> {
                            List<TractorBooking> local = dbHelper.getAllBookings();
                            mainHandler.post(() -> {
                                if (callback != null) callback.onSuccess(local);
                            });
                        });
                    }
                });
    }

    public void loadAdminHistoricalBookings(RepositoryCallback<List<TractorBooking>> callback) {
        ApiClient.getInstance().getApi().getAdminHistoricalBookings()
                .enqueue(new Callback<List<TractorBooking>>() {
                    @Override
                    public void onResponse(Call<List<TractorBooking>> call, Response<List<TractorBooking>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (callback != null) callback.onSuccess(response.body());
                        } else {
                            if (callback != null) callback.onError("Failed to fetch historical bookings", response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TractorBooking>> call, Throwable t) {
                        executor.execute(() -> {
                            List<TractorBooking> local = dbHelper.getAllBookings();
                            mainHandler.post(() -> {
                                if (callback != null) callback.onSuccess(local);
                            });
                        });
                    }
                });
    }

    public static double calculateHaversineDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2.0) * Math.sin(dLat / 2.0)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2.0) * Math.sin(dLon / 2.0);
        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));
        return R * c;
    }
}
