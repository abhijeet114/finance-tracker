package com.finance.tracker.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain entity for Transaction
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity {
    
    @Builder.Default
    private UUID id = UUID.randomUUID();
    private Double amount;
    private TransactionType type;
    private String category;
    private String description;
    @Builder.Default
    private LocalDateTime dateTime = LocalDateTime.now();
}
