package com.example.ecommerce.controller;

import com.example.ecommerce.entity.User;
import com.example.ecommerce.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, Principal principal) {
        model.addAttribute("user", userService.getCurrentUser(principal));
        return "user/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfileForm(Model model, Principal principal) {
        model.addAttribute("user", userService.getCurrentUser(principal));
        return "user/edit";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@ModelAttribute User updated, Principal principal) {
        userService.updateCurrentUser(principal, updated);
        return "redirect:/user/profile?updated";
    }

    @PostMapping("/profile/delete")
    public String deleteAccount(Principal principal) {
        userService.deleteCurrentUser(principal);
        return "redirect:/login?deleted";
    }
}