package com.agrosmart.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.agrosmart.R;
import com.agrosmart.databinding.ItemBookingBinding;
import com.agrosmart.models.TractorBooking;

import java.util.ArrayList;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    public interface OnCancelClickListener {
        void onCancel(TractorBooking booking);
    }

    private final List<TractorBooking> items = new ArrayList<>();
    private final OnCancelClickListener cancelListener;

    public BookingAdapter(OnCancelClickListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public void setItems(List<TractorBooking> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
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
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class BookingViewHolder extends RecyclerView.ViewHolder {
        private final ItemBookingBinding binding;

        BookingViewHolder(ItemBookingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(TractorBooking b) {
            binding.tvBookingId.setText("Booking #" + b.getId());
            binding.tvBookingTractor.setText(b.getTractorModel());
            binding.tvBookingSchedule.setText("📅 " + b.getBookingDate() + " • " + b.getDurationHours() + " Hours");
            binding.tvBookingLocation.setText("📍 " + b.getFarmLocation());
            binding.tvBookingTotal.setText(String.format("Total: ₹%,.0f", b.getTotalAmount()));
            binding.tvBookingStatus.setText(b.getStatus());

            if ("CONFIRMED".equalsIgnoreCase(b.getStatus())) {
                binding.tvBookingStatus.setBackgroundResource(R.drawable.bg_trend_up);
                binding.tvBookingStatus.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.status_success));
                binding.btnCancelBooking.setVisibility(View.GONE);
            } else if ("CANCELLED".equalsIgnoreCase(b.getStatus())) {
                binding.tvBookingStatus.setBackgroundResource(R.drawable.bg_trend_down);
                binding.tvBookingStatus.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.status_error));
                binding.btnCancelBooking.setVisibility(View.GONE);
            } else {
                binding.tvBookingStatus.setBackgroundResource(R.drawable.bg_trend_stable);
                binding.tvBookingStatus.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.harvest_amber));
                binding.btnCancelBooking.setVisibility(View.VISIBLE);
            }

            binding.btnCancelBooking.setOnClickListener(v -> {
                if (cancelListener != null) {
                    cancelListener.onCancel(b);
                }
            });
        }
    }
}
