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
	@Query("SELECT cs.category.id FROM CategoryStore cs WHERE cs.store = :store ORDER BY cs.id ASC")
    public List<Integer> findCategoryIdsByStoreOrderByIdAsc(@Param("store") Store store);

    public Optional<CategoryStore> findByCategoryAndStore(Category category, Store store);
    public List<CategoryStore> findByStoreOrderByIdAsc(Store store);
}
