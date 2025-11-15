package com.example.ecommerce.exception;

import java.util.List;

public class NotEnoughStockException extends RuntimeException {

    public NotEnoughStockException(String productName) {
        super("Unzureichende Menge des volgenfen Artikels auf Lager: " + productName);
    }

    public NotEnoughStockException(List<String> products) {
        super("Unzureichende Menge des volgenfen Artikels auf Lager: " + String.join(", ", products));
    }
}