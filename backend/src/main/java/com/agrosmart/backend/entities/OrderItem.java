package com.agrosmart.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double unitPrice;

    @Column(nullable = false)
    private double subtotal;

    public OrderItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    @com.fasterxml.jackson.annotation.JsonProperty("product")
    public void setProduct(java.util.Map<String, Object> productMap) {
        if (productMap != null) {
            if (productMap.containsKey("id") && (this.productId == null || this.productId.isEmpty())) {
                this.productId = String.valueOf(productMap.get("id"));
            }
            if (productMap.containsKey("name") && (this.productName == null || this.productName.isEmpty())) {
                this.productName = String.valueOf(productMap.get("name"));
            }
            if (productMap.containsKey("price")) {
                try {
                    this.unitPrice = Double.parseDouble(String.valueOf(productMap.get("price")));
                    if (this.subtotal == 0.0) {
                        this.subtotal = this.unitPrice * (this.quantity > 0 ? this.quantity : 1);
                    }
                } catch (Exception ignored) {}
            }
        }
    }
}
