package com.example.tabelog.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.tabelog.entity.Category;
import com.example.tabelog.form.CategoryEditForm;
import com.example.tabelog.form.CategoryRegisterForm;
import com.example.tabelog.service.CategoryService;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {
	private final CategoryService categoryService;
	 
    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String index(@RequestParam(name = "keyword", required = false) String keyword,
                        @PageableDefault(page = 0, size = 15, sort = "id", direction = Direction.ASC) Pageable pageable,
                        Model model)
    {
        Page<Category> categoryPage;

        // キーワードが提供されている場合は名前で検索、それ以外は全カテゴリを取得
        if (keyword != null && !keyword.isEmpty()) {
            categoryPage = categoryService.findCategoriesByNameLike(keyword, pageable);
        } else {
            categoryPage = categoryService.findAllCategories(pageable);
        }

        // モデルに必要な属性を追加
        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryRegisterForm", new CategoryRegisterForm());
        model.addAttribute("categoryEditForm", new CategoryEditForm());

        return "admin/categories/index";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute @Validated CategoryRegisterForm categoryRegisterForm,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model)
    {
    	
    	// バリデーションエラーが存在する場合
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "カテゴリ名を入力してください。");

            return "redirect:/admin/categories";
        }

        // カテゴリを作成
        categoryService.createCategory(categoryRegisterForm);
        redirectAttributes.addFlashAttribute("successMessage", "カテゴリを登録しました。");

        return "redirect:/admin/categories";
    }

    @PostMapping("/{id}/update")
    public String update(@ModelAttribute @Validated CategoryEditForm categoryEditForm,
                         BindingResult bindingResult,
                         @PathVariable(name = "id") Integer id,
                         RedirectAttributes redirectAttributes,
                         Model model)
    {
    	
    	// 指定されたIDのカテゴリを取得
        Optional<Category> optionalCategory = categoryService.findCategoryById(id);

    	// カテゴリが存在しない場合
        if (optionalCategory.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "カテゴリが存在しません。");

            return "redirect:/admin/categories";
        }

        // バリデーションエラーが存在する場合
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "カテゴリ名を入力してください。");

            return "redirect:/admin/categories";
        }

        // カテゴリを更新
        Category category = optionalCategory.get();
        categoryService.updateCategory(categoryEditForm, category);
        redirectAttributes.addFlashAttribute("successMessage", "カテゴリを編集しました。");

        return "redirect:/admin/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
    	// 指定されたIDのカテゴリを取得
        Optional<Category> optionalCategory = categoryService.findCategoryById(id);

        // カテゴリが存在しない場合
        if (optionalCategory.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "カテゴリが存在しません。");

            return "redirect:/admin/categories";
        }

        // カテゴリを削除
        Category category = optionalCategory.get();
        categoryService.deleteCategory(category);
        redirectAttributes.addFlashAttribute("successMessage", "カテゴリを削除しました。");

        return "redirect:/admin/categories";
    }
}
