package com.example.tabelog.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tabelog.entity.Reservation;
import com.example.tabelog.entity.Store;
import com.example.tabelog.entity.User;
import com.example.tabelog.form.ReservationRegisterForm;
import com.example.tabelog.repository.ReservationRepository;


@Service
public class ReservationService {
	private final ReservationRepository reservationRepository;
	 
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    // 指定したidを持つ予約を取得する
    public Optional<Reservation> findReservationById(Integer id) {
        return reservationRepository.findById(id);
    }

    // 指定されたユーザーに紐づく予約を予約日時が新しい順（未来→過去）に並べ替え、ページングされた状態で取得する
    public Page<Reservation> findReservationsByUserOrderByReservedDatetimeDesc(User user, Pageable pageable) {
        return reservationRepository.findByUserOrderByReservedDatetimeDesc(user, pageable);
    }

    // 予約のレコード数を取得する
    public long countReservations() {
        return reservationRepository.count();
    }

    // idが最も大きい予約を取得する
    public Reservation findFirstReservationByOrderByIdDesc() {
        return reservationRepository.findFirstByOrderByIdDesc();
    }

    // 新しい予約を作成し、データベースに保存
    @Transactional
    public void createReservation(ReservationRegisterForm reservationRegisterForm, Store store, User user) {
        Reservation reservation = new Reservation();
        
        // 予約日時を設定（フォームから取得した日付と時間を結合）
        LocalDateTime reservedDatetime = LocalDateTime.of(reservationRegisterForm.getReservationDate(), reservationRegisterForm.getReservationTime());

        reservation.setReservedDatetime(reservedDatetime);
        reservation.setNumberOfPeople(reservationRegisterForm.getNumberOfPeople());
        reservation.setStore(store);
        reservation.setUser(user);

        // 新しい予約をデータベースに保存
        reservationRepository.save(reservation);
    }

    // 指定された予約をデータベースから削除
    @Transactional
    public void deleteReservation(Reservation reservation) {
        reservationRepository.delete(reservation);
    }

    // 予約日時が現在よりも2時間以上後であればtrueを返す
    public boolean isAtLeastTwoHoursInFuture(LocalDateTime reservationDateTime) {
        return Duration.between(LocalDateTime.now(), reservationDateTime).toHours() >= 2;
    }
    
}
