package com.example.tabelog.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabelog.entity.ResetToken;
import com.example.tabelog.entity.User;
import com.example.tabelog.entity.VerificationToken;

public interface UserRepository extends JpaRepository<User, Integer>{
	public Page<User> findByNameLikeOrFuriganaLike(String nameKeyword, String furiganaKeyword, Pageable pageable);
	public User findByEmail(String email);
	public Optional<User> findById(User userId);
	public Optional<User> findByResetToken(ResetToken resetToken);
	public Optional<User> findByVerificationToken(VerificationToken verificationToken);
}
