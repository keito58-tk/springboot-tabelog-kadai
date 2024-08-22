package com.example.tabelog.service;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tabelog.entity.Reservation;
import com.example.tabelog.entity.Store;
import com.example.tabelog.entity.User;
import com.example.tabelog.repository.ReservationRepository;
import com.example.tabelog.repository.StoreRepository;
import com.example.tabelog.repository.UserRepository;


@Service
public class ReservationService {
	 private final ReservationRepository reservationRepository;  
     private final StoreRepository storeRepository;  
     private final UserRepository userRepository;  
     
     public ReservationService(ReservationRepository reservationRepository, StoreRepository storeRepository, UserRepository userRepository) {
         this.reservationRepository = reservationRepository;  
         this.storeRepository = storeRepository;  
         this.userRepository = userRepository;  
     }    
     
     @Transactional
     public void create(Map<String, String> paymentIntentObject ) { 
         Reservation reservation = new Reservation();
         
         Integer storeId = Integer.valueOf(paymentIntentObject.get("storeId"));
         Integer userId = Integer.valueOf(paymentIntentObject.get("userId"));
         
         Store store = storeRepository.getReferenceById(storeId);
         User user = userRepository.getReferenceById(userId);
         LocalDate reservationDate = LocalDate.parse(paymentIntentObject.get("reservationDate"));
         Integer numberOfPeople = Integer.valueOf(paymentIntentObject.get("numberOfPeople"));
                 
         reservation.setStore(store);
         reservation.setUser(user);
         reservation.setReservationDate(reservationDate);
         reservation.setNumberOfPeople(numberOfPeople);
         
         reservationRepository.save(reservation);
     }
	
	// 予約人数が座席数以下かどうかをチェックする
	 public boolean isWithinCapacity(Integer numberOfPeople, Integer capacity) {
         return numberOfPeople <= capacity;
     }
    
}
