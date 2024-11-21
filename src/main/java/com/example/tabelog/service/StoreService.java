package com.example.tabelog.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.tabelog.entity.Store;
import com.example.tabelog.form.StoreEditForm;
import com.example.tabelog.form.StoreRegisterForm;
import com.example.tabelog.repository.StoreRepository;

@Service
public class StoreService {
	private final StoreRepository storeRepository;
	private final CategoryStoreService categoryStoreService;
	
	public StoreService(StoreRepository storeRepository, CategoryStoreService categoryStoreService) {
		this.storeRepository = storeRepository;
		this.categoryStoreService = categoryStoreService;
	}
	
	@Transactional
	public void createStore(StoreRegisterForm storeRegisterForm) {
		Store store = new Store();
		MultipartFile imageFile = storeRegisterForm.getImageFile();
		List<Integer> categoryIds = storeRegisterForm.getCategoryIds();
		
		if (!imageFile.isEmpty()) {
			String imageName = imageFile.getOriginalFilename();
			String hashedImageName = generateNewFileName(imageName);
			Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
			copyImageFile(imageFile,filePath);
			store.setImageName(hashedImageName);
		}
		
		store.setName(storeRegisterForm.getName());
		store.setDescription(storeRegisterForm.getDescription());
		
		DateTimeFormatter fmt = new DateTimeFormatterBuilder()
		        .append(DateTimeFormatter.ISO_LOCAL_TIME)
		        .parseDefaulting(ChronoField.EPOCH_DAY, 0)
		        .toFormatter();
		LocalDateTime openingTime = LocalDateTime.parse(storeRegisterForm.getOpeningTime(), fmt);
		LocalDateTime closingTime = LocalDateTime.parse(storeRegisterForm.getClosingTime(), fmt);
		
		store.setOpeningTime(openingTime);
		store.setClosingTime(closingTime);
		store.setPostalCode(storeRegisterForm.getPostalCode());
		store.setAddress(storeRegisterForm.getAddress());
		store.setPhoneNumber(storeRegisterForm.getPhoneNumber());
		store.setPriceMax(storeRegisterForm.getPriceMax());
		store.setPriceMin(storeRegisterForm.getPriceMin());
		
		storeRepository.save(store);

		if (categoryIds != null) {
            categoryStoreService.createCategoriesStores(categoryIds, store);
        }
	}
	
	@Transactional
	public void update(StoreEditForm storeEditForm) {
		Store store = storeRepository.getReferenceById(storeEditForm.getId());
		MultipartFile imageFile = storeEditForm.getImageFile();
		List<Integer> categoryIds = storeEditForm.getCategoryIds();
		
		if (!imageFile.isEmpty()) {
			String imageName = imageFile.getOriginalFilename();
			String hashedImageName = generateNewFileName(imageName);
			Path filePath = Paths.get("src/main/resourses/static/storage/" + hashedImageName);
			copyImageFile(imageFile, filePath);
			store.setImageName(hashedImageName);
		}
		
		store.setName(storeEditForm.getName());
		store.setDescription(storeEditForm.getDescription());
		
		DateTimeFormatter fmt = new DateTimeFormatterBuilder()
		        .append(DateTimeFormatter.ISO_LOCAL_TIME)
		        .parseDefaulting(ChronoField.EPOCH_DAY, 0)
		        .toFormatter();
		LocalDateTime openingTime = LocalDateTime.parse(storeEditForm.getOpeningTime(), fmt);
		LocalDateTime closingTime = LocalDateTime.parse(storeEditForm.getClosingTime(), fmt);
		
		store.setOpeningTime(openingTime);
		store.setClosingTime(closingTime);
		store.setPostalCode(storeEditForm.getPostalCode());
		store.setAddress(storeEditForm.getAddress());
		store.setPhoneNumber(storeEditForm.getPhoneNumber());
		store.setPriceMax(storeEditForm.getPriceMax());
		store.setPriceMin(storeEditForm.getPriceMin());
		
		storeRepository.save(store);
		
		categoryStoreService.syncCategoriesStores(categoryIds, store);
	}

	private String generateNewFileName(String fileName) {
		String[] fileNames = fileName.split("\\.");
		for (int i = 0; i < fileNames.length - 1; i++) {
			fileNames[i] = UUID.randomUUID().toString();
			
		}
		String hashedFileName = String.join(".", fileNames);
		return hashedFileName;
	}
	
	private void copyImageFile(MultipartFile imageFile, Path filePath) {
		try {
			Files.copy(imageFile.getInputStream(), filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Transactional(readOnly = true)
    public Optional<Store> findById(Integer storeId) {
        return storeRepository.findById(storeId);
    }

	public Optional<Store> findStoreById(Integer storeId) {
	    return storeRepository.findById(storeId);
	}

	// 価格が正しく設定されているかどうかをチェックする
    public boolean isValidPrices(Integer priceMin, Integer highestPrice) {
        return highestPrice >= priceMin;
    }

    // 営業時間が正しく設定されているかどうかをチェックする
    public boolean isValidBusinessHours(String openingTime, String closingTime) {
        try {
            // String型のopeningTimeとclosingTimeをLocalTime型に変換
            LocalTime opening = LocalTime.parse(openingTime);
            LocalTime closing = LocalTime.parse(closingTime);

            // 営業終了時間が営業開始時間より後かどうかを確認
            return closing.isAfter(opening);
        } catch (DateTimeParseException e) {
            // 例外が発生した場合（フォーマットが正しくない場合）はfalseを返す
        	System.out.println("時間のフォーマットが正しくありません: " + e.getMessage());
            return false;
        }
    }
    
 // すべての店舗を作成日時が新しい順に並べ替え、ページングされた状態で取得する
    public Page<Store> findAllStoresByOrderByCreatedAtDesc(Pageable pageable) {
        return storeRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    // すべての店舗を最低価格が安い順に並べ替え、ページングされた状態で取得する
    public Page<Store> findAllStoresByOrderByPriceMinAsc(Pageable pageable) {
        return storeRepository.findAllByOrderByPriceMinAsc(pageable);
    }
    
     // すべての店舗を平均評価が高い順に並べ替え、ページングされた状態で取得する
     public Page<Store> findAllStoresByOrderByAverageRatingDesc(Pageable pageable) {
         return storeRepository.findAllByOrderByAverageRatingDesc(pageable);
     }    

    // 指定されたキーワードを店舗名または住所またはカテゴリ名に含む店舗を作成日時が新しい順に並べ替え、ページングされた状態で取得する
    public Page<Store> findStoresByNameLikeOrAddressLikeOrCategoryNameLikeOrderByCreatedAtDesc(String nameKeyword, String addressKeyword, String categoryNameKeyword, Pageable pageable) {
        return storeRepository.findByNameLikeOrAddressLikeOrCategoryNameLikeOrderByCreatedAtDesc(nameKeyword, addressKeyword, categoryNameKeyword, pageable);
    }

    // 指定されたキーワードを店舗名または住所またはカテゴリ名に含む店舗を最低価格が安い順に並べ替え、ページングされた状態で取得する
    public Page<Store> findStoresByNameLikeOrAddressLikeOrCategoryNameLikeOrderByPriceMinAsc(String nameKeyword, String addressKeyword, String categoryNameKeyword, Pageable pageable) {
        return storeRepository.findByNameLikeOrAddressLikeOrCategoryNameLikeOrderByPriceMinAsc(nameKeyword, addressKeyword, categoryNameKeyword, pageable);
    }
    
     // 指定されたキーワードを店舗名または住所またはカテゴリ名に含む店舗を平均評価が高い順に並べ替え、ページングされた状態で取得する
     public Page<Store> findStoresByNameLikeOrAddressLikeOrCategoryNameLikeOrderByAverageRatingDesc(String nameKeyword, String addressKeyword, String categoryNameKeyword, Pageable pageable) {
         return storeRepository.findByNameLikeOrAddressLikeOrCategoryNameLikeOrderByAverageRatingDesc(nameKeyword, addressKeyword, categoryNameKeyword, pageable);
     }    

    // 指定されたidのカテゴリが設定された店舗を作成日時が新しい順に並べ替え、ページングされた状態で取得する
    public Page<Store> findStoresByCategoryIdOrderByCreatedAtDesc(Integer categoryId, Pageable pageable) {
        return storeRepository.findByCategoryIdOrderByCreatedAtDesc(categoryId, pageable);
    }

    // 指定されたidのカテゴリが設定された店舗を最低価格が安い順に並べ替え、ページングされた状態で取得する
    public Page<Store> findStoresByCategoryIdOrderByPriceMinAsc(Integer categoryId, Pageable pageable) {
        return storeRepository.findByCategoryIdOrderByPriceMinAsc(categoryId, pageable);
    }
    
     // 指定されたidのカテゴリが設定された店舗を平均評価が高い順に並べ替え、ページングされた状態で取得する
     public Page<Store> findStoresByCategoryIdOrderByAverageRatingDesc(Integer categoryId, Pageable pageable) {
         return storeRepository.findByCategoryIdOrderByAverageRatingDesc(categoryId, pageable);
     }    

    // 指定された最低価格以下の店舗を作成日時が新しい順に並べ替え、ページングされた状態で取得する
    public Page<Store> findStoresByPriceMinLessThanEqualOrderByCreatedAtDesc(Integer price, Pageable pageable) {
        return storeRepository.findByPriceMinLessThanEqualOrderByCreatedAtDesc(price, pageable);
    }

    // 指定された最低価格以下の店舗を最低価格が安い順に並べ替え、ページングされた状態で取得する
    public Page<Store> findStoresByPriceMinLessThanEqualOrderByPriceMinAsc(Integer price, Pageable pageable) {
        return storeRepository.findByPriceMinLessThanEqualOrderByPriceMinAsc(price, pageable);
    }
    
     // 指定された最低価格以下の店舗を平均評価が高い順に並べ替え、ページングされた状態で取得する
     public Page<Store> findStoresByPriceMinLessThanEqualOrderByAverageRatingDesc(Integer price, Pageable pageable) {
         return storeRepository.findByPriceMinLessThanEqualOrderByAverageRatingDesc(price, pageable);
     }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
