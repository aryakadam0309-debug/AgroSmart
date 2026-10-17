package com.agrosmart.activities.farmer;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.agrosmart.databinding.ActivityBookTractorBinding;
import com.agrosmart.models.Tractor;
import com.agrosmart.models.TractorBooking;
import com.agrosmart.activities.BaseActivity;
import com.agrosmart.utils.SessionManager;
import com.agrosmart.viewmodels.TractorViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BookTractorActivity extends BaseActivity {

    private ActivityBookTractorBinding binding;
    private TractorViewModel viewModel;
    private Tractor tractor;
    private SessionManager sessionManager;
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookTractorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(TractorViewModel.class);
        sessionManager = new SessionManager(this);

        tractor = (Tractor) getIntent().getSerializableExtra("EXTRA_TRACTOR");
        if (tractor == null) {
            Toast.makeText(this, "Tractor details unavailable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.toolbarBooking.setNavigationOnClickListener(v -> finish());
        setupUI();
    }

    private void setupUI() {
        binding.tvBookingTractorName.setText(tractor.getModelName());
        binding.tvBookingRateSummary.setText(String.format("Rate: ₹%,.0f/hr • %s • %s",
                tractor.getHourlyRate(), tractor.getHp(), tractor.getLocation()));

        // Default date = Tomorrow
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        updateDateDisplay();

        binding.etBookingDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateDisplay();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dialog.getDatePicker().setMinDate(System.currentTimeMillis());
            dialog.show();
        });

        binding.etBookingHours.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateTotal();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        calculateTotal();

        binding.btnSubmitBooking.setOnClickListener(v -> submitBooking());
    }

    private void updateDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        binding.etBookingDate.setText(sdf.format(calendar.getTime()));
    }

    private void calculateTotal() {
        String hoursStr = binding.etBookingHours.getText() != null ? binding.etBookingHours.getText().toString().trim() : "0";
        int hours = 0;
        try {
            hours = Integer.parseInt(hoursStr);
        } catch (NumberFormatException ignored) {}

        double total = hours * tractor.getHourlyRate();
        binding.tvEstimatedTotal.setText(String.format("₹%,.0f", total));
    }

    private void submitBooking() {
        String location = binding.etFarmLocation.getText() != null ? binding.etFarmLocation.getText().toString().trim() : "";
        if (location.isEmpty()) {
            binding.etFarmLocation.setError("Please enter farm location or survey number");
            return;
        }

        String hoursStr = binding.etBookingHours.getText() != null ? binding.etBookingHours.getText().toString().trim() : "0";
        int hours = 1;
        try {
            hours = Integer.parseInt(hoursStr);
        } catch (NumberFormatException ignored) {}

        if (hours < 1) {
            binding.etBookingHours.setError("Booking duration must be at least 1 hour");
            return;
        }

        double total = hours * tractor.getHourlyRate();
        String bookingId = "BK-" + (System.currentTimeMillis() % 10000);
        String bookingDate = binding.etBookingDate.getText().toString();
        String notes = binding.etBookingNotes.getText() != null ? binding.etBookingNotes.getText().toString().trim() : "";
        String now = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());

        TractorBooking booking = new TractorBooking(
                bookingId, tractor.getId(), tractor.getModelName(),
                sessionManager.getUserName(), sessionManager.getUserPhone(),
                bookingDate, hours, total, location, notes, "PENDING", now
        );

        boolean success = viewModel.submitBooking(booking);
        if (success) {
            Toast.makeText(this, "Tractor booking submitted successfully! ID: " + bookingId, Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "This tractor is already booked for " + bookingDate + ". Please select another date or tractor.", Toast.LENGTH_LONG).show();
        }
    }
}
