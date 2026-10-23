package com.agrosmart.backend.controllers;

import com.agrosmart.backend.entities.NotificationItem;
import com.agrosmart.backend.entities.Order;
import com.agrosmart.backend.entities.OrderItem;
import com.agrosmart.backend.entities.Product;
import com.agrosmart.backend.repositories.NotificationRepository;
import com.agrosmart.backend.repositories.OrderRepository;
import com.agrosmart.backend.repositories.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/marketplace")
@CrossOrigin(origins = "*")
public class MarketplaceController {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final NotificationRepository notificationRepository;

    public MarketplaceController(ProductRepository productRepository, 
                                 OrderRepository orderRepository,
                                 NotificationRepository notificationRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/products")
    public List<Product> getProducts(@RequestParam(required = false) String category,
                                     @RequestParam(required = false) String search) {
        if (category != null && !category.equalsIgnoreCase("All")) {
            return productRepository.findByCategoryIgnoreCase(category);
        }
        if (search != null && !search.trim().isEmpty()) {
            return productRepository.findByNameContainingIgnoreCase(search.trim());
        }
        return productRepository.findAll();
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        if (order.getOrderId() == null || order.getOrderId().trim().isEmpty()) {
            order.setOrderId("ORD-" + System.currentTimeMillis());
        }
        if (order.getOrderDate() == null || order.getOrderDate().trim().isEmpty()) {
            order.setOrderDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")));
        }
        if (order.getStatus() == null || order.getStatus().trim().isEmpty()) {
            order.setStatus("CONFIRMED");
        }
        if (order.getFarmerPhone() == null || order.getFarmerPhone().trim().isEmpty()) {
            order.setFarmerPhone("9876543210");
        }

        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                item.setOrder(order);
                if (item.getProductId() != null && !item.getProductId().trim().isEmpty()) {
                    productRepository.findById(item.getProductId()).ifPresent(p -> {
                        int currentStock = p.getStockCount();
                        int newStock = Math.max(0, currentStock - item.getQuantity());
                        p.setStockCount(newStock);
                        productRepository.save(p);
                    });
                }
            }
        }

        Order saved = orderRepository.save(order);

        // Record a persistent notification for the farmer
        try {
            NotificationItem notif = new NotificationItem();
            notif.setId("NOTIF-" + System.currentTimeMillis());
            notif.setFarmerPhone(order.getFarmerPhone());
            notif.setTitle("🛍️ Order Confirmed: #" + saved.getOrderId());
            notif.setMessage("Agri-inputs order (₹" + Math.round(saved.getTotalAmount()) + ") placed successfully via " + saved.getPaymentMethod() + ".");
            notif.setTimestamp("Just now");
            notif.setType("ORDER");
            notif.setRead(false);
            notificationRepository.save(notif);
        } catch (Exception ignored) {}

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/orders")
    public List<Order> getOrders(@RequestParam(required = false) String phone) {
        if (phone != null && !phone.trim().isEmpty()) {
            return orderRepository.findByFarmerPhoneOrderByOrderDateDesc(phone.trim());
        }
        return orderRepository.findAll();
    }
}
