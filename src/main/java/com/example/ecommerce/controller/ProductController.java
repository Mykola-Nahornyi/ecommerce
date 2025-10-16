package com.example.ecommerce.controller;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.service.ProductService;
import com.example.ecommerce.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@Controller
@RequestMapping("/admin/products")
public class ProductController {

    private final ProductService service;
    private final UserService userService;


    public ProductController(ProductService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @GetMapping ("/list")
    public String listProducts(Model model) {
        model.addAttribute("products", service.getAllProducts());
        return "products/list";
    }

    @GetMapping("/new")
    public String newProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "admin/new";
    }

    @PostMapping("/new")
    public String createProduct(@ModelAttribute Product product,
                                @RequestParam("imageFile") MultipartFile imageFile) {
        service.saveProductWithImage(product, imageFile);
        return "redirect:/admin/products/list";
    }

    @GetMapping("/{id}/edit")
    public String editProductForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", service.getProduct(id));
        return "admin/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateProduct(@PathVariable Long id,
                                @ModelAttribute Product product,
                                @RequestParam("imageFile") MultipartFile imageFile) {
        service.updateProductWithImage(id, product, imageFile);
        return "redirect:/admin/products/list";
    }

    @GetMapping("/{id}/delete")
    public String deleteConfirm(@PathVariable Long id, Model model) {
        model.addAttribute("product", service.getProduct(id));
        return "admin/delete";
    }

    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        service.deleteProduct(id);
        return "redirect:/admin/products/list";
    }
}
