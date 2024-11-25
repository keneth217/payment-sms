package com.payment.paystack.payments;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.paystack.payments.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaystackService {

    private static final Logger logger = LoggerFactory.getLogger(PaystackService.class);

    @Value("${spring.paystack.secret.key}")
    private String secretKey;

    private final RestTemplate restTemplate=new RestTemplate();
    private final PaymentRepository paymentRepository;

    // Initialize Transaction
    public Map<String, Object> initializeTransaction(PaymentRequest request) throws Exception {
        String url = "https://api.paystack.co/transaction/initialize";

        // Prepare headers and payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", request.getEmail());
        payload.put("amount", request.getAmount() * 100); // Paystack expects the amount in kobo (1 = 100 kobo)


        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + secretKey);
        headers.add("Content-Type", "application/json");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        // Call Paystack API
        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null && responseBody.containsKey("data")) {
                    Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                    String reference = (String) data.get("reference");

                    logger.info("Transaction initialized successfully with reference: {}", reference);

                    // Save the payment information to the database
                    Payment payment = new Payment();
                    payment.setReference(reference);
                    payment.setEmail(request.getEmail());
                    payment.setAmount(request.getAmount());
                    payment.setStatus("pending"); // Set status as pending initially
                    paymentRepository.save(payment);  // Save the payment entity

                    return responseBody;
                } else {
                    logger.error("Error: Response data missing from Paystack initialization.");
                    throw new Exception("Error: Response data missing from Paystack initialization.");
                }
            } else {
                logger.error("Error initializing transaction: Paystack API responded with error.");
                throw new Exception("Error initializing transaction");
            }
        } catch (Exception e) {
            logger.error("Error initializing Paystack transaction: {}", e.getMessage(), e);
            throw new Exception("Error initializing Paystack transaction", e);
        }
    }

    // Verify Transaction
    public Map<String, Object> verifyTransaction(String reference) throws Exception {
        String url = "https://api.paystack.co/transaction/verify/" + reference;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + secretKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null) {
                    logger.info("Transaction verification successful for reference: {}", reference);
                    return responseBody;
                } else {
                    logger.error("Error: Empty response body from Paystack on verification.");
                    throw new Exception("Error: Empty response body from Paystack on verification.");
                }
            } else {
                logger.error("Error verifying transaction: Paystack API responded with error.");
                throw new Exception("Error verifying transaction");
            }
        } catch (Exception e) {
            logger.error("Error verifying Paystack transaction for reference {}: {}", reference, e.getMessage(), e);
            throw new Exception("Error verifying Paystack transaction", e);
        }
    }

    // Update Payment Status
    public void updatePayment(String reference, String status, String paidAt, String channel, String createdAt, String currency, String ipAddress) {
        try {
            Payment payment = paymentRepository.findByReference(reference);
            if (payment != null) {
                payment.setStatus(status);
                payment.setPaidAt(paidAt);
                payment.setCreatedAt(createdAt);
                payment.setCurrency(currency);
                payment.setChannel(channel);
                payment.setIpAddress(ipAddress);
                paymentRepository.save(payment);
                logger.info("Payment status updated successfully for reference: {}", reference);
            } else {
                logger.warn("Payment not found for reference: {}", reference);
            }
        } catch (Exception e) {
            logger.error("Error updating payment status for reference {}: {}", reference, e.getMessage(), e);
        }
    }
}
