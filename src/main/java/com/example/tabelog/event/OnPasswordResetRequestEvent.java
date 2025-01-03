package com.example.tabelog.event;

import org.springframework.context.ApplicationEvent;

import com.example.tabelog.entity.User;

import lombok.Getter;
@Getter
public class OnPasswordResetRequestEvent extends ApplicationEvent{
	
	private final User user;
	private final String requestUrl;
	
	public OnPasswordResetRequestEvent(Object source, User user, String requestUrl) {
		super(source);
		
		this.user = user;
		this.requestUrl = requestUrl;
	}
}
