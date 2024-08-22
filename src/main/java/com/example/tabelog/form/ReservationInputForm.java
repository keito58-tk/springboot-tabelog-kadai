package com.example.tabelog.form;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationInputForm {
	@NotBlank(message = "予約日を選択してください。")
	private String reservationDate;

	@NotNull(message = "予約人数を入力してください。")
	@Min(value = 1, message = "予約人数は1人以上に設定してください。")
	private Integer numberOfPeople;

	// 予約日を取得する
	public LocalDate getReservationDateForLocalDate() {
		if (reservationDate == null || reservationDate.isEmpty()) {
	        throw new IllegalArgumentException("Reservation date cannot be null or empty");
	    }
		return LocalDate.parse(reservationDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
	}
}
