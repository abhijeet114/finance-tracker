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
 * Test class for TransactionMapper focusing on genuine mapping scenarios
 */
@SpringBootTest
class TransactionMapperTest {

    private final TransactionMapper mapper = Mappers.getMapper(TransactionMapper.class);

    // ========================================
    // API Model ↔ DTO Mapping Tests
    // ========================================

    @Test
    void testApiModelToDto_ShouldMapAllFields() {
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
    void testApiModelToDto_WithNullValues_ShouldHandleGracefully() {
        // Given
        Transaction apiModel = new Transaction();
        apiModel.setId(UUID.randomUUID());
        apiModel.setAmount(null);
        apiModel.setType(Transaction.TypeEnum.EXPENSE);
        apiModel.setCategory(null);
        apiModel.setDescription(null);
        apiModel.setDateTime(null);

        // When
        TransactionDto dto = mapper.apiModelToDto(apiModel);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(apiModel.getId());
        assertThat(dto.getAmount()).isNull();
        assertThat(dto.getType()).isEqualTo(TransactionDto.TransactionTypeDto.EXPENSE);
        assertThat(dto.getCategory()).isNull();
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.getDateTime()).isNull();
    }

    @Test
    void testDtoToApiModel_ShouldMapAllFields() {
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

    // ========================================
    // DTO ↔ Entity Mapping Tests
    // ========================================

    @Test
    void testDtoToEntity_ShouldMapAllFields() {
        // Given
        TransactionDto dto = TransactionDto.builder()
                .id(UUID.randomUUID())
                .amount(200.0)
                .type(TransactionDto.TransactionTypeDto.INCOME)
                .category("Bonus")
                .description("Year-end bonus")
                .dateTime(LocalDateTime.now())
                .build();

        // When
        TransactionEntity entity = mapper.dtoToEntity(dto);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getAmount()).isEqualTo(dto.getAmount());
        assertThat(entity.getType()).isEqualTo(TransactionType.INCOME);
        assertThat(entity.getCategory()).isEqualTo(dto.getCategory());
        assertThat(entity.getDescription()).isEqualTo(dto.getDescription());
        assertThat(entity.getDateTime()).isEqualTo(dto.getDateTime());
    }

    @Test
    void testEntityToDto_ShouldMapAllFields() {
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

    // ========================================
    // Update Mapping Tests
    // ========================================

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

        UUID originalId = existingEntity.getId();

        // When
        mapper.updateEntityFromDto(updateDto, existingEntity);

        // Then
        assertThat(existingEntity.getId()).isEqualTo(originalId); // Should remain unchanged
        assertThat(existingEntity.getAmount()).isEqualTo(150.0); // Should be updated
        assertThat(existingEntity.getCategory()).isEqualTo("New Category"); // Should be updated
        assertThat(existingEntity.getDescription()).isEqualTo("Old Description"); // Should remain unchanged
        assertThat(existingEntity.getType()).isEqualTo(TransactionType.INCOME); // Should remain unchanged
    }

    // ========================================
    // Bulk Mapping Tests
    // ========================================

    @Test
    void testApiModelsToDtos_ShouldMapAllElements() {
        // Given
        List<Transaction> apiModels = Arrays.asList(
                createTestApiModel(100.0, Transaction.TypeEnum.INCOME),
                createTestApiModel(50.0, Transaction.TypeEnum.EXPENSE),
                createTestApiModel(75.0, Transaction.TypeEnum.INCOME)
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
    void testDtosToEntities_ShouldMapAllElements() {
        // Given
        List<TransactionDto> dtos = Arrays.asList(
                createTestDto(200.0, TransactionDto.TransactionTypeDto.INCOME),
                createTestDto(80.0, TransactionDto.TransactionTypeDto.EXPENSE)
        );

        // When
        List<TransactionEntity> entities = mapper.dtosToEntities(dtos);

        // Then
        assertThat(entities).hasSize(2);
        assertThat(entities.get(0).getAmount()).isEqualTo(200.0);
        assertThat(entities.get(0).getType()).isEqualTo(TransactionType.INCOME);
        assertThat(entities.get(1).getAmount()).isEqualTo(80.0);
        assertThat(entities.get(1).getType()).isEqualTo(TransactionType.EXPENSE);
    }

    @Test
    void testDtosToSetOfEntities_ShouldMapAllElements() {
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

    // ========================================
    // DateTime Conversion Tests
    // ========================================

    @Test
    void testDateTimeConversion_ShouldHandleTimezoneCorrectly() {
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
    void testDateTimeConversion_WithNullValues_ShouldReturnNull() {
        // When & Then
        assertThat(mapper.mapLocalToOffset(null)).isNull();
        assertThat(mapper.mapOffsetToLocal(null)).isNull();
    }

    // ========================================
    // Edge Cases
    // ========================================

    @Test
    void testMapping_WithEmptyLists_ShouldReturnEmptyLists() {
        // When
        List<TransactionDto> emptyDtos = mapper.apiModelsToDtos(Collections.emptyList());
        List<TransactionEntity> emptyEntities = mapper.dtosToEntities(Collections.emptyList());

        // Then
        assertThat(emptyDtos).isEmpty();
        assertThat(emptyEntities).isEmpty();
    }

    @Test
    void testMapping_WithNullLists_ShouldReturnNull() {
        // When & Then
        assertThat(mapper.apiModelsToDtos(null)).isNull();
        assertThat(mapper.dtosToEntities(null)).isNull();
    }

    // ========================================
    // Helper Methods
    // ========================================

    private Transaction createTestApiModel(double amount, Transaction.TypeEnum type) {
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
