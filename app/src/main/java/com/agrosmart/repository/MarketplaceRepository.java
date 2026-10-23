package com.agrosmart.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.agrosmart.models.AgriOrder;
import com.agrosmart.models.CartItem;
import com.agrosmart.models.MarketProduct;
import com.agrosmart.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MarketplaceRepository {

    private static final String TAG = "MarketplaceRepo";
    private static MarketplaceRepository instance;
    private final List<MarketProduct> products = new ArrayList<>();
    private final List<CartItem> cartItems = new ArrayList<>();
    private final MutableLiveData<List<CartItem>> cartLiveData = new MutableLiveData<>();
    private final List<AgriOrder> orders = new ArrayList<>();

    public static synchronized MarketplaceRepository getInstance() {
        if (instance == null) {
            instance = new MarketplaceRepository();
        }
        return instance;
    }

    private MarketplaceRepository() {
        initProducts();
        cartLiveData.setValue(new ArrayList<>(cartItems));
    }

    private void initProducts() {
        products.clear();
        products.add(new MarketProduct(
                "PR01", "Certified Sharbati Wheat Seeds (HD-2967)", "Seeds", "Mahyco Seeds",
                950.0, 1100.0, "40 Kg Bag",
                "High tillering, disease resistant, certified germination above 95%.", 4.9f, 50
        ));
        products.add(new MarketProduct(
                "PR02", "JS-335 Certified Soybean Seeds", "Seeds", "National Seeds Corp",
                1850.0, 2100.0, "30 Kg Bag",
                "Early maturing (95 days), drought tolerant, high oil content.", 4.8f, 35
        ));
        products.add(new MarketProduct(
                "PR03", "Bio-Organic Vermicompost Enriched", "Fertilizers", "Krishi Organic",
                320.0, 400.0, "50 Kg Bag",
                "100% pure organic humus enriched with beneficial soil microbes.", 4.9f, 120
        ));
        products.add(new MarketProduct(
                "PR04", "Water Soluble NPK 19:19:19", "Fertilizers", "IFFCO",
                195.0, 240.0, "1 Kg Pack",
                "100% water-soluble foliar spray fertilizer for vegetative booster.", 4.8f, 200
        ));
        products.add(new MarketProduct(
                "PR05", "Battery Powered Knapsack Sprayer (16L)", "Tools", "KisanKraft",
                2850.0, 3500.0, "16 Liter Tank",
                "12V rechargeable battery, dual nozzles, comfortable padded shoulder straps.", 4.9f, 25
        ));
    }

    public LiveData<List<MarketProduct>> getProducts(String query, String category) {
        MutableLiveData<List<MarketProduct>> liveData = new MutableLiveData<>();
        
        // Initial local filter
        List<MarketProduct> filtered = new ArrayList<>();
        for (MarketProduct p : products) {
            boolean matchesQuery = query == null || query.trim().isEmpty()
                    || p.getName().toLowerCase().contains(query.toLowerCase())
                    || p.getBrand().toLowerCase().contains(query.toLowerCase());
            boolean matchesCategory = category == null || category.equals("All Products") || category.equals("All")
                    || p.getCategory().equalsIgnoreCase(category);
            if (matchesQuery && matchesCategory) {
                filtered.add(p);
            }
        }
        liveData.setValue(filtered);

        // Fetch live from MySQL backend
        String catParam = (category == null || category.equals("All Products") || category.equals("All")) ? null : category;
        String qParam = (query == null || query.trim().isEmpty()) ? null : query.trim();

        ApiClient.getInstance().getApi().getProducts(catParam, qParam)
                .enqueue(new Callback<List<MarketProduct>>() {
                    @Override
                    public void onResponse(Call<List<MarketProduct>> call, Response<List<MarketProduct>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            synchronized (MarketplaceRepository.this) {
                                products.clear();
                                products.addAll(response.body());
                            }
                            liveData.postValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<MarketProduct>> call, Throwable t) {
                        Log.w(TAG, "Backend unreachable, using cached products: " + t.getMessage());
                    }
                });

        return liveData;
    }

    public LiveData<List<CartItem>> getCart() { return cartLiveData; }

    public void addToCart(MarketProduct product) {
        if (product == null || product.getStock() <= 0) return;
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(product.getId())) {
                if (item.getQuantity() < product.getStock()) {
                    item.setQuantity(item.getQuantity() + 1);
                    cartLiveData.setValue(new ArrayList<>(cartItems));
                }
                return;
            }
        }
        cartItems.add(new CartItem(product, 1));
        cartLiveData.setValue(new ArrayList<>(cartItems));
    }

    public void updateCartItemQuantity(String productId, int newQty) {
        if (newQty <= 0) {
            cartItems.removeIf(item -> item.getProduct().getId().equals(productId));
        } else {
            for (CartItem item : cartItems) {
                if (item.getProduct().getId().equals(productId)) {
                    int maxStock = item.getProduct().getStock();
                    item.setQuantity(Math.min(newQty, maxStock > 0 ? maxStock : 999));
                    break;
                }
            }
        }
        cartLiveData.setValue(new ArrayList<>(cartItems));
    }

    public void clearCart() {
        cartItems.clear();
        cartLiveData.setValue(new ArrayList<>(cartItems));
    }

    public void placeOrder(AgriOrder order) {
        if (order == null) return;
        orders.add(0, order);

        // Deduct local stock for ordered products
        if (order.getItems() != null) {
            for (CartItem ci : order.getItems()) {
                if (ci.getProduct() != null) {
                    for (MarketProduct mp : products) {
                        if (mp.getId().equals(ci.getProduct().getId())) {
                            mp.setStock(Math.max(0, mp.getStock() - ci.getQuantity()));
                            break;
                        }
                    }
                }
            }
        }

        // Post order to MySQL backend
        ApiClient.getInstance().getApi().createOrder(order)
                .enqueue(new Callback<AgriOrder>() {
                    @Override
                    public void onResponse(Call<AgriOrder> call, Response<AgriOrder> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.i(TAG, "Order persisted into MySQL orders table: " + response.body().getOrderId());
                        } else {
                            Log.w(TAG, "Backend returned status " + response.code() + " for order placement");
                        }
                    }

                    @Override
                    public void onFailure(Call<AgriOrder> call, Throwable t) {
                        Log.w(TAG, "Failed to send order to backend: " + t.getMessage());
                    }
                });

        clearCart();
    }

    public LiveData<List<AgriOrder>> getOrders(String phone) {
        MutableLiveData<List<AgriOrder>> liveData = new MutableLiveData<>(new ArrayList<>(orders));
        ApiClient.getInstance().getApi().getOrders(phone)
                .enqueue(new Callback<List<AgriOrder>>() {
                    @Override
                    public void onResponse(Call<List<AgriOrder>> call, Response<List<AgriOrder>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            orders.clear();
                            orders.addAll(response.body());
                            liveData.postValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AgriOrder>> call, Throwable t) {
                        Log.w(TAG, "Could not fetch orders from backend: " + t.getMessage());
                    }
                });
        return liveData;
    }
}
