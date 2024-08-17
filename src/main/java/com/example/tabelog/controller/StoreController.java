package com.example.tabelog.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.tabelog.entity.Store;
import com.example.tabelog.form.ReservationInputForm;
import com.example.tabelog.repository.StoreRepository;

@Controller
@RequestMapping("/stores")
public class StoreController {
	private final StoreRepository storeRepository;        
    
    public StoreController(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;            
    }     
  
    @GetMapping
    public String index(@RequestParam(name = "keyword", required = false) String keyword,
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
            } else {
                storePage = storeRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc("%" + keyword + "%", "%" + keyword + "%", pageable);
            }
        } else if (area != null && !area.isEmpty()) {
        	if (order != null && order.equals("priceMaxAsc")) {
        		storePage = storeRepository.findByAddressLikeOrderByPriceMaxAsc("%" + area + "%", pageable);
            } else {
            	storePage = storeRepository.findByAddressLikeOrderByCreatedAtDesc("%" + area + "%", pageable);
            }
        } else if (priceMax != null) {
        	if (order != null && order.equals("priceMaxAsc")) {
        		storePage = storeRepository.findByPriceMaxLessThanEqualOrderByPriceMaxAsc(priceMax, pageable);
            } else {
            	storePage = storeRepository.findByPriceMaxLessThanEqualOrderByCreatedAtDesc(priceMax, pageable);
            }
        } else {
        	 if (order != null && order.equals("priceMaxAsc")) {
        		 storePage = storeRepository.findAllByOrderByPriceMaxAsc(pageable);
             } else {
            	 storePage = storeRepository.findAllByOrderByCreatedAtDesc(pageable);   
             }
        
        }                
        
        model.addAttribute("storePage", storePage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("area", area);
        model.addAttribute("priceMax", priceMax);
        model.addAttribute("order", order);
        
        return "stores/index";
    }
    
    @GetMapping("/{id}")
    public String show(@PathVariable(name = "id") Integer id, Model model) {
        Store store = storeRepository.getReferenceById(id);
        
        model.addAttribute("store", store);
        model.addAttribute("reservationInputForm", new ReservationInputForm());
        
        return "stores/show";
    }
}
