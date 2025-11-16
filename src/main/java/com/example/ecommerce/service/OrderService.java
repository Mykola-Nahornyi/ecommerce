package com.example.ecommerce.service;

import com.example.ecommerce.entity.*;
import com.example.ecommerce.exception.EmptyCartException;
import com.example.ecommerce.exception.NotEnoughStockException;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepo;
    private final CartService cartService;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepo, CartService cartService, ProductRepository productRepository) {
        this.orderRepo = orderRepo;
        this.cartService = cartService;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order createOrderFromCart(User user) {
        Cart cart = cartService.getOrCreateCart(user);

        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException();
        }

        // Liste mit unzureichenden Waren
        List<String> insufficientProducts = new ArrayList<>();
        for (CartItem ci : cart.getItems()) {
            Product product = ci.getProduct();
            if (product.getStock() < ci.getQuantity()) {
                insufficientProducts.add(product.getName() + " (" + ci.getQuantity() + " Stück, vorhanden: " + product.getStock() + ")");
            }
        }

        if (!insufficientProducts.isEmpty()) {
            throw new NotEnoughStockException(insufficientProducts);
        }

        Order order = new Order();
        order.setUser(user);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.NEW);

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem ci : cart.getItems()) {
            Product product = ci.getProduct();

            // Lager verkleinen
            product.setStock(product.getStock() - ci.getQuantity());
            productRepository.save(product);

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(product);
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(product.getPrice());

            order.getItems().add(oi);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));
        }

        order.setTotal(total);
        orderRepo.save(order);

        cartService.clearCart(user);

        return order;
    }

    public List<Order> getOrdersByUser(User user) {
        return orderRepo.findByUser(user);
    }

    public Order getOrderByIdAndUser(Long id, User user) {
        return orderRepo.findById(id)
                .filter(o -> o.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Bestellung ist nicht gefunden oder Zugriff verboten"));
    }

    @Transactional
    public Order updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        order.setStatus(newStatus);
        return orderRepo.save(order);
    }
}
