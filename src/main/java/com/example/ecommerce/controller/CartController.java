package com.example.ecommerce.controller;

import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class CartController {

    private final CartService cartService;
    private final UserService userService;
    private final OrderService orderService;

    @Value("${stripe.public-key}")
    private String stripePublicKey;

    public CartController(CartService cartService, UserService userService, OrderService orderService) {
        this.cartService = cartService;
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/cart")
    public String viewCart(Model model, Principal principal) {
        User user = userService.getCurrentUser(principal);
        var cart = cartService.getOrCreateCart(user);
        model.addAttribute("cart", cart);

        model.addAttribute("stripePublicKey", stripePublicKey); // <---- добавляем ключ
        return "cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int qty,
                            Principal principal) {
        User user = userService.getCurrentUser(principal);
        cartService.addToCart(user, productId, qty);
        return "redirect:/cart";
    }

    @PostMapping("/cart/item/{id}/qty")
    public String updateQty(@PathVariable Long id, @RequestParam int qty, Principal principal) {
        User user = userService.getCurrentUser(principal);
        cartService.updateQuantity(user, id, qty);
        return "redirect:/cart";
    }

    @PostMapping("/cart/item/{id}/remove")
    public String removeItem(@PathVariable Long id, Principal principal) {
        User user = userService.getCurrentUser(principal);
        cartService.removeItem(user, id);
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(Principal principal, Model model) {
        User user = userService.getCurrentUser(principal);

        try {
            Order order = orderService.createOrderFromCart(user); // создает и сохраняет Order
            model.addAttribute("order", order);
            return "orders/confirmation";
        } catch (Exception e) {
            model.addAttribute("cart", cartService.getOrCreateCart(user));
            model.addAttribute("error", e.getMessage());
            return "cart";
        }
    }
}
