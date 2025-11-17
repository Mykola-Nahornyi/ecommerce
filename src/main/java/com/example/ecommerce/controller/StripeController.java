package com.example.ecommerce.controller;

import com.example.ecommerce.entity.Order;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.service.UserService;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/stripe")
public class StripeController {

    private final OrderService orderService;
    private final UserService userService;

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Value("${stripe.public-key}")
    private String stripePublicKey;

    public StripeController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    // ------------------------------------------------------------
    // 1) Bestellung aus Korb erstellen (nur POST)
    // ------------------------------------------------------------
    @PostMapping(value = "/create-order", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createOrderForStripe(Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Not authenticated"));
            }

            var user = userService.getCurrentUser(principal);
            Order order = orderService.createOrderFromCart(user);

            return ResponseEntity.ok(Map.of("orderId", order.getId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Server error while creating order"));
        }
    }

    // ------------------------------------------------------------
    // 2) Erstellen STRIPE Checkout Session
    // ------------------------------------------------------------
    @PostMapping(value = "/create-checkout-session/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCheckoutSession(@PathVariable Long orderId) {
        try {
            Order order = orderService.getOrderById(orderId);

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("http://localhost:8080/orders/confirmation?orderId=" + orderId)
                    .setCancelUrl("http://localhost:8080/cart")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("eur")
                                                    .setUnitAmount(order.getTotal().multiply(BigDecimal.valueOf(100)).longValue())
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Order #" + order.getId())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .setClientReferenceId(orderId.toString())
                    .build();

            Session session = Session.create(params);

            return ResponseEntity.ok(Map.of("id", session.getId()));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create Stripe session"));
        }
    }

    // ------------------------------------------------------------
    // 3) Public Key für Stripe
    // ------------------------------------------------------------
    @GetMapping("/public-key")
    public Map<String, String> getPublicKey() {
        return Map.of("publicKey", stripePublicKey);
    }
}
