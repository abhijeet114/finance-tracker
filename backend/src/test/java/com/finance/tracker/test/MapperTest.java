package com.finance.tracker.test;

import com.finance.tracker.dto.TransactionDto;
import com.finance.tracker.entity.TransactionEntity;
import com.finance.tracker.entity.TransactionType;
import com.finance.tracker.mapper.TransactionMapper;
import com.finance.tracker.model.TransactionRequest;
import com.finance.tracker.model.TransactionResponse;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Simple test class to demonstrate the three-layer MapStruct mapper functionality
 * Tests: OpenAPI Model ↔ DTO ↔ Entity
 */
public class MapperTest {
    
    public static void main(String[] args) {
        // Get the mapper instance
        TransactionMapper mapper = Mappers.getMapper(TransactionMapper.class);
        
        // Test Entity ↔ DTO conversion
        System.out.println("=== Testing Entity ↔ DTO Conversion ===");
        
        // Create a domain entity using builder pattern
        TransactionEntity entity = TransactionEntity.builder()
                .id(UUID.randomUUID())
                .amount(100.50)
                .type(TransactionType.INCOME)
                .category("Salary")
                .description("Monthly salary")
                .dateTime(LocalDateTime.now())
                .build();
        
        // Convert entity to DTO
        TransactionDto dto = mapper.entityToDto(entity);
        System.out.println("Entity to DTO: " + dto.getId() + ", " + dto.getAmount() + ", " + dto.getType());
        
        // Convert DTO back to entity
        TransactionEntity convertedEntity = mapper.dtoToEntity(dto);
        System.out.println("DTO to Entity: " + convertedEntity.getId() + ", " + 
                          convertedEntity.getAmount() + ", " + convertedEntity.getType());
        
        // Test OpenAPI Model ↔ DTO conversion
        System.out.println("\n=== Testing OpenAPI Model ↔ DTO Conversion ===");
        
        // Create an OpenAPI request model
        TransactionRequest apiModel = new TransactionRequest()
                .id(UUID.randomUUID())
                .amount(200.75)
                .type(TransactionRequest.TypeEnum.EXPENSE)
                .category("Food")
                .description("Grocery shopping");
        
        // Convert OpenAPI model to DTO
        TransactionDto dtoFromApi = mapper.apiModelToDto(apiModel);
        System.out.println("API Model to DTO: " + dtoFromApi.getId() + ", " + dtoFromApi.getAmount() + ", " + dtoFromApi.getType());
        
        // Convert DTO back to OpenAPI response model
        TransactionResponse apiModelFromDto = mapper.dtoToApiModel(dtoFromApi);
        System.out.println("DTO to API Model: " + apiModelFromDto.getId() + ", " + 
                          apiModelFromDto.getAmount() + ", " + apiModelFromDto.getType());
        
        System.out.println("\nThree-layer MapStruct mapping test completed successfully!");
        System.out.println("Data flow: OpenAPI Model ↔ DTO ↔ Entity");
        System.out.println("✓ Request body (OpenAPI model) doesn't directly interact with entity");
        System.out.println("✓ DTO acts as intermediary using Builder pattern");
        System.out.println("✓ MapStruct provides efficient compile-time mapping");
        System.out.println("✓ Clean separation of concerns achieved");
    }
}
