package com.example.tabelog.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tabelog.entity.Review;
import com.example.tabelog.entity.Store;
import com.example.tabelog.entity.User;
import com.example.tabelog.form.ReviewEditForm;
import com.example.tabelog.form.ReviewRegisterForm;
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
	public boolean hasUserAlreadyReviewed(Store store, User user) {
        return reviewRepository.findByStoreAndUser(store, user) != null;
    }
	
	// レビューを保存する
	public Review saveReview(Review review) {
		return reviewRepository.save(review);
	}
	
	@Transactional
    public void createReview(ReviewRegisterForm reviewRegisterForm, Store store, User user) {
        Review review = new Review();

        review.setComment(reviewRegisterForm.getComment());
        review.setRating(reviewRegisterForm.getRating());
        review.setStore(store);
        review.setUser(user);

        reviewRepository.save(review);
    }

	@Transactional
    public void updateReview(ReviewEditForm reviewEditForm, Review review) {
        review.setRating(reviewEditForm.getRating());
        review.setComment(reviewEditForm.getComment());

        reviewRepository.save(review);
    }
	
	// レビューを削除する
	 @Transactional
     public void deleteReview(Review review) {
         reviewRepository.delete(review);
     }
	// レビューIDでレビューを取得する
	public Optional<Review> findReviewById(Integer id) {
        return reviewRepository.findById(id);
    }

	public Page<Review> findReviewsByStoreOrderByCreatedAtDesc(Store store, Pageable pageable) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
	
	
}
