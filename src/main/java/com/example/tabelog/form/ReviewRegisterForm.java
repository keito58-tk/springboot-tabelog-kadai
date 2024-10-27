package com.example.tabelog.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRegisterForm {
	@NotBlank(message = "コメントしてください。")
    private String comment;
	
	@NotNull(message = "評価を入力してください。")
    @Min(value = 1 , message = "星は1以上に設定してください。")
    @Max(value = 5, message = "星は5以下に設定してください。")
    private Integer rating;

	
	
}
