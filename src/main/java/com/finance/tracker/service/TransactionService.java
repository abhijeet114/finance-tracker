package com.finance.tracker.service;

import com.finance.tracker.model.Transaction;
import java.util.List;

public interface TransactionService {
    List<Transaction> getAllTransactions();
    Transaction getTransactionById(Long id);
    Transaction createTransaction(Transaction transaction);
    Transaction updateTransaction(Long id, Transaction transaction);
    void deleteTransaction(Long id);
}
