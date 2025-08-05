# Finance Tracker - MVC Architecture with Domain Models and Mappers

## Overview
This project implements a proper layered MVC architecture with domain entities and OpenAPI generated models, connected via mappers. This approach provides separation of concerns and maintains clean boundaries between different layers.

## Architecture Layers

### 1. Controller Layer (`controller/`)
- **TransactionController**: REST API endpoints
- Handles HTTP requests/responses
- Uses OpenAPI generated models for API contracts
- Delegates business logic to service layer
- Includes proper logging and error handling

### 2. Service Layer (`service/`)
- **TransactionService** (Interface): Defines business operations
- **TransactionServiceImpl**: Implements business logic
- Works with OpenAPI models (API contracts)
- Uses mappers to convert between API models and domain entities
- Contains validation and error handling logic

### 3. Repository Layer (`repository/`)
- **TransactionRepository** (Interface): Defines data access operations
- **TransactionRepositoryImpl**: In-memory implementation
- Works with domain entities
- Provides data persistence abstraction
- Currently uses ConcurrentHashMap for simplicity (can be replaced with JPA/database)

### 4. Domain/Entity Layer (`entity/`)
- **TransactionEntity**: Domain model representing business logic
- **TransactionType**: Domain-specific enum
- Pure domain objects without external dependencies
- Uses LocalDateTime for internal date handling

### 5. Mapper Layer (`mapper/`)
- **TransactionMapper**: Converts between API models and domain entities
- Handles type conversions (e.g., OffsetDateTime ↔ LocalDateTime)
- Maps between different enum types
- Provides list conversion methods

### 6. API Models (`model/` - Generated)
- OpenAPI generated models in `target/generated-sources/`
- **Transaction**: API contract model
- Contains validation annotations
- Uses OffsetDateTime for API compatibility

## Data Flow

### Create Transaction Request:
```
Client → Controller → Service → Mapper → Repository
  ↓         ↓         ↓        ↓         ↓
 API     API DTO   API DTO  Entity   Entity
Model    Model     Model    Model    Model
```

### Get Transaction Response:
```
Repository → Mapper → Service → Controller → Client
    ↓         ↓        ↓         ↓         ↓
  Entity   API DTO  API DTO   API DTO   API
  Model    Model    Model     Model    Model
```

## Key Benefits

1. **Separation of Concerns**: Each layer has a distinct responsibility
2. **Domain Isolation**: Business logic is separated from API contracts
3. **Flexibility**: Can change API contracts without affecting domain logic
4. **Testability**: Each layer can be tested independently
5. **Maintainability**: Clear boundaries make code easier to understand and modify

## Components

### TransactionEntity (Domain Model)
```java
- Long id
- Double amount
- TransactionType type (domain enum)
- String category
- String description
- LocalDateTime dateTime
```

### Transaction (API Model - Generated)
```java
- Long id
- Double amount
- TypeEnum type (API enum)
- String category
- String description
- OffsetDateTime dateTime
```

### Mapper Methods
- `entityToDto(TransactionEntity)`: Domain → API
- `dtoToEntity(Transaction)`: API → Domain
- `entitiesToDtos(List<TransactionEntity>)`: List conversion
- `dtosToEntities(List<Transaction>)`: List conversion

## Usage Example

```java
// In Controller - receives API model
Transaction apiTransaction = request.getBody();

// In Service - converts to domain entity
TransactionEntity entity = mapper.dtoToEntity(apiTransaction);

// In Repository - saves domain entity
TransactionEntity saved = repository.save(entity);

// In Service - converts back to API model
Transaction response = mapper.entityToDto(saved);

// In Controller - returns API model
return ResponseEntity.ok(response);
```

## Future Enhancements

1. **Add MapStruct**: Replace manual mapper with annotation-based mapping
2. **Database Integration**: Replace in-memory repository with JPA repository
3. **Validation**: Add domain-level validation in entities
4. **DTOs**: Add specific DTOs for different API operations (create, update, response)
5. **Auditing**: Add created/modified timestamps to domain entities

This architecture provides a solid foundation for a scalable, maintainable application following Spring Boot best practices.
