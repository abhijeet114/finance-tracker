package com.finance.tracker.controller;

import com.finance.tracker.api.TransactionsApi;
import com.finance.tracker.dto.TransactionDto;
import com.finance.tracker.mapper.TransactionMapper;
import com.finance.tracker.model.Transaction;
import com.finance.tracker.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
public class TransactionController implements TransactionsApi {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @Autowired
    public TransactionController(TransactionService transactionService, TransactionMapper transactionMapper) {
        this.transactionService = transactionService;
        this.transactionMapper = transactionMapper;
    }

    public ResponseEntity<List<Transaction>> getAllTransactions() {
        logger.info("Retrieving all transactions");
        List<TransactionDto> dtos = transactionService.getAllTransactions();
        List<Transaction> apiModels = transactionMapper.dtosToApiModels(dtos);
        return ResponseEntity.ok(apiModels);
    }

    public ResponseEntity<Transaction> getTransactionById(UUID id) {
        logger.info("Retrieving transaction with id: {}", id);
        try {
            TransactionDto dto = transactionService.getTransactionById(id);
            Transaction apiModel = transactionMapper.dtoToApiModel(dto);
            return ResponseEntity.ok(apiModel);
        } catch (ResponseStatusException e) {
            logger.warn("Transaction not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<Transaction> createTransaction(Transaction transaction) {
        logger.info("Creating new transaction");
        try {
            // Convert OpenAPI model to DTO
            TransactionDto dto = transactionMapper.apiModelToDto(transaction);
            
            // Process through service layer
            TransactionDto createdDto = transactionService.createTransaction(dto);
            
            // Convert back to OpenAPI model
            Transaction createdTransaction = transactionMapper.dtoToApiModel(createdDto);
            
            logger.info("Transaction created successfully with id: {}", createdTransaction.getId());
            return ResponseEntity.ok(createdTransaction);
        } catch (ResponseStatusException e) {
            logger.error("Failed to create transaction: {}", e.getReason());
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<Transaction> updateTransaction(UUID id, Transaction transaction) {
        logger.info("Updating transaction with id: {}", id);
        try {
            // Convert OpenAPI model to DTO
            TransactionDto dto = transactionMapper.apiModelToDto(transaction);
            
            // Process through service layer
            TransactionDto updatedDto = transactionService.updateTransaction(id, dto);
            
            // Convert back to OpenAPI model
            Transaction updatedTransaction = transactionMapper.dtoToApiModel(updatedDto);
            
            logger.info("Transaction updated successfully with id: {}", id);
            return ResponseEntity.ok(updatedTransaction);
        } catch (ResponseStatusException e) {
            logger.warn("Failed to update transaction with id {}: {}", id, e.getReason());
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<Void> deleteTransaction(UUID id) {
        logger.info("Deleting transaction with id: {}", id);
        try {
            transactionService.deleteTransaction(id);
            logger.info("Transaction deleted successfully with id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            logger.warn("Failed to delete transaction with id {}: {}", id, e.getReason());
            return ResponseEntity.notFound().build();
        }
    }
}
