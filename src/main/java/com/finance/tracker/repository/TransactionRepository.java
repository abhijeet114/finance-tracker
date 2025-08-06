package com.finance.tracker.repository;

import com.finance.tracker.entity.TransactionEntity;
import com.finance.tracker.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
    
    // Find all transactions for a specific user
    List<TransactionEntity> findByUser(UserEntity user);
    
    // Find a transaction by ID and user (for authorization)
    Optional<TransactionEntity> findByIdAndUser(UUID id, UserEntity user);
    
    // Check if a transaction exists for a specific user
    boolean existsByIdAndUser(UUID id, UserEntity user);
}
