package com.finance.tracker.integration;

import com.finance.tracker.dto.TransactionDto;
import com.finance.tracker.entity.UserEntity;
import com.finance.tracker.entity.Role;
import com.finance.tracker.repository.UserRepository;
import com.finance.tracker.service.TransactionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration test for testing service layer with database interactions and authentication
 * These tests create real users and set up proper authentication context
 */
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TransactionServiceIntegrationTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        // Clear security context
        SecurityContextHolder.clearContext();
        
        // Clear the database
        userRepository.deleteAll();
        
        // Create a test user in the database
        testUser = UserEntity.builder()
                .username("integrationTestUser")
                .email("integration@test.com")
                .password(passwordEncoder.encode("testPassword"))
                .role(Role.USER)
                .build();
        
        testUser = userRepository.saveAndFlush(testUser);
        
        // Set up authentication context
        UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }    @Test
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
    void testGetAllTransactions_ShouldReturnUserSpecificTransactions() {
        // Given - Create a transaction first
        TransactionDto dto = TransactionDto.builder()
                .amount(100.0)
                .type(TransactionDto.TransactionTypeDto.EXPENSE)
                .category("Test Expense")
                .description("Test transaction for retrieval")
                .build();
        
        transactionService.createTransaction(dto);

        // When
        var transactions = transactionService.getAllTransactions();

        // Then
        assertThat(transactions).isNotNull();
        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getAmount()).isEqualTo(100.0);
        assertThat(transactions.get(0).getCategory()).isEqualTo("Test Expense");
    }

    @Test
    void testUserIsolation_ShouldOnlyReturnOwnTransactions() {
        // Given - Create transaction for first user (testUser is already set up in @BeforeEach)
        TransactionDto firstUserTransaction = TransactionDto.builder()
                .amount(200.0)
                .type(TransactionDto.TransactionTypeDto.INCOME)
                .category("First User Income")
                .build();
        
        TransactionDto savedTransaction = transactionService.createTransaction(firstUserTransaction);
        assertThat(savedTransaction.getId()).isNotNull(); // Verify transaction was created

        // Create second user in a separate transaction to ensure persistence
        UserEntity secondUser = UserEntity.builder()
                .username("secondTestUser")
                .email("second@test.com")
                .password(passwordEncoder.encode("testPassword2"))
                .role(Role.USER)
                .build();
        
        secondUser = userRepository.saveAndFlush(secondUser); // Use saveAndFlush to ensure immediate persistence
        
        // Clear any cached authentication and set new context
        SecurityContextHolder.clearContext();
        UsernamePasswordAuthenticationToken secondAuth = 
                new UsernamePasswordAuthenticationToken(secondUser, null, secondUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(secondAuth);

        // When - Get transactions as second user
        var secondUserTransactions = transactionService.getAllTransactions();

        // Then - Should be empty for second user
        assertThat(secondUserTransactions).isEmpty();

        // Switch back to first user and verify they still see their transaction
        SecurityContextHolder.clearContext();
        UsernamePasswordAuthenticationToken firstAuth = 
                new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(firstAuth);
        
        var firstUserTransactions = transactionService.getAllTransactions();
        assertThat(firstUserTransactions).hasSize(1);
        assertThat(firstUserTransactions.get(0).getCategory()).isEqualTo("First User Income");
    }
}
