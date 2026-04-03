package com.zorvyn.finance.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zorvyn.finance.common.exception.ResourceNotFoundException;
import com.zorvyn.finance.dto.request.TransactionRequest;
import com.zorvyn.finance.dto.response.TransactionResponse;
import com.zorvyn.finance.entity.IdempotencyRecord;
import com.zorvyn.finance.entity.Transaction;
import com.zorvyn.finance.entity.User;
import com.zorvyn.finance.enums.TransactionType;
import com.zorvyn.finance.repository.IdempotencyRepository;
import com.zorvyn.finance.repository.TransactionRepository;
import com.zorvyn.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final IdempotencyRepository idempotencyRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public TransactionResponse create(TransactionRequest request,
                                      String email,
                                      String idempotencyKey) {
        // Check idempotency key
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            var existing = idempotencyRepository.findById(idempotencyKey);
            if (existing.isPresent()) {
                try {
                    return objectMapper.readValue(
                            existing.get().getResponseBody(),
                            TransactionResponse.class);
                } catch (Exception e) {
                    throw new RuntimeException("Idempotency response parse error");
                }
            }
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .date(request.getDate())
                .notes(request.getNotes())
                .isDeleted(false)
                .createdBy(user)
                .build();

        TransactionResponse response = TransactionResponse.from(
                transactionRepository.save(transaction));

        // Save idempotency record
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            try {
                idempotencyRepository.save(IdempotencyRecord.builder()
                        .idempotencyKey(idempotencyKey)
                        .responseBody(objectMapper.writeValueAsString(response))
                        .createdAt(LocalDateTime.now())
                        .build());
            } catch (Exception e) {
                throw new RuntimeException("Idempotency save error");
            }
        }

        return response;
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getAll(
            TransactionType type,
            String category,
            LocalDate from,
            LocalDate to,
            Pageable pageable) {

        return transactionRepository
                .findAllWithFilters(
                        type != null ? type.name() : null,
                        category,
                        from,
                        to,
                        pageable)
                .map(TransactionResponse::from);
    }

    @Transactional(readOnly = true)
    public TransactionResponse getById(UUID id) {
        Transaction transaction = transactionRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transaction not found with id: " + id));
        return TransactionResponse.from(transaction);
    }

    @Transactional
    public TransactionResponse update(UUID id, TransactionRequest request) {
        Transaction transaction = transactionRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transaction not found with id: " + id));

        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setCategory(request.getCategory());
        transaction.setDate(request.getDate());
        transaction.setNotes(request.getNotes());

        return TransactionResponse.from(transactionRepository.save(transaction));
    }

    @Transactional
    public void softDelete(UUID id) {
        Transaction transaction = transactionRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transaction not found with id: " + id));
        transaction.setIsDeleted(true);
        transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> search(String keyword, Pageable pageable) {
        return transactionRepository
                .searchTransactions(keyword, pageable)
                .map(TransactionResponse::from);
    }
}