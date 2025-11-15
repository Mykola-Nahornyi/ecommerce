package com.example.ecommerce.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotEnoughStockException.class)
    public String handleNotEnoughStock(NotEnoughStockException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/custom-error";  // Thymeleaf
    }

    @ExceptionHandler(EmptyCartException.class)
    public String handleEmptyCart(EmptyCartException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/custom-error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneral(Exception ex, Model model) {
        model.addAttribute("errorMessage", "Fehler aufgetaucht: " + ex.getMessage());
        return "error/custom-error";
    }
}
