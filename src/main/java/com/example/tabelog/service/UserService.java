package com.example.tabelog.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tabelog.entity.Role;
import com.example.tabelog.entity.User;
import com.example.tabelog.form.SignupForm;
import com.example.tabelog.form.UserEditForm;
import com.example.tabelog.repository.RoleRepository;
import com.example.tabelog.repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;

@Service
public class UserService {
	private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final StripeService stripeService;
    
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, StripeService stripeService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;        
        this.passwordEncoder = passwordEncoder;
		this.stripeService = stripeService;
    }    
    
    @Transactional
    public User create(SignupForm signupForm) {
        User user = new User();
        Role role = roleRepository.findByName("ROLE_GENERAL");
        
        user.setName(signupForm.getName());
        user.setFurigana(signupForm.getFurigana());
        user.setPostalCode(signupForm.getPostalCode());
        user.setAddress(signupForm.getAddress());
        user.setPhoneNumber(signupForm.getPhoneNumber());
        user.setEmail(signupForm.getEmail());
        user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
        user.setRole(role);
        user.setEnabled(false);        
        
        try {
            // 新しいStripe顧客を作成して、stripeCustomerIdを設定
            Customer customer = stripeService.createCustomer(user);
            user.setStripeCustomerId(customer.getId());
        } catch (StripeException e) {
            e.printStackTrace();
            throw new RuntimeException("Stripe顧客の作成に失敗しました");
        }
        
        return userRepository.save(user);
    }   
    
    @Transactional
    public void update(UserEditForm userEditForm) {
        User user = userRepository.getReferenceById(userEditForm.getId());
        
        user.setName(userEditForm.getName());
        user.setFurigana(userEditForm.getFurigana());
        user.setPostalCode(userEditForm.getPostalCode());
        user.setAddress(userEditForm.getAddress());
        user.setPhoneNumber(userEditForm.getPhoneNumber());
        user.setEmail(userEditForm.getEmail());      
        
        userRepository.save(user);
    } 
    
    @Transactional
    public void saveStripeCustomerId(User user, String stripeCustomerId) {
        user.setStripeCustomerId(stripeCustomerId);
        userRepository.save(user);
    }

    @Transactional
    public void updateRole(User user, String roleName) {
        Role role = roleRepository.findByName(roleName);
        user.setRole(role);
        userRepository.save(user);
    }
    
    // メールアドレスが登録済みかどうかをチェックする
    public boolean isEmailRegistered(String email) {
        User user = userRepository.findByEmail(email);  
        return user != null;
    }
    
    // パスワードとパスワード（確認用）の入力値が一致するかどうかをチェックする
    public boolean isSamePassword(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation);
    }  
    
 // ユーザーを有効にする
    @Transactional
    public void enableUser(User user) {
        user.setEnabled(true); 
        userRepository.save(user);
    } 
    
    // メールアドレスが変更されたかどうかをチェックする
    public boolean isEmailChanged(UserEditForm userEditForm) {
        User currentUser = userRepository.getReferenceById(userEditForm.getId());
        return !userEditForm.getEmail().equals(currentUser.getEmail());      
    }

	public User getLoggedInUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String email = authentication.getName();
	    return userRepository.findByEmail(email);
	}

	public User findById(User userId) {
		return userRepository.findById(userId).orElse(null);
	}

	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public void refreshAuthenticationByRole(String newRole) {
		// 現在の認証情報を取得する
        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();

        // 新しい認証情報を作成する
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();
        simpleGrantedAuthorities.add(new SimpleGrantedAuthority(newRole));
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(currentAuthentication.getPrincipal(), currentAuthentication.getCredentials(), simpleGrantedAuthorities);

        // 認証情報を更新する
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
    }

	@Transactional
    public void save(User user) {
        userRepository.save(user);
    }


}
