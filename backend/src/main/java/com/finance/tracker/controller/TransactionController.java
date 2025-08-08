package com.finance.tracker.controller;

import com.finance.tracker.api.TransactionsApi;
import com.finance.tracker.model.ModelApiResponse;
import com.finance.tracker.model.TransactionRequest;
import com.finance.tracker.model.TransactionResponse;
import com.finance.tracker.service.TransactionService;
import com.finance.tracker.util.ResponseBuilder;
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

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public ResponseEntity<ModelApiResponse> getAllTransactions() {
        logger.info("Retrieving all transactions");
        try {
            // Delegate to service layer for all business logic and model conversion
            List<TransactionResponse> apiModels = transactionService.getAllTransactions();
            
            return ResponseBuilder.success(apiModels, "Transactions retrieved successfully");
            
        } catch (Exception e) {
            logger.error("Failed to retrieve transactions: {}", e.getMessage());
            return ResponseBuilder.error(500, "Internal Server Error", "Failed to retrieve transactions");
        }
    }

    @Override
    public ResponseEntity<ModelApiResponse> getTransactionById(UUID id) {
        logger.info("Retrieving transaction with id: {}", id);
        try {
            // Delegate to service layer for all business logic and model conversion
            TransactionResponse apiModel = transactionService.getTransactionById(id);
            
            return ResponseBuilder.success(apiModel, "Transaction retrieved successfully");
            
        } catch (ResponseStatusException e) {
            logger.warn("Transaction not found with id: {}", id);
            return ResponseBuilder.error(404, "Not Found", "Transaction not found");
        }
    }

    @Override
    public ResponseEntity<ModelApiResponse> createTransaction(TransactionRequest transaction) {
        logger.info("Creating new transaction");
        try {
            // Delegate to service layer for all business logic and model conversion
            TransactionResponse createdTransaction = transactionService.createTransaction(transaction);
            
            logger.info("Transaction created successfully with id: {}", createdTransaction.getId());
            return ResponseBuilder.success(201, createdTransaction, "Transaction created successfully");
            
        } catch (ResponseStatusException e) {
            logger.error("Failed to create transaction: {}", e.getReason());
            return ResponseBuilder.error(400, "Bad Request", "Failed to create transaction: " + e.getReason());
        }
    }

    @Override
    public ResponseEntity<ModelApiResponse> updateTransaction(UUID id, TransactionRequest transaction) {
        logger.info("Updating transaction with id: {}", id);
        try {
            // Delegate to service layer for all business logic and model conversion
            TransactionResponse updatedTransaction = transactionService.updateTransaction(id, transaction);
            
            logger.info("Transaction updated successfully with id: {}", id);
            return ResponseBuilder.success(updatedTransaction, "Transaction updated successfully");
            
        } catch (ResponseStatusException e) {
            logger.warn("Failed to update transaction with id {}: {}", id, e.getReason());
            
            int statusCode = e.getStatusCode().value();
            String errorType = statusCode == 404 ? "Not Found" : "Bad Request";
            
            return ResponseBuilder.error(statusCode, errorType, "Failed to update transaction: " + e.getReason());
        }
    }

    @Override
    public ResponseEntity<ModelApiResponse> deleteTransaction(UUID id) {
        logger.info("Deleting transaction with id: {}", id);
        try {
            transactionService.deleteTransaction(id);
            
            logger.info("Transaction deleted successfully with id: {}", id);
            return ResponseBuilder.success(204, null, "Transaction deleted successfully");
            
        } catch (ResponseStatusException e) {
            logger.warn("Failed to delete transaction with id {}: {}", id, e.getReason());
            return ResponseBuilder.error(404, "Not Found", "Transaction not found");
        }
    }
}
