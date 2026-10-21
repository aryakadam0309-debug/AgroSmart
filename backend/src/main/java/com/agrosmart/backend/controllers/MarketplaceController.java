package com.agrosmart.backend.controllers;

import com.agrosmart.backend.entities.Order;
import com.agrosmart.backend.entities.Product;
import com.agrosmart.backend.repositories.OrderRepository;
import com.agrosmart.backend.repositories.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marketplace")
@CrossOrigin(origins = "*")
public class MarketplaceController {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public MarketplaceController(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
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
        if (order.getItems() != null) {
            order.getItems().forEach(item -> item.setOrder(order));
        }
        return ResponseEntity.ok(orderRepository.save(order));
    }

    @GetMapping("/orders")
    public List<Order> getOrders(@RequestParam(required = false) String phone) {
        if (phone != null && !phone.trim().isEmpty()) {
            return orderRepository.findByFarmerPhoneOrderByOrderDateDesc(phone.trim());
        }
        return orderRepository.findAll();
    }
}
