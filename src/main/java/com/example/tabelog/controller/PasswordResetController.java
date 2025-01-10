package com.example.tabelog.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.tabelog.entity.User;
import com.example.tabelog.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/user")
public class PasswordResetController {
	private final UserService userService;
	
	public PasswordResetController(UserService userService) {
		this.userService = userService;
	}
	
	// パスワードリセットリクエスト画面
    @GetMapping("/resetpassword")
    public String showForgotPasswordForm() {
        return "user/resetpassword";
    }
    
    //　パスワードリセットリクエストの処理
    @PostMapping("/resetpassword")
    public String processForgotPasswordForm(@RequestParam(name = "email") String email,
    										HttpServletRequest request,
    										Model model) {
    	User user = userService.getUserByEmail(email);
    	
    	if (user != null) {
    		// パスワードリセット用のURLを生成
    		String requestUrl = ServletUriComponentsBuilder.fromRequestUri(request)
    				.replacePath("/user/newpassword")
    				.build()
    				.toUriString();
    		
    		// パスワードリセットのリクエストをユーザーに送信
    		userService.requestPasswordReset(user, requestUrl);
    		
    		model.addAttribute("message", "パスワードリセット用のリンクをメールに送信しました。");
		} else {
			model.addAttribute("error", "入力されたメールアドレスは存在しません。");
		}
    	
    	return "user/resetpassword";
    }
    
    // パスワードリセット画面
    @GetMapping("/newpassword/verify")
    public String showResetPasswordForm(@RequestParam(name = "token") String token, Model model) {
    	// トークンに対応するユーザーを取得
    	Optional<User> user = userService.getUserByResetToken(token);
    	
    	if (user.isPresent()) {
    		// トークンが有効な場合、トークンをモデルに追加してパスワードリセットフォームを表示
			model.addAttribute("token", token);
			
			return "user/newpassword";
		} else {
			// トークンが無効な場合、エラーメッセージをモデルに追加してリセットフォームに戻る
			model.addAttribute("error", "無効なトークンです。");
			
			return "user/resetpassword";
		}
    }

    // パスワードリセットの処理
    @PostMapping("/newpassword")
    public String processResetPassword(@RequestParam(name = "token") String token,
    								   @RequestParam(name = "password") String password,
    								   Model model
    		) {
    	// パスワードリセットの実行
    	boolean isReset = userService.resetPassword(token, password);
    	if (isReset) {
    		// パスワードリセットが成功した場合、成功メッセージをモデルに追加してログイン画面にリダイレクト
    		model.addAttribute("message", "パスワードが正常にリセットされました。");
    		return "login";
		} else {
			// パスワードリセットが失敗した場合、エラーメッセージをモデルに追加してリセットフォームに戻る
			model.addAttribute("message", "無効なトークンです。");
			return "user/resetpassword";
		}
    	
    }

}
