package com.example.tabelog.service;

import org.springframework.stereotype.Service;

import com.example.tabelog.entity.Company;
import com.example.tabelog.repository.CompanyRepository;

@Service
public class CompanyService {
	private final CompanyRepository companyRepository;
	
	public CompanyService(CompanyRepository companyRepository) {
		this.companyRepository = companyRepository;
	}
	
	public Company findFirstCompanyByOrderByIdDesc() {
		return companyRepository.findFirstByOrderByIdDesc();
	}
}
