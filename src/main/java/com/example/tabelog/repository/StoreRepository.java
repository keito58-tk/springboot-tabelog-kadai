package com.example.tabelog.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabelog.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Integer>{
	public Page<Store> findByNameLike(String keyword, Pageable pageable);
	
	 public Page<Store> findByNameLikeOrAddressLikeOrderByCreatedAtDesc(String nameKeyword, String addressKeyword, Pageable pageable);  
     public Page<Store> findByNameLikeOrAddressLikeOrderByPriceMaxAsc(String nameKeyword, String addressKeyword, Pageable pageable);  
     public Page<Store> findByAddressLikeOrderByCreatedAtDesc(String area, Pageable pageable);
     public Page<Store> findByAddressLikeOrderByPriceMaxAsc(String area, Pageable pageable);
     public Page<Store> findByPriceMaxLessThanEqualOrderByCreatedAtDesc(Integer priceMax, Pageable pageable);
     public Page<Store> findByPriceMaxLessThanEqualOrderByPriceMaxAsc(Integer priceMax, Pageable pageable); 
     public Page<Store> findAllByOrderByCreatedAtDesc(Pageable pageable);
     public Page<Store> findAllByOrderByPriceMaxAsc(Pageable pageable);
     
     public List<Store> findTop10ByOrderByCreatedAtDesc();
}
