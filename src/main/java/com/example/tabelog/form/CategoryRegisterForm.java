package com.example.tabelog.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRegisterForm {
	@NotBlank
	private String name;
}
