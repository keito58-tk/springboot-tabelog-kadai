package com.example.tabelog.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.tabelog.entity.User;
import com.example.tabelog.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
	private final UserRepository userRepository;
	
	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	
	// メールアドレスに基づいてユーザーの認証情報をロード
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// メールアドレスでユーザーを検索
		try {
			User user = userRepository.findByEmail(email);
			
			// ユーザーの役割名を取得
			String userRoleName = user.getRole().getName();
			
			// 役割をGrantedAuthorityのコレクションに追加
			Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
			authorities.add(new SimpleGrantedAuthority(userRoleName));
			
			// カスタムUserDetails実装を返す
			return new UserDetailsImpl(user, authorities);
		} catch (Exception e) {
			throw new UsernameNotFoundException("ユーザーが見つかりませんでした。");
		}
	}
}
