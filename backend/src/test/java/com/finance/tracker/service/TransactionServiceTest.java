package com.finance.tracker.service;

import com.finance.tracker.dto.TransactionDto;
import com.finance.tracker.entity.TransactionEntity;
import com.finance.tracker.entity.TransactionType;
import com.finance.tracker.entity.UserEntity;
import com.finance.tracker.entity.Role;
import com.finance.tracker.mapper.TransactionMapper;
import com.finance.tracker.model.TransactionRequest;
import com.finance.tracker.model.TransactionResponse;
import com.finance.tracker.repository.TransactionRepository;
import com.finance.tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
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

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private TransactionDto testDto;
    private TransactionEntity testEntity;
    private TransactionRequest testRequest;
    private TransactionResponse testResponse;
    private UserEntity testUser;
    private UUID testId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        userId = UUID.randomUUID();
        
        // Create test user
        testUser = UserEntity.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();
        
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
                .user(testUser)
                .build();

        testRequest = new TransactionRequest()
                .amount(100.0)
                .type(TransactionRequest.TypeEnum.INCOME)
                .category("Test Category")
                .description("Test Description");

        testResponse = new TransactionResponse()
                .id(testId)
                .amount(100.0)
                .type(TransactionResponse.TypeEnum.INCOME)
                .category("Test Category")
                .description("Test Description");
                
        // Mock security context
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(testUser);
    }

    @Test
    void testCreateTransaction_ShouldReturnSavedTransaction() {
        // Given
        when(mapper.apiModelToDto(any(TransactionRequest.class))).thenReturn(testDto);
        when(mapper.dtoToEntity(any(TransactionDto.class))).thenReturn(testEntity);
        when(repository.save(any(TransactionEntity.class))).thenReturn(testEntity);
        when(mapper.entityToDto(any(TransactionEntity.class))).thenReturn(testDto);
        when(mapper.dtoToApiModel(any(TransactionDto.class))).thenReturn(testResponse);

        // When
        TransactionResponse result = transactionService.createTransaction(testRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testId);
        verify(mapper).apiModelToDto(testRequest);
        verify(mapper).dtoToEntity(testDto);
        verify(repository).save(testEntity);
        verify(mapper).entityToDto(testEntity);
        verify(mapper).dtoToApiModel(testDto);
    }

    @Test
    void testGetAllTransactions_ShouldReturnAllTransactions() {
        // Given
        List<TransactionEntity> entities = Arrays.asList(testEntity);
        List<TransactionDto> dtos = Arrays.asList(testDto);
        List<TransactionResponse> responses = Arrays.asList(testResponse);
        
        when(repository.findByUser(testUser)).thenReturn(entities);
        when(mapper.entitiesToDtos(entities)).thenReturn(dtos);
        when(mapper.dtosToApiModels(dtos)).thenReturn(responses);

        // When
        List<TransactionResponse> result = transactionService.getAllTransactions();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(testId);
        verify(repository).findByUser(testUser);
        verify(mapper).entitiesToDtos(entities);
        verify(mapper).dtosToApiModels(dtos);
    }

    @Test
    void testGetTransactionById_WhenExists_ShouldReturnTransaction() {
        // Given
        when(repository.findByIdAndUser(testId, testUser)).thenReturn(Optional.of(testEntity));
        when(mapper.entityToDto(testEntity)).thenReturn(testDto);
        when(mapper.dtoToApiModel(testDto)).thenReturn(testResponse);

        // When
        TransactionResponse result = transactionService.getTransactionById(testId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testId);
        verify(repository).findByIdAndUser(testId, testUser);
        verify(mapper).entityToDto(testEntity);
        verify(mapper).dtoToApiModel(testDto);
    }

    @Test
    void testGetTransactionById_WhenNotExists_ShouldThrowException() {
        // Given
        when(repository.findByIdAndUser(testId, testUser)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> transactionService.getTransactionById(testId))
                .isInstanceOf(RuntimeException.class);
        verify(repository).findByIdAndUser(testId, testUser);
        verify(mapper, never()).entityToDto(any());
    }

    @Test
    void testDeleteTransaction_ShouldCallRepositoryDelete() {
        // Given
        when(repository.findByIdAndUser(testId, testUser)).thenReturn(Optional.of(testEntity));
        
        // When
        transactionService.deleteTransaction(testId);

        // Then
        verify(repository).findByIdAndUser(testId, testUser);
        verify(repository).deleteById(testId);
    }
}
