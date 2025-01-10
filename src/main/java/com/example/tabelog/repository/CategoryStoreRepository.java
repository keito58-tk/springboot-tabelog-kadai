package com.example.tabelog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.tabelog.entity.Category;
import com.example.tabelog.entity.CategoryStore;
import com.example.tabelog.entity.Store;

public interface CategoryStoreRepository extends JpaRepository<CategoryStore, Integer>{
	// 特定の店舗に関連するカテゴリのIDを取得するカスタムクエリ
	@Query("SELECT cs.category.id FROM CategoryStore cs WHERE cs.store = :store ORDER BY cs.id ASC")
    public List<Integer> findCategoryIdsByStoreOrderByIdAsc(@Param("store") Store store);

	// 指定されたカテゴリと店舗に関連付けられたCategoryStoreエンティティを取得
    public Optional<CategoryStore> findByCategoryAndStore(Category category, Store store);
    
    // 特定の店舗に関連する全てのCategoryStoreエンティティを取得
    public List<CategoryStore> findByStoreOrderByIdAsc(Store store);
}
