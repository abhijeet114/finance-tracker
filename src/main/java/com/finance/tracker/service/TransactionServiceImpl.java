package com.finance.tracker.service;

import com.finance.tracker.model.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TransactionServiceImpl implements TransactionService {
    
    private final Map<Long, Transaction> transactionMap = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactionMap.values());
    }

    @Override
    public Transaction getTransactionById(Long id) {
        Transaction transaction = transactionMap.get(id);
        if (transaction == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found with id: " + id);
        }
        return transaction;
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction cannot be null");
        }
        
        // Validate required fields
        if (transaction.getAmount() == null || transaction.getType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount and type are required fields");
        }

        // Set new ID and save
        transaction.setId(idGenerator.getAndIncrement());
        transactionMap.put(transaction.getId(), transaction);
        return transaction;
    }

    @Override
    public Transaction updateTransaction(Long id, Transaction transaction) {
        if (!transactionMap.containsKey(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found with id: " + id);
        }

        if (transaction == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction cannot be null");
        }

        // Validate required fields
        if (transaction.getAmount() == null || transaction.getType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount and type are required fields");
        }

        // Ensure the ID matches the path parameter
        transaction.setId(id);
        transactionMap.put(id, transaction);
        return transaction;
    }

    @Override
    public void deleteTransaction(Long id) {
        if (!transactionMap.containsKey(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found with id: " + id);
        }
        transactionMap.remove(id);
    }
}
