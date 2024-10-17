package com.example.tabelog.controller;


import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.tabelog.entity.Review;
import com.example.tabelog.entity.Store;
import com.example.tabelog.entity.User;
import com.example.tabelog.form.ReviewForm;
import com.example.tabelog.service.ReviewService;
import com.example.tabelog.service.StoreService;
import com.example.tabelog.service.UserService;

import jakarta.validation.Valid;


@Controller
@RequestMapping("/stores/{storeId}/reviews")
public class ReviewController {
	private final UserService userService;
	private final StoreService storeService;
	private final ReviewService reviewService;
	
	public ReviewController(ReviewService reviewService, UserService userService, StoreService storeService) {
    this.userService = userService;
    this.storeService = storeService;
    this.reviewService = reviewService;
}

// 店舗のレビューを表示するメソッド
@GetMapping
public String getReviewsForStore(
								@PathVariable Long storeId, 
								 Model model) {
	List<Review> reviews = reviewService.findReviewsByStoreId(storeId);
    model.addAttribute("reviews", reviews);
    model.addAttribute("storeId", storeId);

    return "reviews/review-list";
}

// レビュー登録フォームを表示するメソッド
@GetMapping("/register")
public String showReviewForm(
							@RequestParam Long storeId,
							Model model) {
	model.addAttribute("reviewForm", new ReviewForm());
	model.addAttribute("storeId", storeId);
	
	return "reviews";
}
// レビューを作成するメソッド
@PostMapping("/register")
public String createReview(
						   @RequestParam Long storeId, 
						   @ModelAttribute("reviewForm")
						   @Valid ReviewForm reviewForm,
						   BindingResult bindingResult, Model model) {
	if (bindingResult.hasErrors()) {
		model.addAttribute("storeId", storeId);
		
		return "reviews/register";
	}
	
	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = userService.findByEmail(authentication.getName());
    Store store = storeService.findById(storeId);

    Review review = new Review();
    review.setUser(user);
    review.setStore(store);
    review.setComment(reviewForm.getComment());
    review.setRating(reviewForm.getRating());

    reviewService.saveReview(review); 
	
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
	model.addAttribute("storeId", storeId);
	
    return "reviews/edit-review";
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
