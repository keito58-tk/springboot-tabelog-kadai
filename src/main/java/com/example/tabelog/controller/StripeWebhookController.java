package com.example.tabelog.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.tabelog.service.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;

@Controller
public class StripeWebhookController {
	private final StripeService stripeService;
	
	// application.propertiesからStripeのAPIキーを取得
    @Value("${stripe.api-key}")
    private String stripeApiKey;

    // application.propertiesからStripeのWebhookシークレットを取得
    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    public StripeWebhookController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/stripe/webhook")
    public ResponseEntity<String> webhook(@RequestBody String payload,
    									  @RequestHeader("Stripe-Signature") String sigHeader) {
    	// StripeのAPIキーを設定
        Stripe.apiKey = stripeApiKey;
        Event event = null;

        try {
        	// Webhookシグネチャを検証し、イベントを構築
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
        	// シグネチャの検証に失敗した場合、400 Bad Requestを返す
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // イベントのタイプがcheckout.session.completedの場合に処理を実行
        if ("checkout.session.completed".equals(event.getType())) {
            stripeService.processSessionCompleted(event);
        }

        // 正常に処理が完了したことを示す200 OKを返す
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }
}
