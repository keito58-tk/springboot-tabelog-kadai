package com.example.tabelog.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tabelog.entity.ResetToken;
import com.example.tabelog.entity.User;
import com.example.tabelog.entity.VerificationToken;

public interface UserRepository extends JpaRepository<User, Integer>{
	// ユーザー名またはフリガナに指定されたキーワードが含まれるユーザーをページングして取得
	public Page<User> findByNameLikeOrFuriganaLike(String nameKeyword, String furiganaKeyword, Pageable pageable);
	// 指定されたメールアドレスに一致するユーザーを取得
	public User findByEmail(String email);
	
	// 指定されたユーザーIDに一致するユーザーを取得
	public Optional<User> findById(User userId);
	
	// 指定されたリセットトークンに関連付けられたユーザーを取得
	public Optional<User> findByResetToken(ResetToken resetToken);
	
	// 指定された検証トークンに関連付けられたユーザーを取得
	public Optional<User> findByVerificationToken(VerificationToken verificationToken);
	
}
