package com.payment.paystack.payments;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.util.Map;


@RestController
@RequestMapping("/api/paystack")
@RequiredArgsConstructor
public class PaystackController {

    private final PaystackService paystackService;

    @Value("${spring.paystack.secret.key}")
    private String secretKey;

    // Initialize Transaction
    @PostMapping("/initialize")
    public ResponseEntity<?> initializeTransaction(@RequestBody PaymentRequest request) {
        try {

            Map<String, Object> response = paystackService.initializeTransaction(request);
            System.out.println(response);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestHeader("X-Paystack-Signature") String signature,
            @RequestBody String payload) {

        try {
            // Validate Paystack Signature
            String computedHash = hmacSHA512(payload, secretKey);
            if (!computedHash.equals(signature)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
            }

            // Parse payload and process
            Map<String, Object> webhookData = new ObjectMapper().readValue(payload, Map.class);
            Map<String, Object> data = (Map<String, Object>) webhookData.get("data");
            System.out.println(data);
            String reference = (String) data.get("reference");
            String status = (String) data.get("status");
            String paid_at = (String) data.get("paid_at");
            String channel = (String) data.get("channel");
            String created_at = (String) data.get("created_at");
            String currency = (String) data.get("currency");
            String ip_address = (String) data.get("ip_address");

            paystackService.updatePayment(reference, status,paid_at,channel,created_at,currency,ip_address);

            return ResponseEntity.ok("Webhook processed successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process webhook");
        }
    }

    // HMAC Hashing Function
    private String hmacSHA512(String payload, String secret) throws Exception {
        Mac sha512Hmac = Mac.getInstance("HmacSHA512");
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA512");
        sha512Hmac.init(keySpec);
        byte[] hashBytes = sha512Hmac.doFinal(payload.getBytes());
        return Hex.encodeHexString(hashBytes);
    }

}
