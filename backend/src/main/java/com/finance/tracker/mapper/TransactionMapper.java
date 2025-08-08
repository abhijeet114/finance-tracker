package com.finance.tracker.mapper;

import com.finance.tracker.dto.TransactionDto;
import com.finance.tracker.entity.TransactionEntity;
import com.finance.tracker.model.TransactionRequest;
import com.finance.tracker.model.TransactionResponse;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

/**
 * Simple, clean mapper for converting between OpenAPI models, DTOs, and entities
 * Focuses only on mapping responsibilities
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionMapper {
    
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);
    
    // ========================================
    // Core Mapping Methods
    // ========================================
    
    /**
     * Maps OpenAPI TransactionRequest model to DTO
     * Handles datetime conversion from OffsetDateTime to LocalDateTime
     */
    @Mapping(source = "dateTime", target = "dateTime", qualifiedByName = "offsetToLocal")
    TransactionDto apiModelToDto(TransactionRequest apiModel);
    
    /**
     * Maps DTO to OpenAPI TransactionResponse model
     * Handles datetime conversion from LocalDateTime to OffsetDateTime
     */
    @Mapping(source = "dateTime", target = "dateTime", qualifiedByName = "localToOffset")
    TransactionResponse dtoToApiModel(TransactionDto dto);
    
    /**
     * Maps DTO to Entity
     * Direct mapping since both use LocalDateTime
     * User field is ignored as it's handled in the service layer
     */
    @Mapping(target = "user", ignore = true)
    TransactionEntity dtoToEntity(TransactionDto dto);
    
    /**
     * Maps Entity to DTO
     * Direct mapping since both use LocalDateTime
     */
    TransactionDto entityToDto(TransactionEntity entity);
    
    // ========================================
    // Bulk Conversions
    // ========================================
    
    List<TransactionDto> apiModelsToDtos(List<TransactionRequest> apiModels);
    List<TransactionResponse> dtosToApiModels(List<TransactionDto> dtos);
    List<TransactionEntity> dtosToEntities(List<TransactionDto> dtos);
    List<TransactionDto> entitiesToDtos(List<TransactionEntity> entities);
    Set<TransactionEntity> dtosToSetOfEntities(Set<TransactionDto> dtos);
    
    // ========================================
    // Update Methods
    // ========================================
    
    /**
     * Updates an existing entity with non-null values from DTO
     * Ignores ID and user to prevent overwrites
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(TransactionDto dto, @MappingTarget TransactionEntity entity);
    
    // ========================================
    // DateTime Conversion Helpers
    // ========================================
    
    @Named("localToOffset")
    default OffsetDateTime mapLocalToOffset(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atOffset(ZoneOffset.UTC) : null;
    }
    
    @Named("offsetToLocal")
    default LocalDateTime mapOffsetToLocal(OffsetDateTime offsetDateTime) {
        return offsetDateTime != null ? offsetDateTime.toLocalDateTime() : null;
    }
}
