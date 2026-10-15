package com.agrosmart.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.agrosmart.models.AgriOrder;
import com.agrosmart.models.CartItem;
import com.agrosmart.models.MarketProduct;
import com.agrosmart.repository.MarketplaceRepository;

import java.util.List;

public class MarketplaceViewModel extends ViewModel {

    private final MarketplaceRepository repository;
    private final MutableLiveData<List<MarketProduct>> products = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private String currentQuery = "";
    private String currentCategory = "All Products";

    public MarketplaceViewModel() {
        this.repository = MarketplaceRepository.getInstance();
        loadProducts();
    }

    public LiveData<List<MarketProduct>> getProducts() { return products; }
    public LiveData<List<CartItem>> getCart() { return repository.getCart(); }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void loadProducts() {
        isLoading.setValue(true);
        LiveData<List<MarketProduct>> res = repository.getProducts(currentQuery, currentCategory);
        products.setValue(res.getValue());
        isLoading.setValue(false);
    }

    public void search(String query) {
        this.currentQuery = query;
        loadProducts();
    }

    public void filterByCategory(String cat) {
        this.currentCategory = cat;
        loadProducts();
    }

    public void addToCart(MarketProduct product) {
        repository.addToCart(product);
    }

    public void updateCartQty(String productId, int qty) {
        repository.updateCartItemQuantity(productId, qty);
    }

    public void clearCart() {
        repository.clearCart();
    }

    public void placeOrder(AgriOrder order) {
        repository.placeOrder(order);
    }
}
