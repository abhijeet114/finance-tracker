package com.finance.tracker.controller;

import com.finance.tracker.api.TransactionsApi;
import com.finance.tracker.model.Transaction;
import com.finance.tracker.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class TransactionController implements TransactionsApi {
    
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    public ResponseEntity<Transaction> getTransactionById(Long id) {
        try {
            return ResponseEntity.ok(transactionService.getTransactionById(id));
        } catch (ResponseStatusException e) {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<Transaction> createTransaction(Transaction transaction) {
        try {
            return ResponseEntity.ok(transactionService.createTransaction(transaction));
        } catch (ResponseStatusException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<Transaction> updateTransaction(Long id, Transaction transaction) {
        try {
            return ResponseEntity.ok(transactionService.updateTransaction(id, transaction));
        } catch (ResponseStatusException e) {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<Void> deleteTransaction(Long id) {
        try {
            transactionService.deleteTransaction(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
