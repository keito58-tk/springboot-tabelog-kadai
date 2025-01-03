package com.example.tabelog.event;

import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.tabelog.entity.User;
import com.example.tabelog.service.ResetTokenService;
@Component
public class PasswordResetListener {
	private final ResetTokenService resetTokenService;
    private final JavaMailSender javaMailSender;
    
    public PasswordResetListener(ResetTokenService resetTokenService, JavaMailSender mailSender) {
        this.resetTokenService = resetTokenService;        
        this.javaMailSender = mailSender;
    }

    @EventListener
    private void onPasswordResetRequestEvent(OnPasswordResetRequestEvent event) {
    	// イベントからユーザー情報を取得
        User user = event.getUser();
        
        // トークンを生成し保存
        String token = resetTokenService.createToken(user);
        
        // メール送信設定
        String recipientAddress = user.getEmail();
        String subject = "パスワードリセットリクエスト";
        String confirmationUrl = event.getRequestUrl() + "/verify?token=" + token;
        String message = "以下のリンクをクリックしてパスワードをリセットしてください。";
        
        // メールメッセージを構築
        SimpleMailMessage mailMessage = new SimpleMailMessage(); 
        mailMessage.setTo(recipientAddress);
        mailMessage.setSubject(subject);
        mailMessage.setText(message + "\n" + confirmationUrl);
        
        // メール送信
        javaMailSender.send(mailMessage);
    }
}
