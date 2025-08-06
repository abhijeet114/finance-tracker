package com.finance.tracker.service;

import com.finance.tracker.dto.TransactionDto;
import com.finance.tracker.entity.TransactionEntity;
import com.finance.tracker.entity.TransactionType;
import com.finance.tracker.mapper.TransactionMapper;
import com.finance.tracker.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit test for TransactionService with mocked dependencies
 * This test does not require database connections and tests business logic only
 */
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository repository;

    @Mock
    private TransactionMapper mapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private TransactionDto testDto;
    private TransactionEntity testEntity;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        
        testDto = TransactionDto.builder()
                .id(testId)
                .amount(100.0)
                .type(TransactionDto.TransactionTypeDto.INCOME)
                .category("Test Category")
                .description("Test Description")
                .dateTime(LocalDateTime.now())
                .build();

        testEntity = TransactionEntity.builder()
                .id(testId)
                .amount(100.0)
                .type(TransactionType.INCOME)
                .category("Test Category")
                .description("Test Description")
                .dateTime(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateTransaction_ShouldReturnSavedTransaction() {
        // Given
        when(mapper.dtoToEntity(any(TransactionDto.class))).thenReturn(testEntity);
        when(repository.save(any(TransactionEntity.class))).thenReturn(testEntity);
        when(mapper.entityToDto(any(TransactionEntity.class))).thenReturn(testDto);

        // When
        TransactionDto result = transactionService.createTransaction(testDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testId);
        verify(mapper).dtoToEntity(testDto);
        verify(repository).save(testEntity);
        verify(mapper).entityToDto(testEntity);
    }

    @Test
    void testGetAllTransactions_ShouldReturnAllTransactions() {
        // Given
        List<TransactionEntity> entities = Arrays.asList(testEntity);
        List<TransactionDto> dtos = Arrays.asList(testDto);
        
        when(repository.findAll()).thenReturn(entities);
        when(mapper.entitiesToDtos(entities)).thenReturn(dtos);

        // When
        List<TransactionDto> result = transactionService.getAllTransactions();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(testId);
        verify(repository).findAll();
        verify(mapper).entitiesToDtos(entities);
    }

    @Test
    void testGetTransactionById_WhenExists_ShouldReturnTransaction() {
        // Given
        when(repository.findById(testId)).thenReturn(Optional.of(testEntity));
        when(mapper.entityToDto(testEntity)).thenReturn(testDto);

        // When
        TransactionDto result = transactionService.getTransactionById(testId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testId);
        verify(repository).findById(testId);
        verify(mapper).entityToDto(testEntity);
    }

    @Test
    void testGetTransactionById_WhenNotExists_ShouldThrowException() {
        // Given
        when(repository.findById(testId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> transactionService.getTransactionById(testId))
                .isInstanceOf(RuntimeException.class);
        verify(repository).findById(testId);
        verify(mapper, never()).entityToDto(any());
    }

    @Test
    void testDeleteTransaction_ShouldCallRepositoryDelete() {
        // Given
        when(repository.existsById(testId)).thenReturn(true);
        
        // When
        transactionService.deleteTransaction(testId);

        // Then
        verify(repository).existsById(testId);
        verify(repository).deleteById(testId);
    }
}
