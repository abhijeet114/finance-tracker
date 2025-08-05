package com.finance.tracker.service;

import com.finance.tracker.dto.TransactionDto;
import com.finance.tracker.entity.TransactionEntity;
import com.finance.tracker.mapper.TransactionMapper;
import com.finance.tracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public List<TransactionDto> getAllTransactions() {
        List<TransactionEntity> entities = transactionRepository.findAll();
        return transactionMapper.entitiesToDtos(entities);
    }

    @Override
    public TransactionDto getTransactionById(UUID id) {
        Optional<TransactionEntity> entityOptional = transactionRepository.findById(id);
        if (entityOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found with id: " + id);
        }
        return transactionMapper.entityToDto(entityOptional.get());
    }

    @Override
    public TransactionDto createTransaction(TransactionDto transactionDto) {
        if (transactionDto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction cannot be null");
        }
        
        // Validate required fields
        if (transactionDto.getAmount() == null || transactionDto.getType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount and type are required fields");
        }

        // Convert DTO to entity, save (UUID is auto-generated), and convert back to DTO
        TransactionEntity entity = transactionMapper.dtoToEntity(transactionDto);
        TransactionEntity savedEntity = transactionRepository.save(entity);
        return transactionMapper.entityToDto(savedEntity);
    }

    @Override
    public TransactionDto updateTransaction(UUID id, TransactionDto transactionDto) {
        if (!transactionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found with id: " + id);
        }

        if (transactionDto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction cannot be null");
        }

        // Validate required fields
        if (transactionDto.getAmount() == null || transactionDto.getType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount and type are required fields");
        }

        // Convert DTO to entity, ensure the ID matches, save, and convert back to DTO
        TransactionEntity entity = transactionMapper.dtoToEntity(transactionDto);
        entity.setId(id);
        TransactionEntity savedEntity = transactionRepository.save(entity);
        return transactionMapper.entityToDto(savedEntity);
    }

    @Override
    public void deleteTransaction(UUID id) {
        if (!transactionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
    }
}
