package com.example.tabelog.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.tabelog.entity.Store;
import com.example.tabelog.repository.StoreRepository;

@Controller
public class HomeController {
	private final StoreRepository storeRepository;   
    
    public HomeController(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;            
    }
	
	@GetMapping("/")
	public String index(Model model) {
		// 最新の10店舗を取得（作成日時の降順）
		List<Store> newStores = storeRepository.findTop10ByOrderByCreatedAtDesc();
		
		// モデルに新着店舗のリストを追加
        model.addAttribute("newStores", newStores); 
		
		return "index";
	}
}
