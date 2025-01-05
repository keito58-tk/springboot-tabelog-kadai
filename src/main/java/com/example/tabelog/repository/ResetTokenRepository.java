package com.example.tabelog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.tabelog.entity.ResetToken;
import com.example.tabelog.entity.User;


public interface ResetTokenRepository extends JpaRepository<ResetToken, Integer>{
	public Optional<ResetToken> findByToken(String token);
	
	// ユーザーでリセットトークンを削除するカスタムクエリ
    @Modifying
    @Query("DELETE FROM ResetToken rt WHERE rt.user = :user")
    void deleteByUser(@Param("user") User user);

    // ユーザーでリセットトークンを検索
    Optional<ResetToken> findByUser(User user);
}
