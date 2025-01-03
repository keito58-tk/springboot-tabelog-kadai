package com.example.tabelog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabelog.entity.Reservation;
import com.example.tabelog.entity.User;

public interface ReservationRepository  extends JpaRepository<Reservation, Integer>{
	 public Page<Reservation> findByUserOrderByReservedDatetimeDesc(User user, Pageable pageable);
     public Reservation findFirstByOrderByIdDesc();
}
 