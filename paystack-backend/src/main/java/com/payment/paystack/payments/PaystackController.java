package com.payment.paystack.payments;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/paystack")
@RequiredArgsConstructor
public class PaystackController {

    private static final Logger logger = LoggerFactory.getLogger(PaystackController.class);

    private final PaystackService paystackService;
    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;

    @Value("${spring.paystack.secret.key}")
    private String secretKey;

    // Initialize Transaction
    @PostMapping("/initialize")
    public ResponseEntity<?> initializeTransaction(@RequestBody PaymentRequest request) {
        try {
            Map<String, Object> response = paystackService.initializeTransaction(request);
            logger.info("Transaction initialized successfully: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error initializing transaction: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
//api/paystack/webhook
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

    // Verify transaction status with Paystack
    @GetMapping("/verify-transaction/{reference}")
    public ResponseEntity<?> verifyTransaction(@PathVariable String reference) {
        System.out.println(reference);
        try {
            // Step 1: Verify the transaction with Paystack
            Map<String, Object> response = paystackService.verifyTransaction(reference);

            // Log the successful transaction verification
            logger.info("Transaction verified successfully: {}", response);

            // Step 2: Check transaction status from the database
            String transactionStatus = paymentRepository.findStatusByReference(reference);
            System.out.println("database......................"+transactionStatus);
            // If no status is found in the database, return an error
            if (transactionStatus == null) {
                logger.error("No transaction found for reference {} in the database.", reference);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found in the database");
            }

            // Extract the 'data' object from the Paystack response
            Map<String, Object> data = (Map<String, Object>) response.get("data");

            // Step 3: Access 'status' from the 'data' object in the Paystack response
            String paystackStatus = (String) data.get("status");
            System.out.println("paystsck......................"+paystackStatus);
            // Step 4: Compare the Paystack status with the database status
            if ("success".equals(paystackStatus) && "success".equals(transactionStatus)) {
                return ResponseEntity.ok(response);
            } else {
                logger.error("Transaction status mismatch for reference {}: Paystack status = {}, Database status = {}",
                        reference, paystackStatus, transactionStatus);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Transaction status mismatch");
            }

        } catch (HttpClientErrorException e) {
            logger.error("Client error verifying transaction for reference {}: {}", reference, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error verifying transaction");
        } catch (HttpServerErrorException e) {
            logger.error("Server error verifying transaction for reference {}: {}", reference, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error verifying transaction");
        } catch (Exception e) {
            logger.error("Unexpected error verifying transaction for reference {}: {}", reference, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error verifying transaction");
        }
    }


}
