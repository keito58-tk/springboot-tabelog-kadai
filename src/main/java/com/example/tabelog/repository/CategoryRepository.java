package com.example.tabelog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabelog.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer>{
	// カテゴリ名が指定されたキーワードを含むカテゴリをページングして取得
	public Page<Category> findByNameLike(String keyword, Pageable pageable);
	
	// IDが最も大きい（最新の）カテゴリを1件取得
    public Category findFirstByOrderByIdDesc();
}
