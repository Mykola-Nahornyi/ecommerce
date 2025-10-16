package com.example.ecommerce.service;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public Product getProduct(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Produkt mit ID " + id + " nicht gefunden"));
    }

    public Product createProduct(Product product) {
        return repository.save(product);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product existing = getProduct(id);

        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setPrice(updatedProduct.getPrice());
        existing.setImageUrl(updatedProduct.getImageUrl());

        return repository.save(existing);
    }

    public void deleteProduct(Long id) {
        repository.deleteById(id);
    }

    public void saveProductWithImage(Product product, MultipartFile imageFile) {
        if (!imageFile.isEmpty()) {
            String imagePath = saveImage(imageFile);
            product.setImageUrl(imagePath);
        }
        repository.save(product);
    }

    public void updateProductWithImage(Long id, Product updated, MultipartFile imageFile) {
        Product existing = getProduct(id);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());

        if (!imageFile.isEmpty()) {
            String imagePath = saveImage(imageFile);
            existing.setImageUrl(imagePath);
        }

        repository.save(existing);
    }

    private String saveImage(MultipartFile file) {
        System.out.println(">>> saveImage: fileName = " + file.getOriginalFilename());

        if (file.isEmpty()) {
            throw new RuntimeException(">>> Datei ist leer!");
        }

        String uploadDir = "C:/projects/ecommerce/uploads/images/";
        File uploadPath = new File(uploadDir);

        if (!uploadPath.exists()) {
            boolean created = uploadPath.mkdirs();
            System.out.println(">>> Ordner erstellt: " + created + " -> " + uploadPath.getAbsolutePath());
        }

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File destination = new File(uploadPath, fileName);

        try {
            file.transferTo(destination);
            System.out.println(">>> Datei gespeichert nach: " + destination.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Speichern des Bildes: " + e.getMessage(), e);
        }

        return "/uploads/images/" + fileName;
    }

}
