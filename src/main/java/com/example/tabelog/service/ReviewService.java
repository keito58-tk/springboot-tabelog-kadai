package com.example.tabelog.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.tabelog.entity.Review;
import com.example.tabelog.repository.ReviewRepository;

@Service
public class ReviewService {
	private final ReviewRepository reviewRepository;
	
	public ReviewService(ReviewRepository reviewRepository) {
		this.reviewRepository = reviewRepository;
	}
	
	// 店舗IDでレビュー一覧を取得する
	public List<Review> findReviewsByStoreId(Long storeId) {
		return reviewRepository.findByStoreId(storeId);
	}
	
	// 特定のユーザーが店舗にレビューを投稿したか確認する
	public boolean hasUserReviewed(Long userId, Long storeId) {
		return reviewRepository.existsByUserIdAndStoreId(userId, storeId);
	}
	
	// レビューを保存する
	public Review saveReview(Review review) {
		return reviewRepository.save(review);
	}

	// レビューを削除する
	public void deleteReview(Long reviewId) {
		
	}
	// レビューIDでレビューを取得する
	public Review findById(Long reviewId) {
		return reviewRepository.findById(reviewId)
				.orElseThrow( () -> new IllegalArgumentException("指定されたレビューが存在しません: ID = " + reviewId));
	}
}
