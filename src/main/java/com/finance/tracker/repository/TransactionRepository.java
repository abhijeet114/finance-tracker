package com.finance.tracker.repository;

import com.finance.tracker.entity.TransactionEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {
    List<TransactionEntity> findAll();
    Optional<TransactionEntity> findById(UUID id);
    TransactionEntity save(TransactionEntity transaction);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
