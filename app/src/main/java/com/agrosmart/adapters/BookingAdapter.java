package com.agrosmart.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.databinding.ItemBookingBinding;
import com.agrosmart.models.TractorBooking;

import java.util.ArrayList;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    public interface OnBookingActionListener {
        void onCancelClick(TractorBooking booking);
    }

    private final List<TractorBooking> bookingList = new ArrayList<>();
    private final OnBookingActionListener listener;

    public BookingAdapter(OnBookingActionListener listener) {
        this.listener = listener;
    }

    public void submitList(List<TractorBooking> list) {
        bookingList.clear();
        if (list != null) {
            bookingList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBookingBinding binding = ItemBookingBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new BookingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        holder.bind(bookingList.get(position));
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    class BookingViewHolder extends RecyclerView.ViewHolder {
        private final ItemBookingBinding binding;

        BookingViewHolder(ItemBookingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(TractorBooking booking) {
            binding.tvBookingId.setText("Booking #" + booking.getId());
            String status = booking.getStatus() != null ? booking.getStatus().toUpperCase() : "PENDING_PAYMENT";
            binding.tvBookingStatus.setText(status);

            if ("CONFIRMED".equals(status)) {
                binding.tvBookingStatus.setTextColor(0xFF2E7D32); // Green
                binding.btnCancelBooking.setVisibility(View.VISIBLE);
            } else if ("ACTIVE".equals(status)) {
                binding.tvBookingStatus.setTextColor(0xFF0288D1); // Light Blue / Active Teal
                binding.btnCancelBooking.setVisibility(View.GONE);
            } else if ("COMPLETED".equals(status)) {
                binding.tvBookingStatus.setTextColor(0xFF455A64); // Slate Grey
                binding.btnCancelBooking.setVisibility(View.GONE);
            } else if ("AWAITING_VERIFICATION".equals(status)) {
                binding.tvBookingStatus.setTextColor(0xFFD97706); // Amber
                binding.tvBookingStatus.setText("AWAITING VERIFICATION");
                binding.btnCancelBooking.setVisibility(View.VISIBLE);
            } else if ("CANCELLED".equals(status) || "FAILED".equals(status) || "EXPIRED".equals(status)) {
                binding.tvBookingStatus.setTextColor(0xFFBA1A1A); // Red
                binding.btnCancelBooking.setVisibility(View.GONE);
            } else {
                binding.tvBookingStatus.setTextColor(0xFFED6C02); // Orange / Pending
                binding.btnCancelBooking.setVisibility(View.VISIBLE);
            }

            // Driver / Owner contact details (Strictly shown for CONFIRMED or ACTIVE bookings to authorized renter)
            if ("CONFIRMED".equals(status) || "ACTIVE".equals(status)) {
                binding.layoutDriverContact.setVisibility(View.VISIBLE);
                String ownerName = booking.getOwnerName();
                String ownerPhone = booking.getOwnerPhone();
                binding.tvDriverName.setText("🚜 Driver / Owner: " + (ownerName != null && !ownerName.trim().isEmpty() ? ownerName : "Assigned Driver"));
                binding.tvDriverPhone.setText("📞 Phone: " + (ownerPhone != null && !ownerPhone.trim().isEmpty() ? ownerPhone : "Available via Support"));

                if (ownerPhone != null && !ownerPhone.trim().isEmpty()) {
                    binding.btnCallDriver.setEnabled(true);
                    binding.btnCallDriver.setOnClickListener(v -> {
                        Context context = v.getContext();
                        try {
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse("tel:" + ownerPhone.trim()));
                            context.startActivity(callIntent);
                        } catch (Exception e) {
                            Toast.makeText(context, "Unable to launch dialer: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    binding.btnCallDriver.setEnabled(false);
                }
            } else {
                binding.layoutDriverContact.setVisibility(View.GONE);
            }

            binding.tvBookingTractor.setText(booking.getTractorModel());
            binding.tvBookingSchedule.setText("📅 Date: " + booking.getBookingDate() + " • Duration: " + booking.getDurationHours() + " Hours");
            binding.tvBookingLocation.setText("📍 Location: " + booking.getFarmLocation());

            String paymentMethod = booking.getPaymentMethod() != null ? booking.getPaymentMethod() : "Direct UPI";
            String payDetails = "💳 Payment: " + paymentMethod;
            if (booking.getUpiTransactionRef() != null && !booking.getUpiTransactionRef().trim().isEmpty()) {
                payDetails += " • UTR: " + booking.getUpiTransactionRef();
            } else if (booking.getPaymentId() != null && !booking.getPaymentId().trim().isEmpty()) {
                payDetails += " (" + booking.getPaymentId() + ")";
            }
            binding.tvPaymentInfo.setText(payDetails);

            binding.tvBookingTotal.setText(String.format("Total: ₹%,.0f", booking.getTotalAmount()));

            binding.btnCancelBooking.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCancelClick(booking);
                }
            });
        }
    }
}
