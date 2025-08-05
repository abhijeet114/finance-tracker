package com.finance.tracker.repository;

import com.finance.tracker.entity.TransactionEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {
    
    private final Map<UUID, TransactionEntity> transactionMap = new ConcurrentHashMap<>();

    @Override
    public List<TransactionEntity> findAll() {
        return new ArrayList<>(transactionMap.values());
    }

    @Override
    public Optional<TransactionEntity> findById(UUID id) {
        return Optional.ofNullable(transactionMap.get(id));
    }

    @Override
    public TransactionEntity save(TransactionEntity transaction) {
        // With @Builder.Default, the UUID is already generated
        transactionMap.put(transaction.getId(), transaction);
        return transaction;
    }

    @Override
    public void deleteById(UUID id) {
        transactionMap.remove(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return transactionMap.containsKey(id);
    }
}
