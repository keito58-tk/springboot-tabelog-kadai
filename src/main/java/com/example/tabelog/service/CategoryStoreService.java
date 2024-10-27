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

    @Transactional
    public void createCategoriesStores(List<Integer> categoryIds, Store store) {
        for (Integer categoryId : categoryIds) {
            if (categoryId != null) {
                Optional<Category> optionalCategory = categoryService.findCategoryById(categoryId);

                if (optionalCategory.isPresent()) {
                    Category category = optionalCategory.get();

                    Optional<CategoryStore> optionalCurrentCategoryStore = categoryStoreRepository.findByCategoryAndStore(category, store);

                    // 重複するエンティティが存在しない場合は新たにエンティティを作成する
                    if (optionalCurrentCategoryStore.isEmpty()) {
                        CategoryStore categoryStore = new CategoryStore();
                        categoryStore.setStore(store);
                        categoryStore.setCategory(category);

                        categoryStoreRepository.save(categoryStore);
                    }
                }
            }
        }
    }

    @Transactional
    public void syncCategoriesStores(List<Integer> newCategoryIds, Store store) {
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

            for (Integer newCategoryId : newCategoryIds) {
                if (newCategoryId != null) {
                    Optional<Category> optionalCategory = categoryService.findCategoryById(newCategoryId);

                    if (optionalCategory.isPresent()) {
                        Category category = optionalCategory.get();

                        Optional<CategoryStore> optionalCurrentCategoryStore = categoryStoreRepository.findByCategoryAndStore(category, store);

                        // 重複するエンティティが存在しない場合は新たにエンティティを作成する
                        if (optionalCurrentCategoryStore.isEmpty()) {
                            CategoryStore categoryStore = new CategoryStore();
                            categoryStore.setStore(store);
                            categoryStore.setCategory(category);

                            categoryStoreRepository.save(categoryStore);
                        }
                    }
                }
            }
        }
    }
}
