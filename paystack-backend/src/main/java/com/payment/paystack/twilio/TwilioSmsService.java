package com.payment.paystack.twilio;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class TwilioSmsService {

    private final TwilioConfig twilioConfig;

    @Autowired
    public TwilioSmsService(TwilioConfig twilioConfig) {
        this.twilioConfig = twilioConfig;
        Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
    }

    public String sendSms( BulkSmsRequest request) {
        try {
            Message message = Message.creator(
                    new PhoneNumber(request.getToPhone()),          // Recipient phone number
                    new PhoneNumber(twilioConfig.getPhoneNumber()), // Twilio phone number
                    request.getMessageBody()                   // Message body
            ).create();

            return "Message sent successfully with SID: " + message.getSid();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to send SMS: " + e.getMessage();
        }
    }
    // Method for sending bulk SMS
    public String sendBulkSms(List<String> recipients, String messageBody) {
        StringBuilder responseBuilder = new StringBuilder();
        for (String to : recipients) {
            try {
                Message message = Message.creator(
                        new PhoneNumber(to),         // Recipient phone number
                        new PhoneNumber(twilioConfig.getPhoneNumber()), // Twilio phone number
                        messageBody                  // Message body
                ).create();

                responseBuilder.append("Message sent to ").append(to)
                        .append(" with SID: ").append(message.getSid()).append("\n");
            } catch (Exception e) {
                responseBuilder.append("Failed to send SMS to ").append(to)
                        .append(": ").append(e.getMessage()).append("\n");
            }
        }
        return responseBuilder.toString();
    }
}
