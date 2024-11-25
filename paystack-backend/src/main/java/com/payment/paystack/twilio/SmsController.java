package com.payment.paystack.twilio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sms")
public class SmsController {

    private final TwilioSmsService smsService;

    @Autowired
    public SmsController(TwilioSmsService smsService) {
        this.smsService = smsService;
    }

    @PostMapping("/send")
    public String sendSms(@RequestBody BulkSmsRequest request) {
        return smsService.sendSms(request);
    }
    /**
     * Endpoint to send bulk SMS
     *
     * @param request Bulk SMS request containing recipients and message body
     * @return ResponseEntity with the status of the operation
     */
    @PostMapping("/send-bulk")
    public ResponseEntity<String> sendBulkSms(@RequestBody BulkSmsRequest request) {
        if (request.getRecipients() == null || request.getRecipients().isEmpty()) {
            return ResponseEntity.badRequest().body("Recipient list is empty");
        }

        String response = smsService.sendBulkSms(request.getRecipients(), request.getMessageBody());
        return ResponseEntity.ok(response);
    }
}
