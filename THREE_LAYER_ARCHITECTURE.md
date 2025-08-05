# Three-Layer Data Transfer Architecture

## Overview
This project implements a clean three-layer architecture for data transfer:

```
OpenAPI Model ↔ DTO ↔ Entity
```

## Architecture Components

### 1. OpenAPI Model (`com.finance.tracker.model.Transaction`)
- **Purpose**: Generated from OpenAPI specification
- **Location**: `target/generated-sources/openapi/`
- **Characteristics**: 
  - Auto-generated from `openapi.yml`
  - Uses `OffsetDateTime` for timestamps
  - Contains `TypeEnum` with values: `INCOME`, `EXPENSE`
  - Immutable by design

### 2. DTO (Data Transfer Object) (`com.finance.tracker.dto.TransactionDto`)
- **Purpose**: Intermediary layer between API and persistence
- **Location**: `src/main/java/com/finance/tracker/dto/`
- **Characteristics**:
  - Uses Lombok's `@Builder` pattern
  - Uses `LocalDateTime` for timestamps
  - Contains `TransactionTypeDto` enum
  - Mutable and flexible

### 3. Entity (`com.finance.tracker.entity.TransactionEntity`)
- **Purpose**: Database persistence layer
- **Location**: `src/main/java/com/finance/tracker/entity/`
- **Characteristics**:
  - Uses Lombok's `@Builder` pattern
  - Uses `LocalDateTime` for timestamps
  - Contains `TransactionType` enum
  - Optimized for JPA/database operations

## Data Flow

### Request Flow (Controller → Service → Repository)
1. **Controller** receives OpenAPI model from HTTP request
2. **Converter** transforms OpenAPI model → DTO
3. **Service** processes business logic using DTO
4. **Converter** transforms DTO → Entity
5. **Repository** persists Entity to database

### Response Flow (Repository → Service → Controller)
1. **Repository** retrieves Entity from database
2. **Converter** transforms Entity → DTO
3. **Service** processes business logic using DTO
4. **Converter** transforms DTO → OpenAPI model
5. **Controller** returns OpenAPI model in HTTP response

## Key Benefits

### 1. Separation of Concerns
- **API Layer**: Handles HTTP requests/responses and validation
- **Business Layer**: Processes business logic with DTOs
- **Persistence Layer**: Manages database operations with entities

### 2. Builder Pattern Usage
- DTOs and Entities use Lombok's `@Builder` for immutable object creation
- Provides fluent API for object construction
- Ensures consistent object state

### 3. Type Safety
- Each layer has its own enum types preventing mixing concerns
- Compile-time safety between layers
- Clear boundaries between external API and internal data

### 4. Flexibility
- Changes to OpenAPI spec don't directly affect persistence layer
- Database schema changes don't impact API contracts
- DTO layer can implement complex transformations and business rules

## Implementation Files

### Core Components
- `TransactionDto.java` - The intermediary DTO with builder pattern
- `TransactionMapper.java` - MapStruct-based mapping between all layers

### Service Layer
- `TransactionService.java` - Interface working with DTOs
- `TransactionServiceImpl.java` - Implementation using MapStruct mapper

### Controller Layer
- `TransactionController.java` - REST controller handling conversions

### Testing
- `MapperTest.java` - Demonstrates three-layer MapStruct conversion

## Usage Example

```java
// Controller receives OpenAPI model
Transaction apiModel = requestBody;

// Convert to DTO for business processing using MapStruct
TransactionDto dto = mapper.apiModelToDto(apiModel);

// Service processes with DTO
TransactionDto processedDto = service.createTransaction(dto);

// Convert DTO to Entity for persistence using MapStruct
TransactionEntity entity = mapper.dtoToEntity(processedDto);

// Save to database
repository.save(entity);
```

## MapStruct Advantages

### 1. Compile-time Code Generation
- MapStruct generates mapping code at compile time
- No runtime reflection overhead
- Type-safe mappings with compile-time validation

### 2. Performance
- Generated code is as fast as hand-written mapping code
- No runtime dependencies beyond the generated classes
- Optimized for performance-critical applications

### 3. Maintainability
- Clean interface-based approach
- IDE support with auto-completion
- Easy to extend and customize mappings

### 4. Automatic Mapping
- Maps properties with matching names automatically
- Custom mappings for special cases (enums, date types)
- Handles complex nested object mappings

This architecture ensures that the OpenAPI request body never directly interacts with the database entity, maintaining clean separation of concerns and using the builder pattern throughout the DTO layer.
