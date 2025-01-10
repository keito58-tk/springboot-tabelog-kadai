package com.example.tabelog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabelog.entity.Favorite;
import com.example.tabelog.entity.Store;
import com.example.tabelog.entity.User;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer>{
	// 指定された店舗とユーザーに関連付けられたFavoriteエンティティを取得
	public Favorite findByStoreAndUser(Store store, User user);
	
	// 指定されたユーザーの全てのFavoriteエンティティを、作成日時の降順でページングして取得
    public Page<Favorite> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
