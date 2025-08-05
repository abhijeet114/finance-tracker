package com.finance.tracker.mapper;

import com.finance.tracker.dto.TransactionDto;
import com.finance.tracker.entity.TransactionEntity;
import com.finance.tracker.model.Transaction;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Simple mapper for converting between OpenAPI models, DTOs, and entities
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionMapper {
    
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);
    
    // ========================================
    // OpenAPI Model ↔ DTO conversions
    // ========================================
    
    /**
     * Maps OpenAPI Transaction model to DTO
     */
    @Mapping(source = "dateTime", target = "dateTime")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "type", target = "type")
    TransactionDto apiModelToDto(Transaction apiModel);
    
    /**
     * Maps DTO to OpenAPI Transaction model
     */
    @Mapping(source = "dateTime", target = "dateTime")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "type", target = "type")
    Transaction dtoToApiModel(TransactionDto dto);
    
    /**
     * Bulk conversion from API models to DTOs
     */
    List<TransactionDto> apiModelsToDtos(List<Transaction> apiModels);
    
    /**
     * Bulk conversion from DTOs to API models
     */
    List<Transaction> dtosToApiModels(List<TransactionDto> dtos);
    
    // ========================================
    // DTO ↔ Entity conversions
    // ========================================
    
    /**
     * Maps DTO to Entity
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "dateTime", target = "dateTime")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "type", target = "type")
    TransactionEntity dtoToEntity(TransactionDto dto);
    
    /**
     * Maps Entity to DTO
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "dateTime", target = "dateTime")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "type", target = "type")
    TransactionDto entityToDto(TransactionEntity entity);
    
    /**
     * Bulk conversion from DTOs to Entities
     */
    List<TransactionEntity> dtosToEntities(List<TransactionDto> dtos);
    
    /**
     * Bulk conversion from Entities to DTOs
     */
    List<TransactionDto> entitiesToDtos(List<TransactionEntity> entities);
    
    // ========================================
    // DateTime conversion methods
    // ========================================
    
    /**
     * Converts OffsetDateTime to LocalDateTime
     * Used when mapping from OpenAPI model to DTO/Entity
     */
    default LocalDateTime map(OffsetDateTime offsetDateTime) {
        return offsetDateTime != null ? offsetDateTime.toLocalDateTime() : null;
    }
    
    /**
     * Converts LocalDateTime to OffsetDateTime
     * Used when mapping from DTO/Entity to OpenAPI model
     */
    default OffsetDateTime map(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atOffset(ZoneOffset.UTC) : null;
    }
}
