package com.example.tabelog.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.tabelog.entity.Favorite;
import com.example.tabelog.entity.Store;
import com.example.tabelog.entity.User;
import com.example.tabelog.repository.StoreRepository;
import com.example.tabelog.security.UserDetailsImpl;
import com.example.tabelog.service.CategoryService;
import com.example.tabelog.service.FavoriteService;
import com.example.tabelog.service.StoreService;

@Controller
@RequestMapping("/stores")
public class StoreController {
	private final StoreRepository storeRepository; 
	private final StoreService storeService;
	private final FavoriteService favoriteService;
	private final CategoryService categoryService;
	
    
    public StoreController(StoreRepository storeRepository, StoreService storeService, FavoriteService favoriteService, CategoryService categoryService) {
        this.storeRepository = storeRepository;
        this.storeService = storeService;
        this.favoriteService = favoriteService;
        this.categoryService = categoryService;
        
    }     
  
    @GetMapping
    public String index(@RequestParam(name = "keyword", required = false) String keyword,
    					@RequestParam(name = "categoryId", required = false) Integer categoryId,
                        @RequestParam(name = "area", required = false) String area,
                        @RequestParam(name = "priceMax", required = false) Integer priceMax,
                        @RequestParam(name = "order", required = false) String order,
                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
                        Model model) 
    {
        Page<Store> storePage;
        
        // キーワードによる検索
        if (keyword != null && !keyword.isEmpty()) {
        	if (order != null && order.equals("priceMaxAsc")) {
        		// 価格の昇順でソート
                storePage = storeRepository.findByNameLikeOrAddressLikeOrderByPriceMaxAsc("%" + keyword + "%", "%" + keyword + "%", pageable);
        	} else if (order != null && order.equals("ratingDesc")) {
        		// 評価の降順でソート
                storePage = storeService.findStoresByNameLikeOrAddressLikeOrCategoryNameLikeOrderByAverageRatingDesc(keyword, keyword, keyword, pageable);
        	} else if (order != null && order.equals("popularDesc")) {
        		// 人気の降順でソート
                storePage = storeService.findStoresByNameLikeOrAddressLikeOrCategoryNameLikeOrderByReservationCountDesc(keyword, keyword, keyword, pageable);
            } else {
            	// 作成日時の降順でソート（デフォルト）
                storePage = storeRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc("%" + keyword + "%", "%" + keyword + "%", pageable);
            }
        } 
        // エリアによる検索
        else if (area != null && !area.isEmpty()) {
        	if (order != null && order.equals("priceMaxAsc")) {
        		storePage = storeRepository.findByAddressLikeOrderByPriceMaxAsc("%" + area + "%", pageable);
        	} else if (order != null && order.equals("ratingDesc")) {
                storePage = storeService.findStoresByCategoryIdOrderByAverageRatingDesc(categoryId, pageable);
        	} else if (order != null && order.equals("popularDesc")) {
                storePage = storeService.findStoresByCategoryIdOrderByReservationCountDesc(categoryId, pageable);
            } else {
            	storePage = storeRepository.findByAddressLikeOrderByCreatedAtDesc("%" + area + "%", pageable);
            }
        }
        // 価格上限による検索
        else if (priceMax != null) {
        	if (order != null && order.equals("priceMaxAsc")) {
        		storePage = storeRepository.findByPriceMaxLessThanEqualOrderByPriceMaxAsc(priceMax, pageable);
        	} else if (order != null && order.equals("ratingDesc")) {
                storePage = storeService.findStoresByPriceMinLessThanEqualOrderByAverageRatingDesc(priceMax, pageable);
        	} else if (order != null && order.equals("popularDesc")) {
                storePage = storeService.findStoresByPriceMinLessThanEqualOrderByReservationCountDesc(priceMax, pageable);
            } else {
            	storePage = storeRepository.findByPriceMaxLessThanEqualOrderByCreatedAtDesc(priceMax, pageable);
            }
        }
        // カテゴリIDによる検索
        else if (categoryId != null) {
            // カテゴリIDが指定されている場合の処理
            if (order != null && order.equals("priceMaxAsc")) {
                storePage = storeRepository.findByCategoryIdOrderByPriceMaxAsc(categoryId, pageable);
            } else if (order != null && order.equals("ratingDesc")) {
                storePage = storeService.findStoresByCategoryIdOrderByAverageRatingDesc(categoryId, pageable);
            } else if (order != null && order.equals("popularDesc")) {
                storePage = storeService.findStoresByCategoryIdOrderByReservationCountDesc(categoryId, pageable);
            } else {
                storePage = storeRepository.findByCategoryIdOrderByCreatedAtDesc(categoryId, pageable);
            }
        }
        // フィルタなしで全店舗を取得
        else {
        	 if (order != null && order.equals("priceMaxAsc")) {
        		 storePage = storeRepository.findAllByOrderByPriceMaxAsc(pageable);
        	 } else if (order != null && order.equals("ratingDesc")) {
                 storePage = storeService.findAllStoresByOrderByAverageRatingDesc(pageable);
        	 } else if (order != null && order.equals("popularDesc")) {
                 storePage = storeService.findAllStoresByOrderByReservationCountDesc(pageable);
             } else {
            	 storePage = storeRepository.findAllByOrderByCreatedAtDesc(pageable);   
             }
        
        }                
        
        
        model.addAttribute("storePage", storePage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("area", area);
        model.addAttribute("priceMax", priceMax);
        model.addAttribute("order", order);
        model.addAttribute("categories", categoryService.findAllCategories()); // カテゴリー一覧
        
        return "stores/index";
    }
    
    @GetMapping("/{id}")
    public String show(@PathVariable(name = "id") Integer id,
    				   @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    				   Model model ) {
        Store store = storeRepository.getReferenceById(id);
        Favorite favorite = null;
        boolean isFavorite = false;

        // 認証ユーザーが存在する場合
        if (userDetailsImpl != null) {
            User user = userDetailsImpl.getUser();
            // ユーザーがお気に入りに登録しているかを確認
            isFavorite = favoriteService.isFavorite(store, user);

            if (isFavorite) {
            	// お気に入りエンティティを取得
                favorite = favoriteService.findFavoriteByStoreAndUser(store, user);
            }
        }
        
        model.addAttribute("store", store);
        model.addAttribute("favorite", favorite);
        model.addAttribute("isFavorite", isFavorite);
    
        
        return "stores/show";
    }
}
