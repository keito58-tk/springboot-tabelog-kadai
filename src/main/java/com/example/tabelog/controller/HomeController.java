package com.example.tabelog.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.tabelog.entity.Store;
import com.example.tabelog.repository.StoreRepository;
import com.example.tabelog.service.StoreService;

@Controller
public class HomeController {
	private final StoreRepository storeRepository;   
	private final StoreService storeService;
    
    public HomeController(StoreRepository storeRepository, StoreService storeService) {
        this.storeRepository = storeRepository;            
        this.storeService = storeService;
    }
	
	@GetMapping("/")
	public String index(Model model) {
		List<Store> newStores = storeRepository.findTop10ByOrderByCreatedAtDesc();
		Page<Store> highlyRatedStores = storeService.findAllStoresByOrderByAverageRatingDesc(PageRequest.of(0, 6));
		
        model.addAttribute("newStores", newStores); 
		
		return "index";
	}
}
