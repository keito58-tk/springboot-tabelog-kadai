package com.example.tabelog.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
     public Page<Store> findAllByOrderByPriceMinAsc(Pageable pageable);
     
     public List<Store> findTop10ByOrderByCreatedAtDesc();
     
  // すべての店舗を平均評価が高い順に並べ替え、ページングされた状態で取得する
     @Query("SELECT s FROM Store s " +
            "LEFT JOIN s.reviews rev " +
            "GROUP BY s.id " +
            "ORDER BY AVG(rev.rating) DESC")
     public Page<Store> findAllByOrderByAverageRatingDesc(Pageable pageable);    

    // 指定されたキーワードを店舗名または住所またはカテゴリ名に含む店舗を作成日時が新しい順に並べ替え、ページングされた状態で取得する
    @Query("SELECT DISTINCT s FROM Store s " +
           "LEFT JOIN s.categoriesStores cs " +
           "WHERE s.name LIKE %:name% " +
           "OR s.address LIKE %:address% " +
           "OR cs.category.name LIKE %:categoryName% " +
           "ORDER BY s.createdAt DESC")
    public Page<Store> findByNameLikeOrAddressLikeOrCategoryNameLikeOrderByCreatedAtDesc(@Param("name") String nameKeyword,
                                                                                         @Param("address") String addressKeyword,
                                                                                         @Param("categoryName") String categoryNameKeyword,
                                                                                         Pageable pageable);
    // 指定されたキーワードを店舗名または住所またはカテゴリ名に含む店舗を最低価格が安い順に並べ替え、ページングされた状態で取得する
    @Query("SELECT DISTINCT s FROM Store s " +
           "LEFT JOIN s.categoriesStores cs " +
           "WHERE s.name LIKE %:name% " +
           "OR s.address LIKE %:address% " +
           "OR cs.category.name LIKE %:categoryName% " +
           "ORDER BY s.priceMin ASC")
    public Page<Store> findByNameLikeOrAddressLikeOrCategoryNameLikeOrderByPriceMinAsc(@Param("name") String nameKeyword,
                                                                                       @Param("address") String addressKeyword,
                                                                                       @Param("categoryName") String categoryNameKeyword,
                                                                                       Pageable pageable);
     // 指定されたキーワードを店舗名または住所またはカテゴリ名に含む店舗を平均評価が高い順に並べ替え、ページングされた状態で取得する
     @Query("SELECT s FROM Store s " +
            "LEFT JOIN s.categoriesStores cs " +
            "LEFT JOIN s.reviews rev " +
            "WHERE s.name LIKE %:name% " +
            "OR s.address LIKE %:address% " +
            "OR cs.category.name LIKE %:categoryName% " +
            "GROUP BY s.id " +
            "ORDER BY AVG(rev.rating) DESC")
     public Page<Store> findByNameLikeOrAddressLikeOrCategoryNameLikeOrderByAverageRatingDesc(@Param("name") String nameKeyword,
																				    		  @Param("address") String addressKeyword,
																				              @Param("categoryName") String categoryNameKeyword,
																				              Pageable pageable);
    // 指定されたidのカテゴリが設定された店舗を作成日時が新しい順に並べ替え、ページングされた状態で取得する
    @Query("SELECT s FROM Store s " +
           "INNER JOIN s.categoriesStores cs " +
           "WHERE cs.category.id = :categoryId " +
           "ORDER BY s.createdAt DESC")
    public Page<Store> findByCategoryIdOrderByCreatedAtDesc(@Param("categoryId") Integer categoryId, Pageable pageable);

    // 指定されたidのカテゴリが設定された店舗を最低価格が安い順に並べ替え、ページングされた状態で取得する
    @Query("SELECT s FROM Store s " +
           "INNER JOIN s.categoriesStores cs " +
           "WHERE cs.category.id = :categoryId " +
           "ORDER BY s.priceMin ASC")
    public Page<Store> findByCategoryIdOrderByPriceMinAsc(@Param("categoryId") Integer categoryId, Pageable pageable);
    
     // 指定されたidのカテゴリが設定された店舗を平均評価が高い順に並べ替え、ページングされた状態で取得する
     @Query("SELECT s FROM Store s " +
            "INNER JOIN s.categoriesStores cs " +
            "LEFT JOIN s.reviews rev " +
            "WHERE cs.category.id = :categoryId " +
            "GROUP BY s.id " +
            "ORDER BY AVG(rev.rating) DESC")
     public Page<Store> findByCategoryIdOrderByAverageRatingDesc(@Param("categoryId") Integer categoryId, Pageable pageable);    

    public Page<Store> findByPriceMinLessThanEqualOrderByCreatedAtDesc(Integer price, Pageable pageable);
    public Page<Store> findByPriceMinLessThanEqualOrderByPriceMinAsc(Integer price, Pageable pageable);
    
     // 指定された最低価格以下の店舗を平均評価が高い順に並べ替え、ページングされた状態で取得する
     @Query("SELECT s FROM Store s " +
            "LEFT JOIN s.reviews rev " +
            "WHERE s.priceMin <= :price " +
            "GROUP BY s.id " +
            "ORDER BY AVG(rev.rating) DESC")
     public Page<Store> findByPriceMinLessThanEqualOrderByAverageRatingDesc(@Param("price") Integer price, Pageable pageable);    
}
