package com.payment.paystack.payments;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.paystack.payments.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaystackService {

    @Value("${spring.paystack.secret.key}")
    private String secretKey;

    private final RestTemplate restTemplate = new RestTemplate();

    private final PaymentRepository paymentRepository;



    // Initialize Transaction
    public Map<String, Object> initializeTransaction(PaymentRequest request) throws Exception {
        String url = "https://api.paystack.co/transaction/initialize";

        // Prepare headers and payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("email", request.getEmail());
        payload.put("amount", request.getAmount() * 100 );

        var headers = new org.springframework.http.HttpHeaders();
        headers.add("Authorization", "Bearer " + secretKey);
        headers.add("Content-Type", "application/json");

        var entity = new org.springframework.http.HttpEntity<>(payload, headers);

        // Call Paystack API
        var response = restTemplate.postForEntity(url, entity, Map.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> responseBody = response.getBody();

            // Extracting the reference from the response
            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            String reference = (String) data.get("reference");

            System.out.println("Reference: " + reference);

            // Save the payment information to the database
            Payment payment = new Payment();
            payment.setReference(reference);
            payment.setEmail(request.getEmail());
            payment.setAmount(request.getAmount());
            payment.setStatus("pending"); // Set status as pending initially
            paymentRepository.save(payment);  // Save the payment entity

            return responseBody;
        } else {
            throw new Exception("Error initializing transaction");
        }
    }



    // Verify Transaction
    public Map<String, Object> verifyTransaction(String reference) throws Exception {
        String url = "https://api.paystack.co/transaction/verify/" + reference;

        var headers = new org.springframework.http.HttpHeaders();
        headers.add("Authorization", "Bearer " + secretKey);

        var entity = new org.springframework.http.HttpEntity<>(headers);
        var response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, Map.class);
        System.out.println(response);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();

        } else {
            throw new Exception("Error verifying transaction");
        }
    }

    // Update Payment Status
    public void updatePayment(String reference, String status, String paid_at,String channel,String created_at,String currency,String ip_address) {
        Payment payment = paymentRepository.findByReference(reference);
        if (payment != null) {
            payment.setStatus(status);
            payment.setPaidAt(paid_at);
            payment.setCreatedAt(created_at);
            payment.setCurrency(currency);
            payment.setChannel(channel);
            payment.setIpAddress(ip_address);
            paymentRepository.save(payment);
        } else {
            System.out.println("Payment not found for reference: " + reference);
        }
    }

}
