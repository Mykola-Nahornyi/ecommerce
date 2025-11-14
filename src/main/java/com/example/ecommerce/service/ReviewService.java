package com.example.ecommerce.service;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.Review;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepo;
    private final OrderRepository orderRepo;

    public ReviewService(ReviewRepository reviewRepo, OrderRepository orderRepo) {
        this.reviewRepo = reviewRepo;
        this.orderRepo = orderRepo;
    }

    public boolean canUserReviewProduct(User user, Product product) {
        return orderRepo.existsByUserAndItemsProductId(user, product.getId());
    }

    public Review leaveReview(User user, Product product, int rating, String comment) {
        if (!canUserReviewProduct(user, product))
            throw new RuntimeException("Cannot review before purchase");

        Optional<Review> maybe = reviewRepo.findByUserAndProduct(user, product);
        if (maybe.isPresent()) {
            Review r = maybe.get();
            r.setRating(rating);
            r.setComment(comment);
            return reviewRepo.save(r);
        }

        Review r = new Review();
        r.setUser(user);
        r.setProduct(product);
        r.setRating(rating);
        r.setComment(comment);
        return reviewRepo.save(r);
    }
}
