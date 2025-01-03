package com.example.tabelog.service;

import org.springframework.stereotype.Service;

import com.example.tabelog.entity.Term;
import com.example.tabelog.repository.TermRepository;

@Service
public class TermService {
	private final TermRepository termRepository;
	
	public TermService(TermRepository termRepository) {
		this.termRepository = termRepository;
	}
	
	public Term findFirstTermByOrderByIdDesc() {
		return termRepository.findFirstByOrderByIdDesc();
	}
}
