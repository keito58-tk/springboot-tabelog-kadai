package com.example.tabelog.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.tabelog.entity.Review;
import com.example.tabelog.entity.Store;
import com.example.tabelog.entity.User;
import com.example.tabelog.repository.ReviewRepository;

@Service
public class ReviewService {
	private final ReviewRepository reviewRepository;
    private final UserService userService;  
    private final StoreService storeService;
    
    public ReviewService(ReviewRepository reviewRepository, UserService userService, StoreService storeService) {
        this.reviewRepository = reviewRepository;
        this.userService = userService;
        this.storeService = storeService;
    }
    
    public List<Review> getReviewsForStore(Long storeId) {
        return reviewRepository.findByStoreId(storeId);
    }
    
    public boolean userHasReviewed(Long userId, Long storeId) {
        return reviewRepository.existsByUserIdAndStoreId(userId, storeId);
    }
    
    public Review createReview(Long storeId, Long userId, String comment, int rating) {
        Store store = storeService.findById(storeId);
        User user = userService.findById(userId);

        Review review = new Review();
        review.setStore(store);
        review.setUser(user);
        review.setComment(comment);
        review.setRating(rating);

        return reviewRepository.save(review);
    }

    public Review updateReview(Long reviewId, String comment, int rating) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        review.setComment(comment);
        review.setRating(rating);
        return reviewRepository.save(review);
    }
    
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

	public Review findById(Long reviewId) {
		reviewRepository.findById(reviewId);
		return reviewRepository.findById(reviewId).orElseThrow( () -> new IllegalArgumentException("Review not found"));
		
	}
}
