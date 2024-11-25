package com.payment.paystack.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Responsedto {
    private String responseCode;
    private String responseMessage;

    public static class CartResponseDto {
    }
}
