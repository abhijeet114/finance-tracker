package com.finance.tracker.service;

import com.finance.tracker.dto.TransactionDto;
import com.finance.tracker.model.TransactionRequest;
import com.finance.tracker.model.TransactionResponse;
import java.util.List;
import java.util.UUID;

public interface TransactionService {
    
    // Methods that work with OpenAPI models directly
    List<TransactionResponse> getAllTransactions();
    TransactionResponse getTransactionById(UUID id);
    TransactionResponse createTransaction(TransactionRequest transactionRequest);
    TransactionResponse updateTransaction(UUID id, TransactionRequest transactionRequest);
    void deleteTransaction(UUID id);
    
    // Internal DTO methods for business logic (can be package-private or private in implementation)
    List<TransactionDto> getAllTransactionDtos();
    TransactionDto getTransactionDtoById(UUID id);
    TransactionDto createTransactionDto(TransactionDto transactionDto);
    TransactionDto updateTransactionDto(UUID id, TransactionDto transactionDto);
}
