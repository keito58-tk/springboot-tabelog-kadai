package com.example.tabelog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabelog.entity.Term;

public interface TermRepository extends JpaRepository<Term, Integer>{
	public Term findFirstByOrderByIdDesc();
}
