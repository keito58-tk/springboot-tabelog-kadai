package com.example.tabelog.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tabelog.entity.ResetToken;
import com.example.tabelog.entity.User;
import com.example.tabelog.repository.ResetTokenRepository;

@Service
public class ResetTokenService {
	private final ResetTokenRepository resetTokenRepository;
	
	public ResetTokenService (ResetTokenRepository resetTokenRepository) {
		this.resetTokenRepository = resetTokenRepository;
	}
	
	// トークンの生成と保存
	@Transactional
	public String createToken(User user) {
		// 既存のトークンを削除
        resetTokenRepository.deleteByUser(user);
        
		String token = UUID.randomUUID().toString();
		ResetToken resetToken = new ResetToken();
		resetToken.setUser(user);
		resetToken.setToken(token);
		resetToken.setExpiryDate(LocalDateTime.now().plusHours(24)); // 24時間有効
		
		resetTokenRepository.save(resetToken);
		
		return token;
	}
	
	// 	トークンの取得
	@Transactional
	public Optional<ResetToken> findByToken(String token) {
		return resetTokenRepository.findByToken(token);
	}
	
	// トークンの有効期限の確認
	@Transactional
	public boolean isTokenExpired(ResetToken token) {
		return token.getExpiryDate().isBefore(LocalDateTime.now());
	}
	
	//トークンの削除
	@Transactional
	public void deleteToken(ResetToken token) {
		resetTokenRepository.delete(token);
		resetTokenRepository.flush(); // 削除を即時反映
	}
	
	// ユーザーに紐づくリセットトークンを削除するメソッド
	@Transactional
	public void deleteByUser(User user) {
		resetTokenRepository.deleteByUser(user);
		resetTokenRepository.flush();
	}
}
