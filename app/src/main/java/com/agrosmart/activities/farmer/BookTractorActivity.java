package com.agrosmart.activities.farmer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.agrosmart.R;
import com.agrosmart.activities.BaseActivity;
import com.agrosmart.databinding.ActivityBookTractorBinding;
import com.agrosmart.models.Tractor;
import com.agrosmart.models.TractorBooking;
import com.agrosmart.repository.TractorRepository;
import com.agrosmart.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BookTractorActivity extends BaseActivity {

    private ActivityBookTractorBinding binding;
    private TractorRepository repository;
    private Tractor tractor;
    private SessionManager sessionManager;
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookTractorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new TractorRepository(this);
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
        binding.tvBookingRateSummary.setText(String.format(Locale.US, "Rate: ₹%,.0f/hr • %s • %s",
                tractor.getHourlyRate(), tractor.getHp(), tractor.getLocation()));
        binding.tvOwnerContact.setText("Equipment Owner: " +
                (tractor.getOwnerName() != null ? tractor.getOwnerName() : "Partner Fleet") + " (" +
                (tractor.getOwnerPhone() != null ? tractor.getOwnerPhone() : "") + ")");

        // Owner/Driver Contact Card
        String driverName = tractor.getOwnerPaymentName() != null && !tractor.getOwnerPaymentName().trim().isEmpty()
                ? tractor.getOwnerPaymentName().trim()
                : (tractor.getOwnerName() != null ? tractor.getOwnerName() : "Partner Driver");
        String driverPhone = tractor.getOwnerPhone() != null && !tractor.getOwnerPhone().trim().isEmpty()
                ? tractor.getOwnerPhone().trim()
                : "9822001122";

        binding.tvOwnerPayeeName.setText("Driver: " + driverName);
        binding.tvOwnerPayeePhone.setText("Contact: " + driverPhone);

        binding.btnCallDriverDirect.setOnClickListener(v -> {
            try {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + driverPhone));
                startActivity(callIntent);
            } catch (Exception e) {
                Toast.makeText(this, "Could not open dialer: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Pre-fill date (Default tomorrow)
        String passedDate = getIntent().getStringExtra("EXTRA_DEFAULT_DATE");
        if (passedDate != null && !passedDate.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                Date d = sdf.parse(passedDate);
                if (d != null) calendar.setTime(d);
            } catch (Exception ignored) {}
        } else {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        updateDateDisplay();

        // Pre-fill farm location if available
        if (binding.etFarmLocation.getText() == null || binding.etFarmLocation.getText().toString().trim().isEmpty()) {
            String defaultLoc = sessionManager.getUserLocation();
            if (defaultLoc == null || defaultLoc.trim().isEmpty() || defaultLoc.contains("Mumbai")) {
                defaultLoc = "Survey 42, " + (tractor.getLocation() != null ? tractor.getLocation() : "Karad");
            }
            binding.etFarmLocation.setText(defaultLoc);
        }

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
            dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
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

        binding.btnConfirmReservation.setOnClickListener(v -> onConfirmBookingClicked());
    }

    private void updateDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        binding.etBookingDate.setText(sdf.format(calendar.getTime()));
    }

    private void calculateTotal() {
        String hoursStr = binding.etBookingHours.getText() != null ? binding.etBookingHours.getText().toString().trim() : "0";
        int hours = 0;
        try {
            hours = Integer.parseInt(hoursStr);
        } catch (NumberFormatException ignored) {}

        double total = hours * tractor.getHourlyRate();
        binding.tvEstimatedTotal.setText(String.format(Locale.US, "₹%,.0f", total));
    }

    private boolean validateInputs() {
        String location = binding.etFarmLocation.getText() != null ? binding.etFarmLocation.getText().toString().trim() : "";
        if (location.isEmpty()) {
            binding.etFarmLocation.setError("Please enter farm location or survey number");
            binding.etFarmLocation.requestFocus();
            Toast.makeText(this, "⚠️ Please enter farm location or survey number", Toast.LENGTH_SHORT).show();
            return false;
        }

        String hoursStr = binding.etBookingHours.getText() != null ? binding.etBookingHours.getText().toString().trim() : "1";
        int hours = 1;
        try {
            hours = Integer.parseInt(hoursStr);
        } catch (NumberFormatException ignored) {}

        if (hours < 1) {
            binding.etBookingHours.setError("Booking duration must be at least 1 hour");
            binding.etBookingHours.requestFocus();
            Toast.makeText(this, "⚠️ Booking duration must be at least 1 hour", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void onConfirmBookingClicked() {
        if (!validateInputs()) return;

        binding.btnConfirmReservation.setEnabled(false);
        binding.pbBooking.setVisibility(View.VISIBLE);

        int hours = 1;
        try {
            hours = Integer.parseInt(binding.etBookingHours.getText().toString().trim());
        } catch (Exception ignored) {}

        double total = hours * tractor.getHourlyRate();
        String bookingDate = binding.etBookingDate.getText().toString().trim();
        String farmLoc = binding.etFarmLocation.getText().toString().trim();
        String notes = binding.etBookingNotes.getText() != null ? binding.etBookingNotes.getText().toString().trim() : "";

        String userPhone = sessionManager != null ? sessionManager.getUserPhone() : null;
        if (userPhone == null || userPhone.trim().isEmpty()) {
            userPhone = "9876543210";
        }
        String userName = sessionManager != null ? sessionManager.getUserName() : null;
        if (userName == null || userName.trim().isEmpty()) {
            userName = "Farmer";
        }

        TractorBooking booking = new TractorBooking();
        booking.setTractorId(tractor.getId());
        booking.setTractorModel(tractor.getModelName());
        booking.setFarmerPhone(userPhone);
        booking.setFarmerName(userName);
        booking.setBookingDate(bookingDate);
        booking.setDurationHours(hours);
        booking.setFarmLocation(farmLoc);
        booking.setNotes(notes);
        booking.setTotalAmount(total);
        booking.setStatus("CONFIRMED");
        booking.setPaymentMethod("DIRECT_WITH_OWNER");
        booking.setPaymentStatus("DIRECT_WITH_OWNER");

        repository.confirmBooking(booking, new TractorRepository.RepositoryCallback<TractorBooking>() {
            @Override
            public void onSuccess(TractorBooking result) {
                binding.pbBooking.setVisibility(View.GONE);
                binding.btnConfirmReservation.setEnabled(true);
                showBookingConfirmationDialog(result);
            }

            @Override
            public void onError(String message, int statusCode) {
                binding.pbBooking.setVisibility(View.GONE);
                binding.btnConfirmReservation.setEnabled(true);

                if (statusCode == 409 || (message != null && (message.contains("conflict") || message.contains("booked")))) {
                    showConflictDialog(message);
                } else {
                    Toast.makeText(BookTractorActivity.this, "Reservation error: " + message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showBookingConfirmationDialog(TractorBooking confirmed) {
        String driverName = confirmed.getOwnerName() != null ? confirmed.getOwnerName() : tractor.getOwnerName();
        String driverPhone = confirmed.getOwnerPhone() != null ? confirmed.getOwnerPhone() : tractor.getOwnerPhone();

        String message = "Booking Reference: #" + confirmed.getId() + "\n\n" +
                "🚜 Equipment: " + confirmed.getTractorModel() + "\n" +
                "📅 Schedule: " + confirmed.getBookingDate() + " (" + confirmed.getDurationHours() + " hrs)\n" +
                "💰 Estimated Rate: ₹" + Math.round(confirmed.getTotalAmount()) + "\n" +
                "📍 Location: " + confirmed.getFarmLocation() + "\n\n" +
                "Driver / Owner: " + driverName + " (" + driverPhone + ")\n\n" +
                "AgroSmart does not collect in-app payment. Please coordinate service delivery and settle payment directly with the tractor owner.";

        new AlertDialog.Builder(this)
                .setTitle("🎉 Tractor Reservation Confirmed!")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("📞 Call Driver", (dialog, which) -> {
                    if (driverPhone != null && !driverPhone.isEmpty()) {
                        try {
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse("tel:" + driverPhone.replaceAll("[^0-9+]", "")));
                            startActivity(callIntent);
                        } catch (Exception ignored) {}
                    }
                    navigateToMyBookings();
                })
                .setNeutralButton("View My Bookings", (dialog, which) -> {
                    navigateToMyBookings();
                })
                .show();
    }

    private void showConflictDialog(String rawMessage) {
        String displayMsg = "Sorry, this tractor has just been booked for your selected dates. Please refresh availability or choose another tractor or date range.";
        new AlertDialog.Builder(this)
                .setTitle("Tractor Unavailable")
                .setMessage(displayMsg)
                .setPositiveButton("Choose Another Date", (dialog, which) -> {
                    binding.etBookingDate.performClick();
                })
                .setNegativeButton("Back to Search", (dialog, which) -> finish())
                .show();
    }

    private void navigateToMyBookings() {
        Intent intent = new Intent(this, MyBookingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
