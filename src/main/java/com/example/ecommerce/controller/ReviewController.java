package com.example.ecommerce.controller;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.Review;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.service.ProductService;
import com.example.ecommerce.service.ReviewService;
import com.example.ecommerce.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Controller
@RequestMapping("/products")
public class ReviewController {

    private final ReviewService reviewService;
    private final ProductService productService;
    private final UserService userService;

    public ReviewController(ReviewService reviewService, ProductService productService, UserService userService) {
        this.reviewService = reviewService;
        this.productService = productService;
        this.userService = userService;
    }

    @PostMapping("/{id}/review")
    public String leaveReview(@PathVariable Long id,
                              @RequestParam int rating,
                              @RequestParam String comment,
                              Principal principal) {
        User user = userService.getCurrentUser(principal);
        Product product = productService.getProduct(id);
        reviewService.leaveReview(user, product, rating, comment);
        return "redirect:/products/" + id;
    }
}
