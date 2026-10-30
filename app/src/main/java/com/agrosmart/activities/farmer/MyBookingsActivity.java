package com.agrosmart.activities.farmer;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.activities.BaseActivity;
import com.agrosmart.adapters.BookingAdapter;
import com.agrosmart.databinding.ActivityMyBookingsBinding;
import com.agrosmart.models.TractorBooking;
import com.agrosmart.repository.TractorRepository;

import java.util.List;

public class MyBookingsActivity extends BaseActivity {

    private ActivityMyBookingsBinding binding;
    private TractorRepository repository;
    private BookingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyBookingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new TractorRepository(this);

        binding.toolbarMyBookings.setNavigationOnClickListener(v -> finish());

        binding.btnBrowseTractors.setOnClickListener(v -> finish());

        setupRecyclerView();
        observeBookings();

        binding.pbLoadingBookings.setVisibility(View.VISIBLE);
        repository.loadFarmerBookings();

        binding.swipeRefreshBookings.setOnRefreshListener(() -> {
            repository.loadFarmerBookings();
            binding.swipeRefreshBookings.setRefreshing(false);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        repository.loadFarmerBookings();
    }

    private void setupRecyclerView() {
        adapter = new BookingAdapter(booking -> {
            // Confirm cancellation
            new AlertDialog.Builder(this)
                    .setTitle("Cancel Booking #" + booking.getId() + "?")
                    .setMessage("Are you sure you want to cancel this booking for " + booking.getTractorModel() + " on " + booking.getBookingDate() + "?")
                    .setPositiveButton("Confirm Cancel", (dialog, which) -> {
                        repository.cancelBooking(booking.getId(), "Cancelled by farmer request",
                                new TractorRepository.RepositoryCallback<TractorBooking>() {
                                    @Override
                                    public void onSuccess(TractorBooking result) {
                                        Toast.makeText(MyBookingsActivity.this, "Booking cancelled successfully", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onError(String message, int statusCode) {
                                        Toast.makeText(MyBookingsActivity.this, message, Toast.LENGTH_SHORT).show();
                                    }
                                });
                    })
                    .setNegativeButton("Keep Booking", null)
                    .show();
        });

        binding.rvBookings.setLayoutManager(new LinearLayoutManager(this));
        binding.rvBookings.setAdapter(adapter);
    }

    private void observeBookings() {
        repository.getBookingsLiveData().observe(this, list -> {
            binding.pbLoadingBookings.setVisibility(View.GONE);
            adapter.submitList(list);
            if (list == null || list.isEmpty()) {
                binding.layoutEmptyBookings.setVisibility(View.VISIBLE);
                binding.tvEmptyBookings.setVisibility(View.GONE);
                binding.rvBookings.setVisibility(View.GONE);
            } else {
                binding.layoutEmptyBookings.setVisibility(View.GONE);
                binding.tvEmptyBookings.setVisibility(View.GONE);
                binding.rvBookings.setVisibility(View.VISIBLE);
            }
        });
    }
}
