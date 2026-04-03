package com.zorvyn.finance.dto.response;

import com.zorvyn.finance.entity.Transaction;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TransactionResponse {
    private UUID id;
    private BigDecimal amount;
    private String type;
    private String category;
    private LocalDate date;
    private String notes;
    private String createdBy;
    private LocalDateTime createdAt;

    public static TransactionResponse from(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .amount(t.getAmount())
                .type(t.getType().name())
                .category(t.getCategory())
                .date(t.getDate())
                .notes(t.getNotes())
                .createdBy(t.getCreatedBy() != null
                        ? t.getCreatedBy().getName() : null)
                .createdAt(t.getCreatedAt())
                .build();
    }
}