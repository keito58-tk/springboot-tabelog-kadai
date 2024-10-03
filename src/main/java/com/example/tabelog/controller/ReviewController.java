package com.example.tabelog.controller;


import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.tabelog.entity.Review;
import com.example.tabelog.entity.Store;
import com.example.tabelog.service.ReviewService;
import com.example.tabelog.service.StoreService;
import com.example.tabelog.service.UserService;


@Controller
@RequestMapping("/stores/{storeId}/reviews")
public class ReviewController {
	private final ReviewService reviewService;
    private final UserService userService; 
    private final StoreService storeService;
    
public ReviewController(ReviewService reviewService, UserService userService, StoreService storeService) {
    this.reviewService = reviewService;
    this.userService = userService;
    this.storeService = storeService;
}

// 店舗のレビューを表示するメソッド
@GetMapping
public String getReviewsForStore(
	@PathVariable Long storeId, Model model) {
	Store store = storeService.findById(storeId);
    List<Review> reviews = reviewService.getReviewsForStore(storeId);
    Long userId = userService.getLoggedInUserId();
    boolean userHasReviews = reviewService.userHasReviewed(userId, storeId);

    model.addAttribute("store", store);
    model.addAttribute("reviews", reviews);
    model.addAttribute("storeHasReviews", !reviews.isEmpty());
    model.addAttribute("userHasReviews", userHasReviews);

    return "reviews";
}

// レビューを作成するメソッド
@PostMapping("/register")
public String createReview(
						   @PathVariable Long storeId, @RequestParam String comment,
                           @RequestParam int rating) {
    Long userId = userService.getLoggedInUserId();
    reviewService.createReview(storeId, userId, comment, rating);
    return "redirect:/stores/" + storeId + "/reviews";
}

// レビュー編集ページを表示するメソッド
@GetMapping("/{reviewId}/edit")
public String editReview(
						 @PathVariable Long storeId, 
						 @PathVariable Long reviewId, 
						 Model model) {
    Review review = reviewService.findById(reviewId);
    model.addAttribute("review", review);
    return "edit-review";
}

// レビューを削除するメソッド
@PostMapping("/{reviewId}/delete")
public String deleteReview(
		@PathVariable Long storeId, 
		@PathVariable Long reviewId) {
    reviewService.deleteReview(reviewId);
    return "redirect:/stores/" + storeId + "/reviews";
}

}
