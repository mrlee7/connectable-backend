package com.backend.connectable.kas.service.dto.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class TransactionOptionFeePayerRequest extends TransactionOptionRequest {
    private UserFeePayerOptionRequest userFeePayer;

    public TransactionOptionFeePayerRequest(boolean enableGlobalFeePayer, UserFeePayerOptionRequest userFeePayer) {
        super(enableGlobalFeePayer);
        this.userFeePayer = userFeePayer;
    }
}