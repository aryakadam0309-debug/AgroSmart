package com.agrosmart.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.agrosmart.models.AgriOrder;
import com.agrosmart.models.CartItem;
import com.agrosmart.models.MarketProduct;

import java.util.ArrayList;
import java.util.List;

public class MarketplaceRepository {

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
                "PR03", "Bollgard II Cotton Hybrid Seeds", "Seeds", "Bayer CropScience",
                860.0, 950.0, "475g Packet",
                "Pink bollworm tolerant hybrid with high boll retention.", 4.7f, 80
        ));
        products.add(new MarketProduct(
                "PR04", "Bio-Organic Vermicompost Enriched", "Fertilizers", "Krishi Organic",
                320.0, 400.0, "50 Kg Bag",
                "100% pure organic humus enriched with beneficial soil microbes.", 4.9f, 120
        ));
        products.add(new MarketProduct(
                "PR05", "Water Soluble NPK 19:19:19", "Fertilizers", "IFFCO",
                195.0, 240.0, "1 Kg Pack",
                "100% water-soluble foliar spray fertilizer for vegetative booster.", 4.8f, 200
        ));
        products.add(new MarketProduct(
                "PR06", "Neem Seed Kernel Oil Extract 10,000 PPM", "Pesticides", "AgroShield",
                550.0, 680.0, "1 Liter Bottle",
                "Broad spectrum organic repellent against whitefly, aphids, and borers.", 4.8f, 65
        ));
        products.add(new MarketProduct(
                "PR07", "Battery Powered Knapsack Sprayer (16L)", "Tools", "KisanKraft",
                2850.0, 3500.0, "16 Liter Tank",
                "12V rechargeable battery, dual nozzles, comfortable padded shoulder straps.", 4.9f, 25
        ));
    }

    public LiveData<List<MarketProduct>> getProducts(String query, String category) {
        MutableLiveData<List<MarketProduct>> liveData = new MutableLiveData<>();
        List<MarketProduct> filtered = new ArrayList<>();
        for (MarketProduct p : products) {
            boolean matchesQuery = query == null || query.trim().isEmpty()
                    || p.getName().toLowerCase().contains(query.toLowerCase())
                    || p.getBrand().toLowerCase().contains(query.toLowerCase());
            boolean matchesCategory = category == null || category.equals("All Products")
                    || p.getCategory().equalsIgnoreCase(category);
            if (matchesQuery && matchesCategory) {
                filtered.add(p);
            }
        }
        liveData.setValue(filtered);
        return liveData;
    }

    public LiveData<List<CartItem>> getCart() { return cartLiveData; }

    public void addToCart(MarketProduct product) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                cartLiveData.setValue(new ArrayList<>(cartItems));
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
                    item.setQuantity(newQty);
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
        orders.add(0, order);
        clearCart();
    }
}
