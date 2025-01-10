package com.example.tabelog.service;

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
	
	// 指定したidを持つレビューを取得する
    public Optional<Review> findReviewById(Integer id) {
        return reviewRepository.findById(id);
    }

    // 指定した店舗のすべてのレビューを作成日時が新しい順に並べ替え、ページングされた状態で取得する
    public Page<Review> findReviewsByStoreOrderByCreatedAtDesc(Store store, Pageable pageable) {
        return reviewRepository.findByStoreOrderByCreatedAtDesc(store, pageable);
    }

    // レビューのレコード数を取得する
    public long countReviews() {
        return reviewRepository.count();
    }

    // idが最も大きいレビューを取得する
    public Review findFirstReviewByOrderByIdDesc() {
        return reviewRepository.findFirstByOrderByIdDesc();
    }

    // 新しいレビューを作成し、データベースに保存
    @Transactional
    public void createReview(ReviewRegisterForm reviewRegisterForm, Store store, User user) {
        Review review = new Review();

        review.setComment(reviewRegisterForm.getComment());
        review.setRating(reviewRegisterForm.getRating());
        review.setStore(store);
        review.setUser(user);

        // 新しいレビューをデータベースに保存
        reviewRepository.save(review);
    }

    // 指定されたレビューを更新し、データベースに保存
    @Transactional
    public void updateReview(ReviewEditForm reviewEditForm, Review review) {
        review.setRating(reviewEditForm.getRating());
        review.setComment(reviewEditForm.getComment());

        reviewRepository.save(review);
    }

    // 指定されたレビューをデータベースから削除
    @Transactional
    public void deleteReview(Review review) {
        reviewRepository.delete(review);
    }

    // 指定したユーザーが指定した店舗のレビューをすでに投稿済みかどうかをチェックする
    public boolean hasUserAlreadyReviewed(Store store, User user) {
        return reviewRepository.findByStoreAndUser(store, user) != null;
    }
}