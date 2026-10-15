package com.agrosmart;

import com.agrosmart.models.AgriOrder;
import com.agrosmart.models.CartItem;
import com.agrosmart.models.MarketProduct;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for AgroSmart Marketplace and Checkout models.
 */
public class MarketplaceModelTest {

    @Test
    public void testMarketProductDetails() {
        MarketProduct product = new MarketProduct(
                "PR01", "Certified Sharbati Wheat Seeds", "Seeds", "Mahyco Seeds",
                950.0, 1100.0, "40 Kg Bag",
                "High tillering, certified germination.", 4.9f, 50
        );

        assertEquals("PR01", product.getId());
        assertEquals("Certified Sharbati Wheat Seeds", product.getName());
        assertEquals("Seeds", product.getCategory());
        assertEquals(950.0, product.getPrice(), 0.001);
        assertEquals(1100.0, product.getOriginalPrice(), 0.001);
        assertEquals(50, product.getStockCount());
    }

    @Test
    public void testCartItemSubtotalCalculation() {
        MarketProduct product = new MarketProduct(
                "PR05", "Water Soluble NPK 19:19:19", "Fertilizers", "IFFCO",
                200.0, 250.0, "1 Kg Pack",
                "Foliar spray booster.", 4.8f, 100
        );

        CartItem item = new CartItem(product, 3);
        assertEquals(3, item.getQuantity());
        assertEquals(600.0, item.getSubtotal(), 0.001);

        item.setQuantity(5);
        assertEquals(5, item.getQuantity());
        assertEquals(1000.0, item.getSubtotal(), 0.001);
    }

    @Test
    public void testAgriOrderCreationAndProperties() {
        MarketProduct p1 = new MarketProduct("PR01", "Wheat Seeds", "Seeds", "BrandA", 500.0, 600.0, "10kg", "desc", 4.5f, 20);
        MarketProduct p2 = new MarketProduct("PR02", "Bio Fertilizer", "Fertilizers", "BrandB", 300.0, 350.0, "5kg", "desc", 4.7f, 30);

        List<CartItem> items = new ArrayList<>();
        items.add(new CartItem(p1, 2)); // 1000
        items.add(new CartItem(p2, 1)); // 300

        double expectedTotal = items.get(0).getSubtotal() + items.get(1).getSubtotal();
        assertEquals(1300.0, expectedTotal, 0.001);

        AgriOrder order = new AgriOrder(
                "ORD_9999", items, expectedTotal,
                "Razorpay (Online)", "pay_test_12345", "Farm Plot 4, Nashik Highway",
                "2026-10-14 10:30:00", "PLACED"
        );

        assertEquals("ORD_9999", order.getOrderId());
        assertEquals(2, order.getItems().size());
        assertEquals(1300.0, order.getTotalAmount(), 0.001);
        assertEquals("Razorpay (Online)", order.getPaymentMethod());
        assertEquals("pay_test_12345", order.getPaymentId());
        assertEquals("Farm Plot 4, Nashik Highway", order.getDeliveryAddress());
        assertEquals("PLACED", order.getStatus());
    }
}
