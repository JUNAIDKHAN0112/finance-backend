package com.zorvyn.finance.service;

import com.zorvyn.finance.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @InjectMocks private DashboardService dashboardService;

    @Test
    void getSummary_ReturnsCorrectValues() {
        when(transactionRepository.getTotalIncome())
                .thenReturn(BigDecimal.valueOf(100000));
        when(transactionRepository.getTotalExpense())
                .thenReturn(BigDecimal.valueOf(40000));

        Map<String, Object> summary = dashboardService.getSummary();

        assertEquals(BigDecimal.valueOf(100000), summary.get("totalIncome"));
        assertEquals(BigDecimal.valueOf(40000), summary.get("totalExpense"));
        assertEquals(BigDecimal.valueOf(60000), summary.get("netBalance"));
    }

    @Test
    void getSummary_ZeroTransactions_ReturnsZeroBalance() {
        when(transactionRepository.getTotalIncome())
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.getTotalExpense())
                .thenReturn(BigDecimal.ZERO);

        Map<String, Object> summary = dashboardService.getSummary();

        assertEquals(BigDecimal.ZERO, summary.get("netBalance"));
    }

    @Test
    void getCategoryBreakdown_ReturnsCorrectData() {
        Object[] row = {"Salary", BigDecimal.valueOf(50000)};
        List<Object[]> mockData = new ArrayList<>();
        mockData.add(row);

        when(transactionRepository.getCategoryBreakdown())
                .thenReturn(mockData);

        List<Map<String, Object>> result = dashboardService.getCategoryBreakdown();

        assertEquals(1, result.size());
        assertEquals("Salary", result.get(0).get("category"));
        assertEquals(BigDecimal.valueOf(50000), result.get(0).get("total"));
    }

    @Test
    void getCategoryBreakdown_Empty_ReturnsEmptyList() {
        when(transactionRepository.getCategoryBreakdown())
                .thenReturn(Collections.emptyList());

        List<Map<String, Object>> result = dashboardService.getCategoryBreakdown();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getRecentActivity_ReturnsEmptyList() {
        when(transactionRepository.findTop10ByIsDeletedFalseOrderByCreatedAtDesc())
                .thenReturn(Collections.emptyList());

        var result = dashboardService.getRecentActivity();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}