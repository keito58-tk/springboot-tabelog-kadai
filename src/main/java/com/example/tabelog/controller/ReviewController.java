package com.example.tabelog.controller;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.tabelog.entity.Review;
import com.example.tabelog.entity.Store;
import com.example.tabelog.entity.User;
import com.example.tabelog.form.ReviewEditForm;
import com.example.tabelog.form.ReviewRegisterForm;
import com.example.tabelog.security.UserDetailsImpl;
import com.example.tabelog.service.ReviewService;
import com.example.tabelog.service.StoreService;


@Controller
@RequestMapping("/stores/{storeId}/review")
public class ReviewController {
	private final ReviewService reviewService;
    private final StoreService storeService;

    public ReviewController(ReviewService reviewService, StoreService storeService) {
        this.reviewService = reviewService;
        this.storeService = storeService;
    }

    @GetMapping
    public String index(@PathVariable(name = "storeId") Integer storeId,
                        @PageableDefault(page = 0, size = 5, sort = "id", direction = Direction.ASC) Pageable pageable,
                        @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                        RedirectAttributes redirectAttributes,
                        Model model)
    {
        Optional<Store> optionalStore  = storeService.findStoreById(storeId);

        if (optionalStore.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");

            return "redirect:/stores";
        }

        Store store = optionalStore.get();
        User user = userDetailsImpl.getUser();
        String userRoleName = user.getRole().getName();
        Page<Review> reviewPage;

        
        // ユーザーの役割に応じて表示するレビューの数を調整
        if (userRoleName.equals("ROLE_PAID_MEMBER")) {
        	// 有料プランユーザーは全てのレビューを表示
            reviewPage = reviewService.findReviewsByStoreOrderByCreatedAtDesc(store, pageable);
        } else {
        	// 無料プランユーザーは最新3件のみ表示
            reviewPage = reviewService.findReviewsByStoreOrderByCreatedAtDesc(store, PageRequest.of(0, 3));
        }

        // ユーザーが既にレビューを投稿しているかどうかを確認
        boolean hasUserAlreadyReviewed = reviewService.hasUserAlreadyReviewed(store, user);

        model.addAttribute("store", store);
        model.addAttribute("userRoleName", userRoleName);
        model.addAttribute("reviewPage", reviewPage);
        model.addAttribute("hasUserAlreadyReviewed", hasUserAlreadyReviewed);

        return "review/index";
    }

    @GetMapping("/register")
    public String register(@PathVariable(name = "storeId") Integer storeId,
                           @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                           RedirectAttributes redirectAttributes,
                           Model model)
    {
        User user = userDetailsImpl.getUser();

        // ユーザーが無料プランの場合、サブスクリプション登録ページにリダイレクト
        if (user.getRole().getName().equals("ROLE_FREE_MEMBER")) {
            redirectAttributes.addFlashAttribute("subscriptionMessage", "この機能を利用するには有料プランへの登録が必要です。");

            return "redirect:/subscription/register";
        }

        Optional<Store> optionalStore  = storeService.findStoreById(storeId);

        if (optionalStore.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");

            return "redirect:/stores";
        }

        Store store = optionalStore.get();
        ReviewRegisterForm reviewRegisterForm = new ReviewRegisterForm();
        reviewRegisterForm.setRating(5);

        model.addAttribute("store", store);
        model.addAttribute("reviewRegisterForm", reviewRegisterForm);

        return "review/register";
    }

    @PostMapping("/create")
    public String create(@PathVariable(name = "storeId") Integer storeId,
                         @ModelAttribute @Validated ReviewRegisterForm reviewRegisterForm,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                         RedirectAttributes redirectAttributes,
                         Model model)
    {
        User user = userDetailsImpl.getUser();

        // ユーザーが無料プランの場合、サブスクリプション登録ページにリダイレクト
        if (user.getRole().getName().equals("ROLE_FREE_MEMBER")) {
            redirectAttributes.addFlashAttribute("subscriptionMessage", "この機能を利用するには有料プランへの登録が必要です。");

            return "redirect:/subscription/register";
        }

        Optional<Store> optionalStore  = storeService.findStoreById(storeId);

        if (optionalStore.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");

            return "redirect:/stores";
        }

        Store store = optionalStore.get();

        if (bindingResult.hasErrors()) {
            model.addAttribute("store", store);
            model.addAttribute("reviewRegisterForm", reviewRegisterForm);

            return "review/register";
        }

        // 新しいレビューを作成
        reviewService.createReview(reviewRegisterForm, store, user);
        redirectAttributes.addFlashAttribute("successMessage", "レビューを投稿しました。");

        return "redirect:/stores/{storeId}";
    }

    @GetMapping("/{reviewId}/edit")
    public String edit(@PathVariable(name = "storeId") Integer storeId,
                       @PathVariable(name = "reviewId") Integer reviewId,
                       @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                       RedirectAttributes redirectAttributes,
                       Model model)
    {
        User user = userDetailsImpl.getUser();

        if (user.getRole().getName().equals("ROLE_FREE_MEMBER")) {
            redirectAttributes.addFlashAttribute("subscriptionMessage", "この機能を利用するには有料プランへの登録が必要です。");

            return "redirect:/subscription/register";
        }

        Optional<Store> optionalStore  = storeService.findStoreById(storeId);
        Optional<Review> optionalReview  = reviewService.findReviewById(reviewId);

        if (optionalStore.isEmpty() || optionalReview.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "指定されたページが見つかりません。");

            return "redirect:/stores";
        }

        Review review = optionalReview.get();

        // レビューが指定された店舗に属しておらず、かつユーザー自身のレビューでない場合、不正なアクセスとみなしてエラーメッセージを表示
        if (!review.getStore().getId().equals(storeId) || !review.getUser().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");

            return "redirect:/stores/{storeId}";
        }

        Store store = optionalStore.get();
        ReviewEditForm reviewEditForm = new ReviewEditForm(review.getRating(), review.getComment());

        model.addAttribute("store", store);
        model.addAttribute("review", review);
        model.addAttribute("reviewEditForm", reviewEditForm);

        return "review/edit";
    }

    @PostMapping("/{reviewId}/update")
    public String update(@PathVariable(name = "storeId") Integer storeId,
                         @PathVariable(name = "reviewId") Integer reviewId,
                         @ModelAttribute @Validated ReviewEditForm reviewEditForm,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                         RedirectAttributes redirectAttributes,
                         Model model)
    {
        User user = userDetailsImpl.getUser();

        if (user.getRole().getName().equals("ROLE_FREE_MEMBER")) {
            redirectAttributes.addFlashAttribute("subscriptionMessage", "この機能を利用するには有料プランへの登録が必要です。");

            return "redirect:/subscription/register";
        }

        Optional<Store> optionalStore  = storeService.findStoreById(storeId);
        Optional<Review> optionalReview  = reviewService.findReviewById(reviewId);

        // 店舗またはレビューが存在しない場合、エラーメッセージを表示して店舗一覧にリダイレクト
        if (optionalStore.isEmpty() || optionalReview.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "指定されたページが見つかりません。");

            return "redirect:/stores";
        }

        Review review = optionalReview.get();

        // レビューが指定された店舗に属しておらず、かつユーザー自身のレビューでない場合、不正なアクセスとみなしてエラーメッセージを表示
        if (!review.getStore().getId().equals(storeId) || !review.getUser().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");

            return "redirect:/stores/{storeId}";
        }

        Store store = optionalStore.get();

        if (bindingResult.hasErrors()) {
            model.addAttribute("store", store);
            model.addAttribute("review", review);
            model.addAttribute("reviewEditForm", reviewEditForm);

            return "review/edit";
        }

        // レビューを更新
        reviewService.updateReview(reviewEditForm, review);
        redirectAttributes.addFlashAttribute("successMessage", "レビューを編集しました。");

        return "redirect:/stores/{storeId}";
    }

    @PostMapping("/{reviewId}/delete")
    public String delete(@PathVariable(name = "storeId") Integer storeId,
                         @PathVariable(name = "reviewId") Integer reviewId,
                         @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                         RedirectAttributes redirectAttributes)
    {
        User user = userDetailsImpl.getUser();

        if (user.getRole().getName().equals("ROLE_FREE_MEMBER")) {
            redirectAttributes.addFlashAttribute("subscriptionMessage", "この機能を利用するには有料プランへの登録が必要です。");

            return "redirect:/subscription/register";
        }

        Optional<Store> optionalStore  = storeService.findStoreById(storeId);
        Optional<Review> optionalReview  = reviewService.findReviewById(reviewId);

        if (optionalStore.isEmpty() || optionalReview.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "指定されたページが見つかりません。");

            return "redirect:/stores";
        }

        Review review = optionalReview.get();

        if (!review.getStore().getId().equals(storeId) || !review.getUser().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");

            return "redirect:/stores/{storeId}";
        }

        // レビューを削除
        reviewService.deleteReview(review);
        redirectAttributes.addFlashAttribute("successMessage", "レビューを削除しました。");

        return "redirect:/stores/{storeId}";
    }
}