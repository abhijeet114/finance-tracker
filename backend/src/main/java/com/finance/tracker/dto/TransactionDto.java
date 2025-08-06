package com.finance.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for Transaction
 * Acts as an intermediary between OpenAPI model and Entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    
    private UUID id;
    private Double amount;
    private TransactionTypeDto type;
    private String category;
    private String description;
    private LocalDateTime dateTime;
    
    /**
     * DTO enum for Transaction Type
     */
    public enum TransactionTypeDto {
        INCOME,
        EXPENSE
    }
}
