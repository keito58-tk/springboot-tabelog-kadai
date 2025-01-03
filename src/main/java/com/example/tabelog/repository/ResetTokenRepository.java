package com.example.tabelog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabelog.entity.ResetToken;
import com.example.tabelog.entity.User;


public interface ResetTokenRepository extends JpaRepository<ResetToken, Integer>{
	public Optional<ResetToken> findByToken(String token);
	void deleteByUser(User user);
}
