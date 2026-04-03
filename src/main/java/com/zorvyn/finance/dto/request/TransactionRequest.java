package com.zorvyn.finance.dto.request;

import com.zorvyn.finance.enums.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0001", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Type is required (INCOME or EXPENSE)")
    private TransactionType type;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Date is required")
    private LocalDate date;

    private String notes;
}