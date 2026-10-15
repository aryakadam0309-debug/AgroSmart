package com.agrosmart.models;

import java.io.Serializable;

public class CartItem implements Serializable {
    private MarketProduct product;
    private int quantity;

    public CartItem() {}

    public CartItem(MarketProduct product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public MarketProduct getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getSubtotal() { return product.getPrice() * quantity; }
}
