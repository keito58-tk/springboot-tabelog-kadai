package com.example.tabelog.event;

import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.tabelog.entity.User;
import com.example.tabelog.service.VerificationTokenService;

@Component
public class SignupEventListener {
	private final VerificationTokenService verificationTokenService;    
    private final JavaMailSender javaMailSender;
    
    public SignupEventListener(VerificationTokenService verificationTokenService, JavaMailSender mailSender) {
        this.verificationTokenService = verificationTokenService;        
        this.javaMailSender = mailSender;
    }

    @EventListener
    private void onSignupEvent(SignupEvent signupEvent) {
   //   SignupEventクラスから通知を受けたときに実行される処理
        User user = signupEvent.getUser();
        String token = UUID.randomUUID().toString();
        verificationTokenService.create(user, token);
        
        String senderAddress = "keito1904@outlook.jp";
        String recipientAddress = user.getEmail();
        String subject = "メール認証";
        String confirmationUrl = signupEvent.getRequestUrl() + "/verify?token=" + token;
        String message = "以下のリンクをクリックして会員登録を完了してください。";
        
        // メールメッセージを構築
        SimpleMailMessage mailMessage = new SimpleMailMessage(); 
        mailMessage.setTo(recipientAddress);
        mailMessage.setSubject(subject);
        mailMessage.setText(message + "\n" + confirmationUrl);
        mailMessage.setFrom(senderAddress);
        
        // メール送信
        javaMailSender.send(mailMessage);
    }
}
