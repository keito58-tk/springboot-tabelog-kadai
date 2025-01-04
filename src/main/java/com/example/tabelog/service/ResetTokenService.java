package com.example.tabelog.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tabelog.entity.ResetToken;
import com.example.tabelog.entity.User;
import com.example.tabelog.repository.ResetTokenRepository;

@Service
public class ResetTokenService {
	private final ResetTokenRepository resetTokenRepository;
	private static final Logger logger = LoggerFactory.getLogger(ResetTokenService.class);
	
	public ResetTokenService (ResetTokenRepository resetTokenRepository) {
		this.resetTokenRepository = resetTokenRepository;
	}
	
	// トークンの生成と保存
	@Transactional
	public String createToken(User user) {
		// 既存のトークンを削除
		logger.debug("Creating reset token for user ID: {}", user.getId());
        resetTokenRepository.deleteByUser(user);
        
		String token = UUID.randomUUID().toString();
		ResetToken resetToken = new ResetToken();
		resetToken.setUser(user);
		resetToken.setToken(token);
		resetToken.setExpiryDate(LocalDateTime.now().plusHours(24)); // 24時間有効
		
		resetTokenRepository.save(resetToken);
		logger.debug("Saved new reset token for user ID: {}", user.getId());
		
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
		logger.debug("Deleting reset token ID: {}", token.getId());
		resetTokenRepository.delete(token);
		resetTokenRepository.flush(); // 削除を即時反映
		logger.debug("Deleted reset token ID: {}", token.getId());
	}
	
	// ユーザーに紐づくリセットトークンを削除するメソッド
	@Transactional
	public void deleteByUser(User user) {
		logger.debug("Deleting reset token for user ID: {}", user.getId());
		resetTokenRepository.deleteByUser(user);
		resetTokenRepository.flush();
		logger.debug("Deleted reset token for user ID: {}", user.getId());
	}
}
