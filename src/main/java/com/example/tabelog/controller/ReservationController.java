package com.example.tabelog.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.tabelog.entity.Reservation;
import com.example.tabelog.entity.Store;
import com.example.tabelog.entity.User;
import com.example.tabelog.form.ReservationRegisterForm;
import com.example.tabelog.security.UserDetailsImpl;
import com.example.tabelog.service.ReservationService;
import com.example.tabelog.service.StoreService;

@Controller
public class ReservationController {
	private final ReservationService reservationService;
    private final StoreService storeService;

    public ReservationController(ReservationService reservationService, StoreService storeService) {
        this.reservationService = reservationService;
        this.storeService = storeService;
    }

    @GetMapping("/reservations")
    public String index(@PageableDefault(page = 0, size = 15, sort = "id", direction = Direction.ASC) Pageable pageable,
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

        // ユーザーの予約を取得（予約日時の降順）
        Page<Reservation> reservationPage = reservationService.findReservationsByUserOrderByReservedDatetimeDesc(user, pageable);

        model.addAttribute("reservationPage", reservationPage);
        model.addAttribute("currentDateTime", LocalDateTime.now());

        return "reservations/index";
    }

    @GetMapping("/stores/{storeId}/reservations/register")
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

        // 店舗が存在しない場合、エラーメッセージを表示して店舗一覧にリダイレクト
        if (optionalStore.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");

            return "redirect:/stores";
        }

        Store store = optionalStore.get();

        model.addAttribute("store", store);
        model.addAttribute("reservationRegisterForm", new ReservationRegisterForm());

        return "reservations/register";
    }

    @PostMapping("/stores/{storeId}/reservations/create")
    public String create(@PathVariable(name = "storeId") Integer storeId,
                         @ModelAttribute @Validated ReservationRegisterForm reservationRegisterForm,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                         RedirectAttributes redirectAttributes,
                         Model model)
    {
    	
    	Optional<Store> optionalStore = storeService.findStoreById(storeId);
    	
        if (optionalStore.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");
            return "redirect:/stores";
        }

        // 予約日のバリデーション：予約日は今日以降である必要がある
        LocalDate reservationDate = reservationRegisterForm.getReservationDate();
        if (reservationDate != null && reservationDate.isBefore(LocalDate.now())) {
            bindingResult.rejectValue("reservationDate", "error.reservationDate", "予約日は今日以降の日付を選択してください。");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("store", optionalStore.get());
            return "reservations/register";
        }

        Store store = optionalStore.get();
        User user = userDetailsImpl.getUser();
        
        // 新しい予約を作成
        reservationService.createReservation(reservationRegisterForm, store, user);

        redirectAttributes.addFlashAttribute("successMessage", "予約が完了しました。");
        return "redirect:/reservations";
    	}

    	@PostMapping("/reservations/{reservationId}/delete")
    	public String delete(@PathVariable(name = "reservationId") Integer reservationId,
                         @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                         RedirectAttributes redirectAttributes)
    	{
        User user = userDetailsImpl.getUser();

        // ユーザーが無料プランの場合、サブスクリプション登録ページにリダイレクト
        if (user.getRole().getName().equals("ROLE_FREE_MEMBER")) {
            redirectAttributes.addFlashAttribute("subscriptionMessage", "この機能を利用するには有料プランへの登録が必要です。");

            return "redirect:/subscription/register";
        }

        Optional<Reservation> optionalReservation  = reservationService.findReservationById(reservationId);

        if (optionalReservation.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "予約が存在しません。");

            return "redirect:/reservations";
        }

        Reservation reservation = optionalReservation.get();

        // 予約が現在のユーザーのものでない場合、不正なアクセスとみなしエラーメッセージを表示
        if (!reservation.getUser().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");

            return "redirect:/reservations";
        }

        // 予約を削除
        reservationService.deleteReservation(reservation);
        redirectAttributes.addFlashAttribute("successMessage", "予約をキャンセルしました。");

        return "redirect:/reservations";
    }
}
