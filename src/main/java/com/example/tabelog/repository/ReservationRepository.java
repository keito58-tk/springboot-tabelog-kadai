package com.example.tabelog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabelog.entity.Reservation;
import com.example.tabelog.entity.User;

public interface ReservationRepository  extends JpaRepository<Reservation, Integer>{
	 // 指定されたユーザーの予約を、予約日時の降順でページングして取得
	 public Page<Reservation> findByUserOrderByReservedDatetimeDesc(User user, Pageable pageable);
	 
	 // 最新のReservationエンティティを取得
     public Reservation findFirstByOrderByIdDesc();
}
 