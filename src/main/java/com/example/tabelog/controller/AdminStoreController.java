package com.example.tabelog.controller;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.tabelog.entity.Category;
import com.example.tabelog.entity.Store;
import com.example.tabelog.form.StoreEditForm;
import com.example.tabelog.form.StoreRegisterForm;
import com.example.tabelog.repository.StoreRepository;
import com.example.tabelog.service.CategoryService;
import com.example.tabelog.service.StoreService;

@Controller
@RequestMapping("/admin/stores")
public class AdminStoreController {
	private final StoreRepository storeRepository;
	private final StoreService storeService;
	private final CategoryService categoryService;
	
	public AdminStoreController(StoreRepository storeRepository, StoreService storeService, CategoryService categoryService) {
		this.storeRepository = storeRepository;
		this.storeService = storeService;
		this.categoryService = categoryService;
	}
	
	@GetMapping
	public String index(Model model, @PageableDefault(page = 0, size = 10, sort = "id",direction = Direction.ASC) Pageable pageable, @RequestParam(name = "keyword", required = false) String keyword) {
		Page<Store> storePage;
		
		if (keyword != null && !keyword.isEmpty()) {
			storePage = storeRepository.findByNameLike("%" + keyword + "%", pageable);
		} else {
			storePage = storeRepository.findAll(pageable);
		}
		
		model.addAttribute("storePage", storePage);
		model.addAttribute("keyword", keyword);
		
		return "admin/stores/index";
	}
	
	@GetMapping("/{id}")
	public String show(@PathVariable(name = "id") Integer id, Model model) {
		Store store = storeRepository.getReferenceById(id);
		
		model.addAttribute("store", store);
		
		return "admin/stores/show";
	}
	
	@GetMapping("/register")
	public String register(Model model) {
		List<Category> categories = categoryService.findAllCategories();
		
		model.addAttribute("storeRegisterForm", new StoreRegisterForm());
		model.addAttribute("categories", categories);
		return "admin/stores/register";
	}
	
	@PostMapping("/create")
	public String create(@ModelAttribute @Validated StoreRegisterForm storeRegisterForm, 
						 BindingResult bindingResult, 
						 RedirectAttributes redirectAttributes,
						 Model model)
		{
	         Integer priceMin = storeRegisterForm.getPriceMin();
	         Integer priceMax = storeRegisterForm.getPriceMax();
	         String openingTime = storeRegisterForm.getOpeningTime();
	         String closingTime = storeRegisterForm.getClosingTime();
	 
	         if (priceMin != null && priceMax != null && !storeService.isValidPrices(priceMin, priceMax)) {
	                 FieldError priceMinError = new FieldError(bindingResult.getObjectName(), "priceMin", "最低価格は最高価格以下に設定してください。");
	                 FieldError priceMaxError = new FieldError(bindingResult.getObjectName(), "priceMax", "最高価格は最低価格以上に設定してください。");
	                 bindingResult.addError(priceMinError);
	                 bindingResult.addError(priceMaxError);
	         }
	 
	         if (openingTime != null && closingTime != null && !storeService.isValidBusinessHours(openingTime, closingTime)) {
	                 FieldError openingTimeError = new FieldError(bindingResult.getObjectName(), "openingTime", "開店時間は閉店時間よりも前に設定してください。");
	                 FieldError closingTimeError = new FieldError(bindingResult.getObjectName(), "closingTime", "閉店時間は開店時間よりも後に設定してください。");
	                 bindingResult.addError(openingTimeError);
	                 bindingResult.addError(closingTimeError);
	         }
	 
	         if (bindingResult.hasErrors()) {
	        	 List<Category> categories = categoryService.findAllCategories();
	             model.addAttribute("storeRegisterForm", storeRegisterForm);
	             model.addAttribute("categories", categories);
	 
	             return "admin/stores/register";
	         }
	 
	         storeService.createStore(storeRegisterForm);
	         redirectAttributes.addFlashAttribute("successMessage", "店舗を登録しました。");
	 
	         return "redirect:/admin/stores";
	    }
	
	@GetMapping("/{id}/edit")
	public String edit(@PathVariable(name = "id", required = false) Integer id,
					   RedirectAttributes redirectAttributes,
					   Model model) {
		Optional<Store> optionalStore = storeService.findStoreById(id);
		 
        if (optionalStore.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");

            return "redirect:/admin/stores";
        }

        Store store = optionalStore.get();
        StoreEditForm storeEditForm = new StoreEditForm();
        List<Category> categories = categoryService.findAllCategories();
        model.addAttribute("store", store);
        model.addAttribute("storeEditForm", storeEditForm);
        model.addAttribute("categories", categories);

        return "admin/stores/edit";
    }
	
	@PostMapping("/{id}/update")
	public String update(@ModelAttribute @Validated StoreEditForm storeEditForm, 
						 BindingResult bindingResult, 
						 RedirectAttributes redirectAttributes,
						 @PathVariable(name = "id") Integer id,
						 Model model) {
		Optional<Store> optionalStore = storeService.findStoreById(id);
		Store store = optionalStore.get();
		
		if (bindingResult.hasErrors()) {
			List<Category> categories = categoryService.findAllCategories();
            model.addAttribute("store", store);
            model.addAttribute("storeEditForm", storeEditForm);
            model.addAttribute("categories", categories);
             
			return "admin/stores/edit";
		}
		
		storeService.update(storeEditForm);
		redirectAttributes.addFlashAttribute("successMessage", "店舗情報を編集しました。");
		
		return "redirect:/admin/stores";
	}
	
	@PostMapping("/{id}/delete")
	public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
		storeRepository.deleteById(id);
		
		redirectAttributes.addFlashAttribute("successMessage", "店舗情報を削除しました。");
		
		return "redirect:/admin/stores";
	}
}
