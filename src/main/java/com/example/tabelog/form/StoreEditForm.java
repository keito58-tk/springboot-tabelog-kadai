package com.example.tabelog.form;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreEditForm {
	

	@NotNull
	private Integer id;
	
	@NotBlank(message = "店舗名を入力してください。")
	private String name;
	
	private MultipartFile imageFile;
	
	@NotBlank(message = "説明を入力してください。")
	private String description;
	
	@NotNull(message = "最低価格を入力してください。")
	@Min(value = 1000, message = "価格帯は1000円以上に設定してください。")
	private Integer priceMin;
	
	@NotNull(message = "最高価格を入力してください。")
	private Integer priceMax;
	
	@NotNull(message = "座席数を入力してください。")
	private Integer capacity;
	
	@NotNull(message = "営業時間を入力してください。")
	private String openingTime;
	
	@NotNull(message = "営業時間を入力してください。")
	private String closingTime;
	
	@NotBlank(message = "定休日を入力してください。")
	private String regularHoliday;
	
	@NotBlank(message = "郵便番号を入力してください。")
	private String postalCode;
	
	@NotBlank(message = "住所を入力してください。")
	private String address;
	
	@NotBlank(message = "電話番号を入力してください。")
	private String phoneNumber;
}
