package com.finance.tracker.repository;

import com.finance.tracker.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
    // JpaRepository provides all basic CRUD operations
    // Custom methods can be added here if needed
}
