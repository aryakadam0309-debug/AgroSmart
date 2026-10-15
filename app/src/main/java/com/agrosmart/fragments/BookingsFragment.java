package com.agrosmart.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.adapters.BookingAdapter;
import com.agrosmart.databinding.FragmentBookingsBinding;
import com.agrosmart.viewmodels.TractorViewModel;

public class BookingsFragment extends Fragment {

    private FragmentBookingsBinding binding;
    private TractorViewModel viewModel;
    private BookingAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBookingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(TractorViewModel.class);

        adapter = new BookingAdapter(booking -> {
            viewModel.cancelBooking(booking.getId());
            Toast.makeText(requireContext(), "Booking #" + booking.getId() + " cancelled", Toast.LENGTH_SHORT).show();
        });

        binding.rvBookings.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvBookings.setAdapter(adapter);

        viewModel.getBookings().observe(getViewLifecycleOwner(), list -> {
            adapter.setItems(list);
            if (list == null || list.isEmpty()) {
                binding.tvEmptyBookings.setVisibility(View.VISIBLE);
                binding.rvBookings.setVisibility(View.GONE);
            } else {
                binding.tvEmptyBookings.setVisibility(View.GONE);
                binding.rvBookings.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
