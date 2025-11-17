package com.example.ecommerce.controller;

import com.example.ecommerce.entity.OrderStatus;
import com.example.ecommerce.service.OrderService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stripe/webhook")
public class StripeWebhookController {

    private final OrderService orderService;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    public StripeWebhookController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().body("Webhook signature verification failed");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session != null && session.getClientReferenceId() != null) {
                Long orderId = Long.valueOf(session.getClientReferenceId());
                orderService.updateStatus(orderId, OrderStatus.PAID);
            }
        }

        return ResponseEntity.ok("");
    }
}
