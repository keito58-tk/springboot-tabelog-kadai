package com.example.tabelog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabelog.entity.Favorite;
import com.example.tabelog.entity.Store;
import com.example.tabelog.entity.User;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer>{
	public Favorite findByStoreAndUser(Store store, User user);
    public Page<Favorite> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
