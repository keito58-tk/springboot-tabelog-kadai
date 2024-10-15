package com.example.tabelog.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "reviews")
@Data
public class Review {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
	
	@ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;
	
	@Column(name = "comment")
	private String comment;
	
	@Column(name = "rating")
	private int rating;
	
	@Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
	
	
}
