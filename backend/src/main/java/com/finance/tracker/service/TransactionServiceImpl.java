package com.finance.tracker.service;

import com.finance.tracker.dto.TransactionDto;
import com.finance.tracker.entity.TransactionEntity;
import com.finance.tracker.entity.UserEntity;
import com.finance.tracker.mapper.TransactionMapper;
import com.finance.tracker.model.TransactionRequest;
import com.finance.tracker.model.TransactionResponse;
import com.finance.tracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, 
                                TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    private UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return user;
    }

    // OpenAPI model methods
    @Override
    public List<TransactionResponse> getAllTransactions() {
        List<TransactionDto> dtos = getAllTransactionDtos();
        return transactionMapper.dtosToApiModels(dtos);
    }

    @Override
    public TransactionResponse getTransactionById(UUID id) {
        TransactionDto dto = getTransactionDtoById(id);
        return transactionMapper.dtoToApiModel(dto);
    }

    @Override
    public TransactionResponse createTransaction(TransactionRequest transactionRequest) {
        // Convert OpenAPI model to DTO
        TransactionDto dto = transactionMapper.apiModelToDto(transactionRequest);
        
        // Process through DTO layer
        TransactionDto createdDto = createTransactionDto(dto);
        
        // Convert back to OpenAPI model
        return transactionMapper.dtoToApiModel(createdDto);
    }

    @Override
    public TransactionResponse updateTransaction(UUID id, TransactionRequest transactionRequest) {
        // Convert OpenAPI model to DTO
        TransactionDto dto = transactionMapper.apiModelToDto(transactionRequest);
        
        // Process through DTO layer
        TransactionDto updatedDto = updateTransactionDto(id, dto);
        
        // Convert back to OpenAPI model
        return transactionMapper.dtoToApiModel(updatedDto);
    }

    // DTO-based methods for internal business logic
    @Override
    public List<TransactionDto> getAllTransactionDtos() {
        UserEntity currentUser = getCurrentUser();
        List<TransactionEntity> entities = transactionRepository.findByUser(currentUser);
        return transactionMapper.entitiesToDtos(entities);
    }

    @Override
    public TransactionDto getTransactionDtoById(UUID id) {
        UserEntity currentUser = getCurrentUser();
        Optional<TransactionEntity> entityOptional = transactionRepository.findByIdAndUser(id, currentUser);
        
        if (entityOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found with id: " + id);
        }
        return transactionMapper.entityToDto(entityOptional.get());
    }

    @Override
    public TransactionDto createTransactionDto(TransactionDto transactionDto) {
        if (transactionDto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction cannot be null");
        }
        
        // Validate required fields
        if (transactionDto.getAmount() == null || transactionDto.getType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount and type are required fields");
        }

        UserEntity currentUser = getCurrentUser();
        
        // Convert DTO to entity, ensure ID is null for new entities, save, and convert back to DTO
        TransactionEntity entity = transactionMapper.dtoToEntity(transactionDto);
        entity.setId(null); // Ensure ID is null so Hibernate can auto-generate it
        entity.setUser(currentUser); // Set the current user
        
        // Set current time if dateTime is not provided
        if (entity.getDateTime() == null) {
            entity.setDateTime(LocalDateTime.now());
        }
        
        TransactionEntity savedEntity = transactionRepository.save(entity);
        return transactionMapper.entityToDto(savedEntity);
    }

    @Override
    public TransactionDto updateTransactionDto(UUID id, TransactionDto transactionDto) {
        UserEntity currentUser = getCurrentUser();
        
        Optional<TransactionEntity> existingEntityOptional = transactionRepository.findByIdAndUser(id, currentUser);
        if (existingEntityOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found with id: " + id);
        }

        if (transactionDto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction cannot be null");
        }

        // Validate required fields
        if (transactionDto.getAmount() == null || transactionDto.getType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount and type are required fields");
        }

        TransactionEntity existingEntity = existingEntityOptional.get();
        
        // Convert DTO to entity, ensure the ID matches, save, and convert back to DTO
        TransactionEntity entity = transactionMapper.dtoToEntity(transactionDto);
        entity.setId(id);
        entity.setUser(currentUser); // Ensure user is set
        
        // Preserve original dateTime if not provided in update
        if (entity.getDateTime() == null) {
            entity.setDateTime(existingEntity.getDateTime());
        }
        
        TransactionEntity savedEntity = transactionRepository.save(entity);
        return transactionMapper.entityToDto(savedEntity);
    }

    @Override
    public void deleteTransaction(UUID id) {
        UserEntity currentUser = getCurrentUser();
        
        Optional<TransactionEntity> entityOptional = transactionRepository.findByIdAndUser(id, currentUser);
        if (entityOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found with id: " + id);
        }
        
        transactionRepository.deleteById(id);
    }
}
