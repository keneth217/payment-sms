package com.payment.paystack.twilio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Data
@Setter
@Getter
@AllArgsConstructor
public class BulkSmsRequest {
    private List<String> recipients;
    private String messageBody;
    private String toPhone;

}
