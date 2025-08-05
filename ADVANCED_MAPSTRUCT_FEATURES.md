# Advanced MapStruct Implementation - TransactionMapper

This document explains the advanced MapStruct features implemented in the `TransactionMapper` interface, showcasing the latest capabilities and best practices from MapStruct 1.6.3.

## 🚀 Advanced Features Implemented

### 1. **Spring Component Model with Constructor Injection**
```java
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
```
- **Benefit**: Spring-managed bean with constructor injection for better testability and immutability
- **Usage**: Can be autowired into controllers, services using `@Autowired` or constructor injection

### 2. **Comprehensive Null Value Handling**
```java
nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT,
nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
nullValueMapMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
```
- **Benefits**: 
  - Prevents NullPointerExceptions
  - Returns sensible defaults for null collections/maps
  - Consistent null handling across all mapping scenarios
  - Better control over update operations

### 3. **Custom Conditional Mapping**
```java
@Condition
@Named("isValidAmount")
default boolean isValidAmount(Double amount) {
    return amount != null && amount > 0;
}

@Mapping(target = "amount", source = "amount", conditionQualifiedByName = "isValidAmount")
```
- **Benefit**: Only maps properties that meet specific business rules
- **Use Cases**: Validation, business logic enforcement, data quality assurance

### 4. **Advanced Enum Mapping with Fallback Strategies**
```java
@ValueMappings({
    @ValueMapping(source = "INCOME", target = "INCOME"),
    @ValueMapping(source = "EXPENSE", target = "EXPENSE"),
    @ValueMapping(source = MappingConstants.NULL, target = "EXPENSE"),
    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = "EXPENSE")
})
```
- **Benefits**:
  - Handles unknown enum values gracefully
  - Provides sensible defaults for null values
  - Future-proof against enum changes
  - Explicit mapping for better maintainability

### 5. **Timezone-Aware DateTime Conversion**
```java
@Named("localToOffset")
default OffsetDateTime mapLocalToOffset(LocalDateTime value) {
    return value == null ? null : value.atOffset(ZoneOffset.UTC);
}

@Named("offsetToLocal")
default LocalDateTime mapOffsetToLocal(OffsetDateTime value) {
    return value == null ? null : value.toLocalDateTime();
}
```
- **Benefits**:
  - Consistent timezone handling (UTC)
  - Null-safe conversions
  - Clear separation between local and offset date times

### 6. **Builder Pattern Support**
```java
builder = @Builder(disableBuilder = false)
```
- **Benefit**: Leverages Lombok builders for immutable object creation
- **Performance**: More efficient than setter-based mapping for immutable objects

### 7. **Before/After Mapping Hooks**
```java
@BeforeMapping
default void validateSource(Object source, @TargetType Class<?> targetType) {
    if (source == null) {
        throw new IllegalArgumentException("Source object cannot be null for mapping to " + targetType.getSimpleName());
    }
}

@AfterMapping
default void auditEntityMapping(@MappingTarget TransactionEntity target, Object source) {
    if (target != null && target.getId() == null) {
        target.setId(UUID.randomUUID());
    }
}
```
- **Benefits**:
  - Input validation
  - Post-processing logic
  - Audit trail creation
  - Business rule enforcement
  - Default value assignment

### 8. **Collection and Set Mapping with Strategies**
```java
@IterableMapping(qualifiedByName = "apiModelToDto")
List<TransactionDto> apiModelsToDtos(List<Transaction> apiModels);

@IterableMapping(qualifiedByName = "apiModelToDto")
Set<TransactionDto> apiModelsToSetOfDtos(Set<Transaction> apiModels);

collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
```
- **Benefits**:
  - Efficient bulk operations
  - Consistent element mapping
  - Support for different collection types
  - Optimized collection handling

### 9. **Update Methods with Selective Property Mapping**
```java
@Mapping(target = "id", ignore = true) // Don't update ID
@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
void updateEntityFromDto(TransactionDto dto, @MappingTarget TransactionEntity entity);
```
- **Benefits**:
  - Perfect for PATCH operations
  - Preserves existing values when source is null
  - Prevents accidental ID updates
  - Efficient partial updates

### 10. **Named Mappings for Organization**
```java
@Named("apiModelToDto")
TransactionDto apiModelToDto(Transaction apiModel);

@IterableMapping(qualifiedByName = "apiModelToDto")
List<TransactionDto> apiModelsToDtos(List<Transaction> apiModels);
```
- **Benefits**:
  - Reusable mapping logic
  - Better organization
  - Explicit method selection
  - Reduces code duplication

### 11. **Expression-Based Default Values**
```java
@Mapping(target = "id", source = "id", defaultExpression = "java(java.util.UUID.randomUUID())")
@Mapping(target = "dateTime", source = "dateTime", defaultExpression = "java(java.time.LocalDateTime.now())")
```
- **Benefits**:
  - Dynamic default value generation
  - Business logic in defaults
  - Consistent entity creation
  - Automatic audit field population

### 12. **Custom Object Factories**
```java
@ObjectFactory
default TransactionEntity createTransactionEntity() {
    return TransactionEntity.builder()
        .id(UUID.randomUUID())
        .dateTime(LocalDateTime.now())
        .build();
}
```
- **Benefits**:
  - Custom object creation logic
  - Pre-populated default values
  - Integration with builders
  - Consistent object initialization

### 13. **Comprehensive Reporting Policies**
```java
unmappedTargetPolicy = ReportingPolicy.WARN,
unmappedSourcePolicy = ReportingPolicy.IGNORE
```
- **Benefits**:
  - Compile-time safety
  - Catches mapping errors early
  - Configurable strictness levels
  - Better maintainability

## 🎯 Key Benefits of This Implementation

### **1. Type Safety**
- Compile-time validation of mappings
- Early detection of incompatible changes
- Reduced runtime errors

### **2. Performance**
- Generated code with plain method calls (no reflection)
- Optimized for speed
- Minimal memory allocation

### **3. Maintainability**
- Clear separation of concerns
- Self-documenting mapping logic
- Easy to extend and modify
- Consistent patterns across the application

### **4. Robustness**
- Comprehensive null handling
- Input validation
- Business rule enforcement
- Graceful error handling

### **5. Flexibility**
- Support for complex mapping scenarios
- Conditional mapping based on business rules
- Multiple collection types
- Partial updates

## 🔧 Usage Examples

### **Basic Mapping**
```java
@Autowired
private TransactionMapper mapper;

// API to DTO
TransactionDto dto = mapper.apiModelToDto(apiModel);

// DTO to Entity with defaults
TransactionEntity entity = mapper.dtoToEntity(dto);

// Bulk operations
List<TransactionDto> dtos = mapper.apiModelsToDtos(apiModels);
```

### **Update Operations**
```java
// PATCH operation - only updates non-null fields
TransactionDto patchDto = TransactionDto.builder()
    .amount(150.0)
    .category("Updated Category")
    .build();

mapper.updateEntityFromDto(patchDto, existingEntity);
// Only amount and category are updated, other fields remain unchanged
```

### **Validation Integration**
```java
// Automatic validation during mapping
Transaction invalidTransaction = new Transaction();
invalidTransaction.setAmount(-100.0); // Invalid

// This will throw IllegalStateException due to @AfterMapping validation
Transaction result = mapper.dtoToApiModel(someDto);
```

## 🧪 Testing Strategy

The implementation includes comprehensive tests demonstrating:

1. **Basic mapping functionality**
2. **Null value handling**
3. **Validation conditions**
4. **Bulk operations**
5. **Update operations**
6. **Error scenarios**
7. **Default value generation**
8. **Enum mapping edge cases**

## 🚀 Best Practices Demonstrated

1. **Consistent Naming**: All methods follow clear naming conventions
2. **Documentation**: Comprehensive JavaDoc for all methods
3. **Error Handling**: Proper exception handling and validation
4. **Separation of Concerns**: Each method has a single responsibility
5. **Configuration Management**: Centralized mapper configuration
6. **Testing**: Comprehensive test coverage
7. **Performance**: Optimized for production use

This implementation showcases MapStruct's evolution into a mature, feature-rich mapping framework capable of handling complex enterprise scenarios while maintaining simplicity and performance.
