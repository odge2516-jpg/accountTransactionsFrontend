package com.example.accountsystem.dto;

import com.example.accountsystem.model.Transaction;
import lombok.Data;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private Long userId;
    private String type;
    private BigDecimal amount;
    private String description;
    private String toUserEmail;
    private String fromUserEmail;
    private LocalDateTime date;
}
