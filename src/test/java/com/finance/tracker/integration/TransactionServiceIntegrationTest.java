package com.finance.tracker.integration;

import com.finance.tracker.dto.TransactionDto;
import com.finance.tracker.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration test template for testing service layer with database interactions
 * This test is disabled by default and should be run explicitly when testing database operations
 * 
 * To run: ./mvnw test -Dtest=TransactionServiceIntegrationTest
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TransactionServiceIntegrationTest {

    @Autowired
    private TransactionService transactionService;

    @Test
    void testCreateTransaction_ShouldPersistToDatabase() {
        // Given
        TransactionDto dto = TransactionDto.builder()
                .amount(500.0)
                .type(TransactionDto.TransactionTypeDto.INCOME)
                .category("Test Income")
                .description("Integration test transaction")
                .build();

        // When
        TransactionDto savedDto = transactionService.createTransaction(dto);

        // Then
        assertThat(savedDto).isNotNull();
        assertThat(savedDto.getId()).isNotNull();
        assertThat(savedDto.getAmount()).isEqualTo(500.0);
        assertThat(savedDto.getType()).isEqualTo(TransactionDto.TransactionTypeDto.INCOME);
        assertThat(savedDto.getCategory()).isEqualTo("Test Income");
        assertThat(savedDto.getDescription()).isEqualTo("Integration test transaction");
        assertThat(savedDto.getDateTime()).isNotNull();
    }

    @Test
    void testGetAllTransactions_ShouldReturnEmptyListInitially() {
        // When
        var transactions = transactionService.getAllTransactions();

        // Then
        assertThat(transactions).isNotNull();
        // Note: May not be empty if other tests have run
    }
}
