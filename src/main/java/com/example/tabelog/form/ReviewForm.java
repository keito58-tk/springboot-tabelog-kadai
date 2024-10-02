package com.example.tabelog.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class ReviewForm {
	@NotBlank
    private String comment;

    @Min(1)
    @Max(5)
    private int rating;

}
