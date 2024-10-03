package com.example.tabelog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabelog.entity.Review;


public interface ReviewRepository extends JpaRepository<Review, Long>{
	
	List<Review> findByStoreId(Long storeId);
	
	boolean existsByUserIdAndStoreId(Long userId, Long storeId);

}
