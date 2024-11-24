package com.payment.paystack.payments;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String reference;
    private double amount; // Amount in kobo
    private String status; // pending, success, failed
    private String createdAt;
    private String paidAt;
    private String channel;
    private String currency;
    private String ipAddress;
    private String metadata;
    private Long fees;
    private String feesBreakdown;
    private String log;

    // After deserialization, you can convert to LocalDateTime
    public LocalDateTime getCreatedAtAsLocalDateTime() {
        return LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME);
    }

    public LocalDateTime getPaidAtAsLocalDateTime() {
        return LocalDateTime.parse(paidAt, DateTimeFormatter.ISO_DATE_TIME);
    }
}
