package com.example.tabelog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabelog.entity.Review;
import com.example.tabelog.entity.Store;
import com.example.tabelog.entity.User;


public interface ReviewRepository extends JpaRepository<Review, Integer>{
	
	public Review findByStoreAndUser(Store store, User user);
    public Page<Review> findByStoreOrderByCreatedAtDesc(Store store, Pageable pageable);
    public Review findFirstByOrderByIdDesc();
}
