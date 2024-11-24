package com.payment.paystack.payments;

import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PaymentRequest {
    private String email;
   private  double amount;
}
