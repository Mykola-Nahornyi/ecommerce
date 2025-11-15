package com.example.ecommerce.controller;

import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Controller
public class CartController {

    private final CartService cartService;
    private final UserService userService;
    private final OrderService orderService; // ✅ добавляем

    public CartController(CartService cartService, UserService userService, OrderService orderService) {
        this.cartService = cartService;
        this.userService = userService;
        this.orderService = orderService; // ✅ сохраняем в поле
    }

    @GetMapping("/cart")
    public String viewCart(Model model, Principal principal) {
        User user = userService.getCurrentUser(principal);
        model.addAttribute("cart", cartService.getOrCreateCart(user));
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
        try {
            User user = userService.getCurrentUser(principal);
            Order order = orderService.createOrderFromCart(user);
            model.addAttribute("order", order);
            return "orders/confirmation";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "cart";
        }
    }
}
