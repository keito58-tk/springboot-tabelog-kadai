package com.example.tabelog.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.tabelog.entity.User;
@Component
public class PasswordResetPublisher {
	private final ApplicationEventPublisher applicationEventPublisher;
    
    public PasswordResetPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;                
    }
    
    public void publishPasswordResetEvent(User user, String requestUrl) {
    	// 新しいOnPasswordResetRequestEventを作成し、イベントを発行
        applicationEventPublisher.publishEvent(new OnPasswordResetRequestEvent(this, user, requestUrl));
    }
}
