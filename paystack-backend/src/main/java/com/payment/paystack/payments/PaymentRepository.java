package com.payment.paystack.payments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    Payment findByReference(String reference);


    @Query("SELECT t.status FROM Payment t WHERE t.reference = :reference")
    String findStatusByReference(@Param("reference") String reference);
}
