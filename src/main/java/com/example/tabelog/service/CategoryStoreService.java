package com.example.tabelog.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tabelog.entity.Category;
import com.example.tabelog.entity.CategoryStore;
import com.example.tabelog.entity.Store;
import com.example.tabelog.repository.CategoryStoreRepository;

@Service
public class CategoryStoreService {
	private final CategoryStoreRepository categoryStoreRepository;
    private final CategoryService categoryService;

    public CategoryStoreService(CategoryStoreRepository categoryStoreRepository, CategoryService categoryService) {
        this.categoryStoreRepository = categoryStoreRepository;
        this.categoryService = categoryService;
    }

    // 指定した店舗のカテゴリのidをリスト形式で取得する
    public List<Integer> findCategoryIdsByStoreOrderByIdAsc(Store store) {
        return categoryStoreRepository.findCategoryIdsByStoreOrderByIdAsc(store);
    }

    // 指定されたカテゴリIDのリストと店舗に基づいて、カテゴリと店舗の関連付けを作成
    @Transactional
    public void createCategoriesStores(List<Integer> categoryIds, Store store) {
        for (Integer categoryId : categoryIds) {
            if (categoryId != null) {
            	// カテゴリIDに基づいてカテゴリを検索
                Optional<Category> optionalCategory = categoryService.findCategoryById(categoryId);

                if (optionalCategory.isPresent()) {
                    Category category = optionalCategory.get();

                    // カテゴリと店舗の関連付けが存在するか確認
                    Optional<CategoryStore> optionalCurrentCategoryStore = categoryStoreRepository.findByCategoryAndStore(category, store);

                    // 重複するエンティティが存在しない場合は新たにエンティティを作成する
                    if (optionalCurrentCategoryStore.isEmpty()) {
                        CategoryStore categoryStore = new CategoryStore();
                        categoryStore.setStore(store);
                        categoryStore.setCategory(category);

                        // 新しいCategoryStoreエンティティを保存
                        categoryStoreRepository.save(categoryStore);
                    }
                }
            }
        }
    }

    // 指定された新しいカテゴリIDのリストと店舗に基づいて、カテゴリと店舗の関連付けを同期
    @Transactional
    public void syncCategoriesStores(List<Integer> newCategoryIds, Store store) {
    	// 店舗に関連するすべてのCategoryStoreエンティティを取得
        List<CategoryStore> currentCategoriesStores = categoryStoreRepository.findByStoreOrderByIdAsc(store);

        if (newCategoryIds == null) {
            // newCategoryIdsがnullの場合はすべてのエンティティを削除する
            for (CategoryStore currentCategoryStore : currentCategoriesStores) {
                categoryStoreRepository.delete(currentCategoryStore);
            }
        } else {
            // 既存のエンティティが新しいリストに存在しない場合は削除する
            for (CategoryStore currentCategoryStore : currentCategoriesStores) {
                if (!newCategoryIds.contains(currentCategoryStore.getCategory().getId())) {
                    categoryStoreRepository.delete(currentCategoryStore);
                }
            }

            // 新しいカテゴリIDに基づいて関連付けを作成または維持する
            for (Integer newCategoryId : newCategoryIds) {
                if (newCategoryId != null) {
                	// カテゴリIDに基づいてカテゴリを検索
                    Optional<Category> optionalCategory = categoryService.findCategoryById(newCategoryId);

                    if (optionalCategory.isPresent()) {
                        Category category = optionalCategory.get();

                        // カテゴリと店舗の関連付けが存在するか確認
                        Optional<CategoryStore> optionalCurrentCategoryStore = categoryStoreRepository.findByCategoryAndStore(category, store);

                        // 重複するエンティティが存在しない場合は新たにエンティティを作成する
                        if (optionalCurrentCategoryStore.isEmpty()) {
                            CategoryStore categoryStore = new CategoryStore();
                            categoryStore.setStore(store);
                            categoryStore.setCategory(category);

                            // 新しいCategoryStoreエンティティを保存
                            categoryStoreRepository.save(categoryStore);
                        }
                    }
                }
            }
        }
    }
}
