package com.example.ecommerce.controller;

import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping
    public String listOrders(Model model, Principal principal) {
        User user = userService.getCurrentUser(principal);
        model.addAttribute("orders", orderService.getOrdersByUser(user));
        return "orders/list";  // убедись, что именно папка orders
    }

    @GetMapping("/{id}")
    public String viewOrder(@PathVariable Long id, Model model, Principal principal) {
        User user = userService.getCurrentUser(principal);
        Order order = orderService.getOrderByIdAndUser(id, user);
        model.addAttribute("order", order);
        return "orders/detail";
    }
}
