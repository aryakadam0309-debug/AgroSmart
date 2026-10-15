package com.agrosmart.models;

import java.io.Serializable;
import java.util.List;

public class AgriOrder implements Serializable {
    private String orderId;
    private List<CartItem> items;
    private double totalAmount;
    private String paymentMethod; // "Razorpay (Online)", "Cash on Delivery"
    private String paymentId;
    private String deliveryAddress;
    private String orderDate;
    private String status; // "PLACED", "DISPATCHED", "DELIVERED"

    public AgriOrder() {}

    public AgriOrder(String orderId, List<CartItem> items, double totalAmount,
                     String paymentMethod, String paymentId, String deliveryAddress,
                     String orderDate, String status) {
        this.orderId = orderId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.paymentId = paymentId;
        this.deliveryAddress = deliveryAddress;
        this.orderDate = orderDate;
        this.status = status;
    }

    public String getOrderId() { return orderId; }
    public List<CartItem> getItems() { return items; }
    public double getTotalAmount() { return totalAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentId() { return paymentId; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getOrderDate() { return orderDate; }
    public String getStatus() { return status; }
}
