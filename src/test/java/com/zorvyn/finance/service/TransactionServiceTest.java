package com.zorvyn.finance.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zorvyn.finance.common.exception.ResourceNotFoundException;
import com.zorvyn.finance.dto.request.TransactionRequest;
import com.zorvyn.finance.dto.response.TransactionResponse;
import com.zorvyn.finance.entity.Transaction;
import com.zorvyn.finance.entity.User;
import com.zorvyn.finance.enums.Role;
import com.zorvyn.finance.enums.TransactionType;
import com.zorvyn.finance.repository.IdempotencyRepository;
import com.zorvyn.finance.repository.TransactionRepository;
import com.zorvyn.finance.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private IdempotencyRepository idempotencyRepository;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private TransactionService transactionService;

    private User buildUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .email("admin@test.com")
                .role(Role.ADMIN)
                .isActive(true)
                .build();
    }

    private Transaction buildTransaction(User user) {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .amount(BigDecimal.valueOf(50000))
                .type(TransactionType.INCOME)
                .category("Salary")
                .date(LocalDate.now())
                .notes("Test")
                .isDeleted(false)
                .createdBy(user)
                .build();
    }

    @Test
    void create_Success_NoIdempotencyKey() {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(BigDecimal.valueOf(50000));
        request.setType(TransactionType.INCOME);
        request.setCategory("Salary");
        request.setDate(LocalDate.now());

        User user = buildUser();
        Transaction saved = buildTransaction(user);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(transactionRepository.save(any())).thenReturn(saved);

        TransactionResponse response = transactionService.create(
                request, "admin@test.com", null);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(50000), response.getAmount());
        assertEquals("INCOME", response.getType());
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void create_WithIdempotencyKey_DuplicateRequest_ReturnsCached() throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(BigDecimal.valueOf(50000));
        request.setType(TransactionType.INCOME);
        request.setCategory("Salary");
        request.setDate(LocalDate.now());

        String idempotencyKey = "test-key-123";
        TransactionResponse cachedResponse = TransactionResponse.builder()
                .id(UUID.randomUUID())
                .amount(BigDecimal.valueOf(50000))
                .type("INCOME")
                .category("Salary")
                .date(LocalDate.now())
                .build();

        com.zorvyn.finance.entity.IdempotencyRecord record =
                com.zorvyn.finance.entity.IdempotencyRecord.builder()
                        .idempotencyKey(idempotencyKey)
                        .responseBody("{}")
                        .createdAt(java.time.LocalDateTime.now())
                        .build();

        when(idempotencyRepository.findById(idempotencyKey))
                .thenReturn(Optional.of(record));
        when(objectMapper.readValue(any(String.class),
                eq(TransactionResponse.class))).thenReturn(cachedResponse);

        TransactionResponse response = transactionService.create(
                request, "admin@test.com", idempotencyKey);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(50000), response.getAmount());
        verify(transactionRepository, never()).save(any());
    }
}