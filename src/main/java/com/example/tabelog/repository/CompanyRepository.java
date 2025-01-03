package com.example.tabelog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabelog.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Integer>{
	public Company findFirstByOrderByIdDesc();
}
