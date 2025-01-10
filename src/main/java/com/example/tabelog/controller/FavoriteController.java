package com.example.tabelog.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.tabelog.entity.Favorite;
import com.example.tabelog.entity.Store;
import com.example.tabelog.entity.User;
import com.example.tabelog.security.UserDetailsImpl;
import com.example.tabelog.service.FavoriteService;
import com.example.tabelog.service.StoreService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class FavoriteController {
	private final StoreService storeService;
    private final FavoriteService favoriteService;

    public FavoriteController(StoreService storeService, FavoriteService favoriteService) {
        this.storeService = storeService;
        this.favoriteService = favoriteService;
    }

    @GetMapping("/favorites")
    public String index(@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
                        @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                        RedirectAttributes redirectAttributes,
                        Model model)
    {
    	// 認証されたユーザーを取得
        User user = userDetailsImpl.getUser();

        // ユーザーが無料プランの場合、サブスクリプション登録ページにリダイレクト
        if (user.getRole().getName().equals("ROLE_FREE_MEMBER")) {
            redirectAttributes.addFlashAttribute("subscriptionMessage", "この機能を利用するには有料プランへの登録が必要です。");

            return "redirect:/subscription/register";
        }

        // ユーザーのお気に入り店舗を取得（最新の順）
        Page<Favorite> favoritePage = favoriteService.findFavoritesByUserOrderByCreatedAtDesc(user, pageable);

        // モデルにお気に入りページ情報を追加
        model.addAttribute("favoritePage", favoritePage);

        return "favorites/index";
    }

    @PostMapping("/stores/{storeId}/favorites/create")
    public String create(@PathVariable(name = "storeId") Integer storeId,
                         @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                         RedirectAttributes redirectAttributes,
                         Model model)
    {
    	// 認証されたユーザーを取得
        User user = userDetailsImpl.getUser();

        // ユーザーが無料プランの場合、サブスクリプション登録ページにリダイレクト
        if (user.getRole().getName().equals("ROLE_FREE_MEMBER")) {
            redirectAttributes.addFlashAttribute("subscriptionMessage", "この機能を利用するには有料プランへの登録が必要です。");

            return "redirect:/subscription/register";
        }

        // 指定された店舗を取得
        Optional<Store> optionalStore  = storeService.findStoreById(storeId);

        // 店舗が存在しない場合、エラーメッセージを表示して店舗一覧にリダイレクト
        if (optionalStore.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");

            return "redirect:/stores";
        }

        Store store = optionalStore.get();

        // お気に入りを作成
        favoriteService.createFavorite(store, user);
        redirectAttributes.addFlashAttribute("successMessage", "お気に入りに追加しました。");

        return "redirect:/stores/{storeId}";
    }

    @PostMapping("/favorites/{favoriteId}/delete")
    public String delete(@PathVariable(name = "favoriteId") Integer favoriteId,
                         @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                         RedirectAttributes redirectAttributes,
                         HttpServletRequest httpServletRequest)
    {
    	// 認証されたユーザーを取得
        User user = userDetailsImpl.getUser();

        // ユーザーが無料プランの場合、サブスクリプション登録ページにリダイレクト
        if (user.getRole().getName().equals("ROLE_FREE_MEMBER")) {
            redirectAttributes.addFlashAttribute("subscriptionMessage", "この機能を利用するには有料プランへの登録が必要です。");

            return "redirect:/subscription/register";
        }

        // 指定されたお気に入りを取得
        Optional<Favorite> optionalFavorite  = favoriteService.findFavoriteById(favoriteId);
        // リファラヘッダーを取得（リダイレクト先に使用）
        String referer = httpServletRequest.getHeader("Referer");

        // お気に入りが存在しない場合、エラーメッセージを表示してリファラページにリダイレクト
        if (optionalFavorite.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "お気に入りが存在しません。");

            return "redirect:" + (referer != null ? referer : "/favorites");
        }

        Favorite favorite = optionalFavorite.get();

        // ユーザーが所有していないお気に入りを削除しようとした場合、エラーメッセージを表示してリファラページにリダイレクト
        if (!favorite.getUser().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");

            return "redirect:" + (referer != null ? referer : "/favorites");
        }

        // お気に入り削除
        favoriteService.deleteFavorite(favorite);
        redirectAttributes.addFlashAttribute("successMessage", "お気に入りを解除しました。");

        // リファラページにリダイレクト
        return "redirect:" + (referer != null ? referer : "/favorites");
    }
}
