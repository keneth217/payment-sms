package com.payment.paystack.twilio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {

    @Value("${spring.twilio.account-sid}")
    private String accountSid;

    @Value("${spring.twilio.auth-token}")
    private String authToken;

    @Value("${spring.twilio.phone-number}")
    private String phoneNumber;

    public String getAccountSid() {
        return accountSid;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
