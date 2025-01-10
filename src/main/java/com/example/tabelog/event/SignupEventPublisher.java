package com.example.tabelog.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.tabelog.entity.User;

@Component
public class SignupEventPublisher {
	private final ApplicationEventPublisher applicationEventPublisher;
    
    public SignupEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;                
    }
    
    public void publishSignupEvent(User user, String requestUrl) {
    	// 新しいSignupEventを作成し、イベントを発行
        applicationEventPublisher.publishEvent(new SignupEvent(this, user, requestUrl));
    }
}
