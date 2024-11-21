package com.example.tabelog.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.tabelog.entity.User;
import com.example.tabelog.form.ReservationRegisterForm;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.PaymentMethod;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.SubscriptionCreateParams;
import com.stripe.param.SubscriptionListParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionRetrieveParams;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class StripeService {
	
	@Value("${stripe.api-key}")
    private String stripeApiKey;
	
	private final ReservationService reservationService;
    
    public StripeService(ReservationService reservationService) {
        this.reservationService = reservationService;
        Stripe.apiKey = stripeApiKey;
    }
	
	 // セッションを作成し、Stripeに必要な情報を返す
    public String createStripeSession(String storeName, ReservationRegisterForm reservationRegisterForm, HttpServletRequest httpServletRequest) {
        String requestUrl = new String(httpServletRequest.getRequestURL());
        SessionCreateParams params =
            SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addLineItem(
                    SessionCreateParams.LineItem.builder()
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()   
                                .setProductData(
                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(storeName)
                                        .build())
                                .setCurrency("jpy")  
                                .setUnitAmount(2000L)
                                .build())
                        .setQuantity(1L)
                        .build())
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(requestUrl.replaceAll("/stores/[0-9]+/reservations/confirm", "") + "/reservations?reserved")
                .setCancelUrl(requestUrl.replace("/reservations/confirm", ""))
                .setPaymentIntentData(
                    SessionCreateParams.PaymentIntentData.builder()
                        .putMetadata("storeId", reservationRegisterForm.getStoreId().toString())
                        .putMetadata("userId", reservationRegisterForm.getUserId().toString())
                        .putMetadata("reservationDate", reservationRegisterForm.getReservationDate())
                        .putMetadata("numberOfPeople", reservationRegisterForm.getNumberOfPeople().toString())
                        .build())
                .build();
        try {
            Session session = Session.create(params);
            return session.getId();
        } catch (StripeException e) {
            e.printStackTrace();
            return "";
        }
    } 
    
 // セッションから予約情報を取得し、ReservationServiceクラスを介してデータベースに登録する  
    public void processSessionCompleted(Event event) {
        Optional<StripeObject> optionalStripeObject = event.getDataObjectDeserializer().getObject();
        optionalStripeObject.ifPresentOrElse(stripeObject -> {
            Session session = (Session)stripeObject;
            SessionRetrieveParams params = SessionRetrieveParams.builder().addExpand("payment_intent").build();

            try {
                session = Session.retrieve(session.getId(), params, null);
                Map<String, String> paymentIntentObject = session.getPaymentIntentObject().getMetadata();
                reservationService.create(paymentIntentObject);
            } catch (StripeException e) {
                e.printStackTrace();
            }
            System.out.println("予約一覧ページの登録処理が成功しました。");
            System.out.println("Stripe API Version: " + event.getApiVersion());
            System.out.println("stripe-java Version: " + Stripe.VERSION);
        },
        () -> {
            System.out.println("予約一覧ページの登録処理が失敗しました。");
            System.out.println("Stripe API Version: " + event.getApiVersion());
            System.out.println("stripe-java Version: " + Stripe.VERSION);
        });
    }
    
 // 依存性の注入後に一度だけ実行するメソッド
    @PostConstruct
    private void init() {
        // Stripeのシークレットキーを設定する
        Stripe.apiKey = stripeApiKey;
        System.out.println("使用するStripe APIキー: " + Stripe.apiKey);
    }
    
 // 顧客（StripeのCustomerオブジェクト）を作成する
    public Customer createCustomer(User user) throws StripeException {
        // 顧客の作成時に渡すユーザーの情報
        CustomerCreateParams customerCreateParams =
            CustomerCreateParams.builder()
                .setName(user.getName())
                .setEmail(user.getEmail())
                .build();

        return Customer.create(customerCreateParams);
    }

    // 支払い方法（StripeのPaymentMethodオブジェクト）を顧客（StripeのCustomerオブジェクト）に紐づける
    public void attachPaymentMethodToCustomer(String paymentMethodId, String customerId) throws StripeException {
    	// 支払い方法を紐づける顧客
        PaymentMethodAttachParams paymentMethodAttachParams =
            PaymentMethodAttachParams.builder()
            .setCustomer(customerId)
            .build();

        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
        paymentMethod.attach(paymentMethodAttachParams);
    }

    // 顧客（StripeのCustomerオブジェクト）のデフォルトの支払い方法（StripeのPaymentMethodオブジェクト）を設定する
    public void setDefaultPaymentMethod(String paymentMethodId, String customerId) throws StripeException {
        // 顧客の更新時に渡すデフォルトの支払い方法のID
        CustomerUpdateParams customerUpdateParams =
            CustomerUpdateParams.builder()
                .setInvoiceSettings(
                    CustomerUpdateParams.InvoiceSettings.builder()
                        .setDefaultPaymentMethod(paymentMethodId)
                        .build()
                )
                .build();

        Customer customer = Customer.retrieve(customerId);
        customer.update(customerUpdateParams);
    }

    // サブスクリプション（StripeのSubscriptionオブジェクト）を作成する
    public Subscription createSubscription(String customerId, String priceId) throws StripeException {
        // サブスクリプションの作成時に渡す顧客IDや価格ID
        SubscriptionCreateParams subscriptionCreateParams =
            SubscriptionCreateParams.builder()
                .setCustomer(customerId)
                .addItem(
                    SubscriptionCreateParams
                      .Item.builder()
                      .setPrice(priceId)
                      .build()
                )
                .build();

        return Subscription.create(subscriptionCreateParams);
    }

    // 顧客（StripeのCustomerオブジェクト）のデフォルトの支払い方法（StripeのPaymentMethodオブジェクト）を取得する
    public PaymentMethod getDefaultPaymentMethod(String customerId) throws StripeException {
    	// Stripeのシークレットキーを設定する
        Stripe.apiKey = stripeApiKey;
        Customer customer = Customer.retrieve(customerId);
        String defaultPaymentMethodId = customer.getInvoiceSettings().getDefaultPaymentMethod();
        return PaymentMethod.retrieve(defaultPaymentMethodId);
    }

    // 顧客（StripeのCustomerオブジェクト）のデフォルトの支払い方法（StripeのPaymentMethodオブジェクト）のIDを取得する
    public String getDefaultPaymentMethodId(String customerId) throws StripeException {
    	// Stripeのシークレットキーを設定する
        Stripe.apiKey = stripeApiKey;
        Customer customer = Customer.retrieve(customerId);
        return customer.getInvoiceSettings().getDefaultPaymentMethod();
    }

    // 支払い方法（StripeのPaymentMethodオブジェクト）と顧客（StripeのCustomerオブジェクト）の紐づけを解除する
    public void detachPaymentMethodFromCustomer(String paymentMethodId) throws StripeException {
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
        paymentMethod.detach();
    }

    // サブスクリプション（StripeのSubscriptionオブジェクト）を取得する
    public List<Subscription> getSubscriptions(String customerId) throws StripeException {
        // 契約中のサブスクリプションの取得時に渡す顧客ID
        SubscriptionListParams subscriptionListParams =
            SubscriptionListParams.builder()
                .setCustomer(customerId)
                .build();

        return Subscription.list(subscriptionListParams).getData();
    }

    // サブスクリプション（StripeのSubscriptionオブジェクト）をキャンセルする
    public void cancelSubscriptions(List<Subscription> subscriptions) throws StripeException {
        for (Subscription subscription : subscriptions) {
            subscription.cancel();
        }
    }
}
