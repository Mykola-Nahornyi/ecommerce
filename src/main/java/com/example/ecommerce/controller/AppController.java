package com.example.ecommerce.controller;

import com.example.ecommerce.entity.User;
import com.example.ecommerce.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class AppController {

    private final UserService userService;

    public AppController(UserService userService) {
        this.userService = userService;
    }

    // normaller root, macht 302 Http-redirect
    @GetMapping("/")
    public String root() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String index(Model model, Principal principal) {
        User current = null;
        if (principal != null) {
            current = userService.getCurrentUser(principal);
        }
        model.addAttribute("currentUser", current);
        return "index";
    }

    @GetMapping("/login")
    public String login(Principal principal) {
        if (principal != null) {
            return "redirect:/index";
        }
        return "login";
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        User current = userService.getCurrentUser(principal);
        if (current == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", current);
        return "user/profile";
    }

    @GetMapping("/admin")
    public String admin(Model model, Principal principal) {
        User current = userService.getCurrentUser(principal);
        if (current == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", current);
        return "admin/admin";
    }
}
