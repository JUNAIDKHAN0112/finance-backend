package com.zorvyn.finance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zorvyn.finance.dto.request.TransactionRequest;
import com.zorvyn.finance.dto.response.TransactionResponse;
import com.zorvyn.finance.enums.TransactionType;
import com.zorvyn.finance.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean
    private TransactionService transactionService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createTransaction_AsAdmin_Returns201() throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(BigDecimal.valueOf(50000));
        request.setType(TransactionType.INCOME);
        request.setCategory("Salary");
        request.setDate(LocalDate.now());
        request.setNotes("Test");

        TransactionResponse response = TransactionResponse.builder()
                .id(UUID.randomUUID())
                .amount(BigDecimal.valueOf(50000))
                .type("INCOME")
                .category("Salary")
                .date(LocalDate.now())
                .build();

        when(transactionService.create(any(), any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/transactions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.type").value("INCOME"));
    }

    @Test
    @WithMockUser(roles = "VIEWER")
    void createTransaction_AsViewer_Returns403() throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(BigDecimal.valueOf(50000));
        request.setType(TransactionType.INCOME);
        request.setCategory("Salary");
        request.setDate(LocalDate.now());

        mockMvc.perform(post("/api/transactions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createTransaction_NegativeAmount_Returns400() throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(BigDecimal.valueOf(-100));
        request.setType(TransactionType.INCOME);
        request.setCategory("Salary");
        request.setDate(LocalDate.now());

        mockMvc.perform(post("/api/transactions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}