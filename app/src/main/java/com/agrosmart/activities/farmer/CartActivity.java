package com.agrosmart.activities.farmer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.agrosmart.activities.BaseActivity;
import com.agrosmart.adapters.CartAdapter;
import com.agrosmart.databinding.ActivityCartBinding;
import com.agrosmart.models.AgriOrder;
import com.agrosmart.models.CartItem;
import com.agrosmart.utils.SessionManager;
import com.agrosmart.viewmodels.MarketplaceViewModel;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CartActivity extends BaseActivity implements PaymentResultListener {

    private ActivityCartBinding binding;
    private MarketplaceViewModel viewModel;
    private CartAdapter adapter;
    private SessionManager sessionManager;
    private double currentTotal = 0.0;
    private List<CartItem> currentItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Checkout.preload(getApplicationContext());
        viewModel = new ViewModelProvider(this).get(MarketplaceViewModel.class);
        sessionManager = new SessionManager(this);

        binding.toolbarCart.setNavigationOnClickListener(v -> finish());

        // Prefill delivery address with farmer's registered location
        String userLoc = sessionManager.getUserLocation();
        if (userLoc != null && !userLoc.isEmpty()) {
            binding.etDeliveryAddress.setText(userLoc);
        }

        setupRecyclerView();
        observeCart();

        binding.btnPayRazorpay.setOnClickListener(v -> startRazorpayPayment());
        binding.btnPayCOD.setOnClickListener(v -> completeOrder("Cash on Delivery (COD)", "COD-OFFLINE"));
    }

    private void setupRecyclerView() {
        adapter = new CartAdapter((item, newQty) -> {
            viewModel.updateCartQty(item.getProduct().getId(), newQty);
        });
        binding.rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCartItems.setAdapter(adapter);
    }

    private void observeCart() {
        viewModel.getCart().observe(this, items -> {
            currentItems = items != null ? items : new ArrayList<>();
            adapter.setItems(currentItems);

            double subtotal = 0.0;
            for (CartItem it : currentItems) {
                subtotal += it.getSubtotal();
            }
            currentTotal = subtotal;

            binding.tvBillSubtotal.setText(String.format("₹%,.0f", subtotal));
            binding.tvBillTotal.setText(String.format("₹%,.0f", currentTotal));

            if (currentItems.isEmpty()) {
                binding.tvEmptyCart.setVisibility(View.VISIBLE);
                binding.cardSummary.setVisibility(View.GONE);
                binding.inputLayoutAddress.setVisibility(View.GONE);
                binding.btnPayRazorpay.setVisibility(View.GONE);
                binding.btnPayCOD.setVisibility(View.GONE);
            } else {
                binding.tvEmptyCart.setVisibility(View.GONE);
                binding.cardSummary.setVisibility(View.VISIBLE);
                binding.inputLayoutAddress.setVisibility(View.VISIBLE);
                binding.btnPayRazorpay.setVisibility(View.VISIBLE);
                binding.btnPayCOD.setVisibility(View.VISIBLE);
            }
        });
    }

    private void startRazorpayPayment() {
        if (currentTotal <= 0) return;

        String address = binding.etDeliveryAddress.getText() != null ? binding.etDeliveryAddress.getText().toString().trim() : "";
        if (address.isEmpty()) {
            binding.etDeliveryAddress.setError("Please enter your farm or village delivery address");
            binding.etDeliveryAddress.requestFocus();
            Toast.makeText(this, "Please enter your delivery address first.", Toast.LENGTH_SHORT).show();
            return;
        }

        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_AgroSmartDemo123");

        try {
            JSONObject options = new JSONObject();
            options.put("name", "AgroSmart Kisan Store");
            options.put("description", "Quality Agri-Inputs Order");
            options.put("currency", "INR");
            options.put("amount", Math.round(currentTotal * 100)); // paise

            JSONObject preFill = new JSONObject();
            preFill.put("email", sessionManager.getUserEmail().isEmpty() ? "farmer@agrosmart.in" : sessionManager.getUserEmail());
            preFill.put("contact", sessionManager.getUserPhone());
            options.put("prefill", preFill);

            checkout.open(this, options);
        } catch (Exception e) {
            Toast.makeText(this, "Error starting payment: " + e.getMessage() + "\nSwitching to COD.", Toast.LENGTH_SHORT).show();
            completeOrder("Cash on Delivery (Fallback)", "COD-" + System.currentTimeMillis());
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentId) {
        completeOrder("Razorpay Online (UPI/Card)", razorpayPaymentId);
    }

    @Override
    public void onPaymentError(int code, String response) {
        Toast.makeText(this, "Payment cancelled or failed. You can place order with Cash on Delivery.", Toast.LENGTH_LONG).show();
    }

    private void completeOrder(String method, String txnId) {
        String address = binding.etDeliveryAddress.getText() != null ? binding.etDeliveryAddress.getText().toString().trim() : "";
        if (address.isEmpty()) {
            binding.etDeliveryAddress.setError("Please enter your farm or village delivery address");
            binding.etDeliveryAddress.requestFocus();
            Toast.makeText(this, "Please enter your delivery address first.", Toast.LENGTH_SHORT).show();
            return;
        }

        String orderId = "ORD-" + (System.currentTimeMillis() % 100000);
        String today = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date());

        AgriOrder order = new AgriOrder(orderId, new ArrayList<>(currentItems), currentTotal, method, txnId, address, today, "PLACED");
        viewModel.placeOrder(order);

        Intent intent = new Intent(this, OrderSuccessActivity.class);
        intent.putExtra("EXTRA_ORDER_ID", orderId);
        intent.putExtra("EXTRA_AMOUNT", currentTotal);
        intent.putExtra("EXTRA_METHOD", method);
        startActivity(intent);
        finish();
    }
}
