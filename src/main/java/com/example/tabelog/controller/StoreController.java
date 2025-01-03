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
import com.example.tabelog.service.FavoriteService;
import com.example.tabelog.service.StoreService;

@Controller
@RequestMapping("/stores")
public class StoreController {
	private final StoreRepository storeRepository; 
	private final StoreService storeService;
	private final FavoriteService favoriteService;
    
    public StoreController(StoreRepository storeRepository, StoreService storeService, FavoriteService favoriteService) {
        this.storeRepository = storeRepository;
        this.storeService = storeService;
        this.favoriteService = favoriteService;
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
                
        if (keyword != null && !keyword.isEmpty()) {
        	if (order != null && order.equals("priceMaxAsc")) {
                storePage = storeRepository.findByNameLikeOrAddressLikeOrderByPriceMaxAsc("%" + keyword + "%", "%" + keyword + "%", pageable);
        	} else if (order != null && order.equals("ratingDesc")) {
                storePage = storeService.findStoresByNameLikeOrAddressLikeOrCategoryNameLikeOrderByAverageRatingDesc(keyword, keyword, keyword, pageable);
        	} else if (order != null && order.equals("popularDesc")) {
                storePage = storeService.findStoresByNameLikeOrAddressLikeOrCategoryNameLikeOrderByReservationCountDesc(keyword, keyword, keyword, pageable);
            } else {
                storePage = storeRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc("%" + keyword + "%", "%" + keyword + "%", pageable);
            }
        } else if (area != null && !area.isEmpty()) {
        	if (order != null && order.equals("priceMaxAsc")) {
        		storePage = storeRepository.findByAddressLikeOrderByPriceMaxAsc("%" + area + "%", pageable);
        	} else if (order != null && order.equals("ratingDesc")) {
                storePage = storeService.findStoresByCategoryIdOrderByAverageRatingDesc(categoryId, pageable);
        	} else if (order != null && order.equals("popularDesc")) {
                storePage = storeService.findStoresByCategoryIdOrderByReservationCountDesc(categoryId, pageable);
            } else {
            	storePage = storeRepository.findByAddressLikeOrderByCreatedAtDesc("%" + area + "%", pageable);
            }
        } else if (priceMax != null) {
        	if (order != null && order.equals("priceMaxAsc")) {
        		storePage = storeRepository.findByPriceMaxLessThanEqualOrderByPriceMaxAsc(priceMax, pageable);
        	} else if (order != null && order.equals("ratingDesc")) {
                storePage = storeService.findStoresByPriceMinLessThanEqualOrderByAverageRatingDesc(priceMax, pageable);
        	} else if (order != null && order.equals("popularDesc")) {
                storePage = storeService.findStoresByPriceMinLessThanEqualOrderByReservationCountDesc(priceMax, pageable);
            } else {
            	storePage = storeRepository.findByPriceMaxLessThanEqualOrderByCreatedAtDesc(priceMax, pageable);
            }
        } else {
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
        
        return "stores/index";
    }
    
    @GetMapping("/{id}")
    public String show(@PathVariable(name = "id") Integer id,
    				   @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    				   Model model ) {
        Store store = storeRepository.getReferenceById(id);
        Favorite favorite = null;
        boolean isFavorite = false;

        if (userDetailsImpl != null) {
            User user = userDetailsImpl.getUser();
            isFavorite = favoriteService.isFavorite(store, user);

            if (isFavorite) {
                favorite = favoriteService.findFavoriteByStoreAndUser(store, user);
            }
        }
        
        model.addAttribute("store", store);
        model.addAttribute("favorite", favorite);
        model.addAttribute("isFavorite", isFavorite);
    
        
        return "stores/show";
    }
}
