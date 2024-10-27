package com.example.tabelog.entity;

import java.sql.Timestamp;

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
@Table(name = "category_store")
@Data
public class CategoryStore {
	 @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "id")
     private Integer id;
 
     @ManyToOne
     @JoinColumn(name = "store_id")
     private Store store;
 
     @ManyToOne
     @JoinColumn(name = "category_id")
     private Category category;
 
     @Column(name = "created_at", insertable = false, updatable = false)
     private Timestamp createdAt;
 
     @Column(name = "updated_at", insertable = false, updatable = false)
     private Timestamp updatedAt;
}
