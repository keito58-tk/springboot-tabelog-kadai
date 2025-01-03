package com.example.tabelog.entity;

import java.sql.Timestamp;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
@Entity
@Table(name = "companys")
@Data
public class Company {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "postalCode")
    private String postalCode;

    @Column(name = "address")
    private String address;

    @Column(name = "representative")
    private String representative;

    @Column(name = "establishmentDate")
    private LocalDate establishmentDate;

    @Column(name = "capital")
    private String capital;

    @Column(name = "business")
    private String business;

    @Column(name = "numberOfEmployees")
    private Integer numberOfEmployees;
    
    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Timestamp updatedAt;
}
