package com.finance.tracker.mapper;

import com.finance.tracker.dto.TransactionDto;
import com.finance.tracker.entity.TransactionEntity;
import com.finance.tracker.entity.TransactionType;
import com.finance.tracker.model.Transaction;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive test class demonstrating advanced MapStruct features in TransactionMapper
 */
@SpringBootTest
class TransactionMapperTest {

    private final TransactionMapper mapper = Mappers.getMapper(TransactionMapper.class);

    @Test
    void testApiModelToDto_WithValidData_ShouldMapCorrectly() {
        // Given
        Transaction apiModel = new Transaction();
        apiModel.setId(UUID.randomUUID());
        apiModel.setAmount(100.50);
        apiModel.setType(Transaction.TypeEnum.INCOME);
        apiModel.setCategory("Salary");
        apiModel.setDescription("Monthly salary");
        apiModel.setDateTime(OffsetDateTime.now().minusDays(1));

        // When
        TransactionDto dto = mapper.apiModelToDto(apiModel);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(apiModel.getId());
        assertThat(dto.getAmount()).isEqualTo(apiModel.getAmount());
        assertThat(dto.getType()).isEqualTo(TransactionDto.TransactionTypeDto.INCOME);
        assertThat(dto.getCategory()).isEqualTo(apiModel.getCategory());
        assertThat(dto.getDescription()).isEqualTo(apiModel.getDescription());
        assertThat(dto.getDateTime()).isEqualTo(apiModel.getDateTime().toLocalDateTime());
    }

    @Test
    void testApiModelToDto_WithNullAmount_ShouldSkipMapping() {
        // Given
        Transaction apiModel = new Transaction();
        apiModel.setId(UUID.randomUUID());
        apiModel.setAmount(null); // This should fail validation
        apiModel.setType(Transaction.TypeEnum.EXPENSE);

        // When
        TransactionDto dto = mapper.apiModelToDto(apiModel);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getAmount()).isNull(); // Amount should be null due to condition
    }

    @Test
    void testApiModelToDto_WithNegativeAmount_ShouldSkipMapping() {
        // Given
        Transaction apiModel = new Transaction();
        apiModel.setId(UUID.randomUUID());
        apiModel.setAmount(-50.0); // This should fail validation
        apiModel.setType(Transaction.TypeEnum.EXPENSE);

        // When
        TransactionDto dto = mapper.apiModelToDto(apiModel);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getAmount()).isNull(); // Amount should be null due to condition
    }

    @Test
    void testDtoToApiModel_ShouldMapCorrectly() {
        // Given
        TransactionDto dto = TransactionDto.builder()
                .id(UUID.randomUUID())
                .amount(75.25)
                .type(TransactionDto.TransactionTypeDto.EXPENSE)
                .category("Food")
                .description("Grocery shopping")
                .dateTime(LocalDateTime.now().minusHours(2))
                .build();

        // When
        Transaction apiModel = mapper.dtoToApiModel(dto);

        // Then
        assertThat(apiModel).isNotNull();
        assertThat(apiModel.getId()).isEqualTo(dto.getId());
        assertThat(apiModel.getAmount()).isEqualTo(dto.getAmount());
        assertThat(apiModel.getType()).isEqualTo(Transaction.TypeEnum.EXPENSE);
        assertThat(apiModel.getCategory()).isEqualTo(dto.getCategory());
        assertThat(apiModel.getDescription()).isEqualTo(dto.getDescription());
        assertThat(apiModel.getDateTime()).isEqualTo(dto.getDateTime().atOffset(ZoneOffset.UTC));
    }

    @Test
    void testDtoToEntity_WithDefaults_ShouldGenerateValues() {
        // Given
        TransactionDto dto = TransactionDto.builder()
                .amount(200.0)
                .type(TransactionDto.TransactionTypeDto.INCOME)
                .category("Bonus")
                .description("Year-end bonus")
                // No ID or dateTime - should be generated
                .build();

        // When
        TransactionEntity entity = mapper.dtoToEntity(dto);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNotNull(); // Should be generated
        assertThat(entity.getAmount()).isEqualTo(dto.getAmount());
        assertThat(entity.getType()).isEqualTo(TransactionType.INCOME);
        assertThat(entity.getCategory()).isEqualTo(dto.getCategory());
        assertThat(entity.getDescription()).isEqualTo(dto.getDescription());
        assertThat(entity.getDateTime()).isNotNull(); // Should be generated
    }

    @Test
    void testEntityToDto_ShouldMapCorrectly() {
        // Given
        TransactionEntity entity = TransactionEntity.builder()
                .id(UUID.randomUUID())
                .amount(150.0)
                .type(TransactionType.EXPENSE)
                .category("Transport")
                .description("Taxi fare")
                .dateTime(LocalDateTime.now().minusMinutes(30))
                .build();

        // When
        TransactionDto dto = mapper.entityToDto(entity);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(entity.getId());
        assertThat(dto.getAmount()).isEqualTo(entity.getAmount());
        assertThat(dto.getType()).isEqualTo(TransactionDto.TransactionTypeDto.EXPENSE);
        assertThat(dto.getCategory()).isEqualTo(entity.getCategory());
        assertThat(dto.getDescription()).isEqualTo(entity.getDescription());
        assertThat(dto.getDateTime()).isEqualTo(entity.getDateTime());
    }

    @Test
    void testUpdateEntityFromDto_ShouldUpdateOnlyNonNullFields() {
        // Given
        TransactionEntity existingEntity = TransactionEntity.builder()
                .id(UUID.randomUUID())
                .amount(100.0)
                .type(TransactionType.INCOME)
                .category("Old Category")
                .description("Old Description")
                .dateTime(LocalDateTime.now().minusDays(1))
                .build();

        TransactionDto updateDto = TransactionDto.builder()
                .amount(150.0) // Update amount
                .category("New Category") // Update category
                // Leave other fields null - should not update
                .build();

        // When
        mapper.updateEntityFromDto(updateDto, existingEntity);

        // Then
        assertThat(existingEntity.getId()).isNotNull(); // Should remain unchanged
        assertThat(existingEntity.getAmount()).isEqualTo(150.0); // Should be updated
        assertThat(existingEntity.getCategory()).isEqualTo("New Category"); // Should be updated
        assertThat(existingEntity.getDescription()).isEqualTo("Old Description"); // Should remain unchanged
        assertThat(existingEntity.getType()).isEqualTo(TransactionType.INCOME); // Should remain unchanged
    }

    @Test
    void testBulkMapping_Lists_ShouldMapAllElements() {
        // Given
        List<Transaction> apiModels = Arrays.asList(
                createTestTransaction(100.0, Transaction.TypeEnum.INCOME),
                createTestTransaction(50.0, Transaction.TypeEnum.EXPENSE),
                createTestTransaction(75.0, Transaction.TypeEnum.INCOME)
        );

        // When
        List<TransactionDto> dtos = mapper.apiModelsToDtos(apiModels);

        // Then
        assertThat(dtos).hasSize(3);
        assertThat(dtos.get(0).getAmount()).isEqualTo(100.0);
        assertThat(dtos.get(0).getType()).isEqualTo(TransactionDto.TransactionTypeDto.INCOME);
        assertThat(dtos.get(1).getAmount()).isEqualTo(50.0);
        assertThat(dtos.get(1).getType()).isEqualTo(TransactionDto.TransactionTypeDto.EXPENSE);
        assertThat(dtos.get(2).getAmount()).isEqualTo(75.0);
        assertThat(dtos.get(2).getType()).isEqualTo(TransactionDto.TransactionTypeDto.INCOME);
    }

    @Test
    void testBulkMapping_Sets_ShouldMapAllElements() {
        // Given
        Set<TransactionDto> dtos = Set.of(
                createTestDto(200.0, TransactionDto.TransactionTypeDto.INCOME),
                createTestDto(80.0, TransactionDto.TransactionTypeDto.EXPENSE)
        );

        // When
        Set<TransactionEntity> entities = mapper.dtosToSetOfEntities(dtos);

        // Then
        assertThat(entities).hasSize(2);
        assertThat(entities).extracting(TransactionEntity::getAmount)
                .containsExactlyInAnyOrder(200.0, 80.0);
    }

    @Test
    void testEnumMapping_WithNullValue_ShouldReturnDefault() {
        // When
        TransactionDto.TransactionTypeDto result = mapper.mapApiEnumToDto(null);

        // Then
        assertThat(result).isEqualTo(TransactionDto.TransactionTypeDto.EXPENSE); // Default fallback
    }

    @Test
    void testEnumMapping_WithValidValues_ShouldMapCorrectly() {
        // When & Then
        assertThat(mapper.mapApiEnumToDto(Transaction.TypeEnum.INCOME))
                .isEqualTo(TransactionDto.TransactionTypeDto.INCOME);
        assertThat(mapper.mapApiEnumToDto(Transaction.TypeEnum.EXPENSE))
                .isEqualTo(TransactionDto.TransactionTypeDto.EXPENSE);
    }

    @Test
    void testDateTimeMapping_ShouldHandleTimezones() {
        // Given
        LocalDateTime localTime = LocalDateTime.of(2023, 12, 25, 15, 30);
        OffsetDateTime offsetTime = OffsetDateTime.of(2023, 12, 25, 15, 30, 0, 0, ZoneOffset.of("+02:00"));

        // When
        OffsetDateTime localToOffset = mapper.mapLocalToOffset(localTime);
        LocalDateTime offsetToLocal = mapper.mapOffsetToLocal(offsetTime);

        // Then
        assertThat(localToOffset).isEqualTo(localTime.atOffset(ZoneOffset.UTC));
        assertThat(offsetToLocal).isEqualTo(offsetTime.toLocalDateTime());
    }

    @Test
    void testValidationConditions() {
        // Test amount validation
        assertThat(mapper.isValidAmount(100.0)).isTrue();
        assertThat(mapper.isValidAmount(0.0)).isFalse();
        assertThat(mapper.isValidAmount(-50.0)).isFalse();
        assertThat(mapper.isValidAmount(null)).isFalse();

        // Test string validation
        assertThat(mapper.isNotEmpty("valid")).isTrue();
        assertThat(mapper.isNotEmpty("")).isFalse();
        assertThat(mapper.isNotEmpty("   ")).isFalse();
        assertThat(mapper.isNotEmpty(null)).isFalse();

        // Test datetime validation
        assertThat(mapper.isValidDateTime(OffsetDateTime.now().minusHours(1))).isTrue();
        assertThat(mapper.isValidDateTime(OffsetDateTime.now().plusHours(1))).isFalse();
        assertThat(mapper.isValidDateTime(null)).isFalse();
    }

    @Test
    void testObjectFactories() {
        // When
        TransactionEntity entity = mapper.createTransactionEntity();
        TransactionDto dto = mapper.createTransactionDto();

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNotNull();
        assertThat(entity.getDateTime()).isNotNull();

        assertThat(dto).isNotNull();
    }

    @Test
    void testAfterMappingValidation_WithInvalidAmount_ShouldThrowException() {
        // Given
        Transaction invalidTransaction = new Transaction();
        invalidTransaction.setAmount(-100.0); // Invalid amount

        // When & Then
        assertThatThrownBy(() -> mapper.validateApiModel(invalidTransaction))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("amount must be positive");
    }

    @Test 
    void testBeforeMappingValidation_WithNullSource_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> mapper.validateSource(null, TransactionDto.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Source object cannot be null");
    }

    // Helper methods
    private Transaction createTestTransaction(double amount, Transaction.TypeEnum type) {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setCategory("Test Category");
        transaction.setDescription("Test Description");
        transaction.setDateTime(OffsetDateTime.now());
        return transaction;
    }

    private TransactionDto createTestDto(double amount, TransactionDto.TransactionTypeDto type) {
        return TransactionDto.builder()
                .id(UUID.randomUUID())
                .amount(amount)
                .type(type)
                .category("Test Category")
                .description("Test Description")
                .dateTime(LocalDateTime.now())
                .build();
    }
}
