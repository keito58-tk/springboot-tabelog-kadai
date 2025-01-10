package com.example.tabelog.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tabelog.entity.User;
import com.example.tabelog.entity.VerificationToken;
import com.example.tabelog.repository.VerificationTokenRepository;


@Service
public class VerificationTokenService {
	private final VerificationTokenRepository verificationTokenRepository;
    
    
    public VerificationTokenService(VerificationTokenRepository verificationTokenRepository) {        
        this.verificationTokenRepository = verificationTokenRepository;
    } 
    
    // 新しい検証トークンを作成
    @Transactional
    public void create(User user, String token) {
        VerificationToken verificationToken = new VerificationToken();
        
        verificationToken.setUser(user);
        verificationToken.setToken(token);        
        
        verificationTokenRepository.save(verificationToken);
    }    
    
    // トークンの文字列で検索した結果を返す
    public VerificationToken getVerificationToken(String token) {
        return verificationTokenRepository.findByToken(token);
    } 
}
