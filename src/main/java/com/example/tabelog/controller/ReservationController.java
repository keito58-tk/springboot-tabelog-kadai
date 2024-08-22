package com.example.tabelog.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.tabelog.entity.Reservation;
import com.example.tabelog.entity.Store;
import com.example.tabelog.entity.User;
import com.example.tabelog.form.ReservationInputForm;
import com.example.tabelog.form.ReservationRegisterForm;
import com.example.tabelog.repository.ReservationRepository;
import com.example.tabelog.repository.StoreRepository;
import com.example.tabelog.security.UserDetailsImpl;
import com.example.tabelog.service.ReservationService;
import com.example.tabelog.service.StripeService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ReservationController {
 private final ReservationRepository reservationRepository; 
 private final StoreRepository storeRepository;
 private final ReservationService reservationService;
 private final StripeService stripeService;
 
     
     public ReservationController(ReservationRepository reservationRepository, StoreRepository storeRepository, ReservationService reservationService, StripeService stripeService ) {        
         this.reservationRepository = reservationRepository; 
         this. storeRepository = storeRepository;
         this.reservationService = reservationService;
         this.stripeService = stripeService;
     }   
     
 
     @GetMapping("/reservations")
     public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, Model model) {
         User user = userDetailsImpl.getUser();
         Page<Reservation> reservationPage = reservationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
         
         model.addAttribute("reservationPage", reservationPage);         
         
         return "reservations/index";
     }
     
     @GetMapping("/stores/{id}/reservations/input")
     public String input(@PathVariable(name = "id") Integer id,
             @ModelAttribute @Validated ReservationInputForm reservationInputForm,
             BindingResult bindingResult,
             RedirectAttributes redirectAttributes,
             Model model)
	{   
		Store store = storeRepository.getReferenceById(id);
		Integer numberOfPeople = reservationInputForm.getNumberOfPeople();   
		Integer capacity = store.getCapacity();
		
		if (numberOfPeople != null) {
		 if (!reservationService.isWithinCapacity(numberOfPeople, capacity)) {
		     FieldError fieldError = new FieldError(bindingResult.getObjectName(), "numberOfPeople", "予約人数が席数を超えています。");
		     bindingResult.addError(fieldError);                
		 }            
		}         
		
		if (bindingResult.hasErrors()) {            
		 model.addAttribute("store", store);            
		 model.addAttribute("errorMessage", "予約内容に不備があります。"); 
		 return "stores/show";
		}
		
		redirectAttributes.addFlashAttribute("reservationInputForm", reservationInputForm);           
		
		return "redirect:/stores/{id}/reservations/confirm";
	} 
     
     @GetMapping("/stores/{id}/reservations/confirm")
     public String confirm(@PathVariable(name = "id") Integer id,
                           @ModelAttribute ReservationInputForm reservationInputForm,
                           @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, 
                           HttpServletRequest httpServletRequest,
                           Model model) 
     {        
    	 Store store = storeRepository.getReferenceById(id);
    	 User user = userDetailsImpl.getUser(); 
                 
         // 予約日を取得する
         LocalDate reservationDate = reservationInputForm.getReservationDateForLocalDate();
  
         
         ReservationRegisterForm reservationRegisterForm = new ReservationRegisterForm(store.getId(), user.getId(), reservationDate.toString() , reservationInputForm.getNumberOfPeople());
         
         String sessionId = stripeService.createStripeSession(store.getName(), reservationRegisterForm, httpServletRequest);
         
         
         model.addAttribute("store", store);  
         model.addAttribute("reservationRegisterForm", reservationRegisterForm); 
         model.addAttribute("sessionId", sessionId);
         
         return "reservations/confirm";
     }  
     
     /*
     @PostMapping("/stores/{id}/reservations/create")
     public String create(@ModelAttribute ReservationRegisterForm reservationRegisterForm) {                
         reservationService.create(reservationRegisterForm);        
         
         return "redirect:/reservations?reserved";
     }
     */
}
