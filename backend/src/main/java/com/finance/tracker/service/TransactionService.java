package com.finance.tracker.service;

import com.finance.tracker.dto.TransactionDto;
import java.util.List;
import java.util.UUID;

public interface TransactionService {
    List<TransactionDto> getAllTransactions();
    TransactionDto getTransactionById(UUID id);
    TransactionDto createTransaction(TransactionDto transactionDto);
    TransactionDto updateTransaction(UUID id, TransactionDto transactionDto);
    void deleteTransaction(UUID id);
}
