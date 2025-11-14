package com.example.ecommerce.service;

import com.example.ecommerce.entity.*;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepo;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepo, CartService cartService) {
        this.orderRepo = orderRepo;
        this.cartService = cartService;
    }

    public Order createOrderFromCart(User user) {
        Cart cart = cartService.getOrCreateCart(user);
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Warenkorb ist leer!");
        }

        Order order = new Order();
        order.setUser(user);

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : cart.getItems()) {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(ci.getProduct());
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(ci.getProduct().getPrice());
            order.getItems().add(oi);
            total = total.add(oi.getPrice().multiply(BigDecimal.valueOf(oi.getQuantity())));
        }

        order.setTotal(total);
        order.setStatus("NEW");
        order.setCreatedAt(LocalDateTime.now());
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
                .orElseThrow(() -> new RuntimeException("Bestellung nicht gefunden oder Zugriff verweigert"));
    }
}
