package com.zorvyn.finance.service;

import com.zorvyn.finance.dto.response.TransactionResponse;
import com.zorvyn.finance.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;
    @Transactional(readOnly = true)
    public Map<String, Object> getSummary() {
        BigDecimal totalIncome = transactionRepository.getTotalIncome();
        BigDecimal totalExpense = transactionRepository.getTotalExpense();
        BigDecimal netBalance = totalIncome.subtract(totalExpense);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalIncome", totalIncome);
        summary.put("totalExpense", totalExpense);
        summary.put("netBalance", netBalance);
        return summary;
    }
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCategoryBreakdown() {
        List<Object[]> raw = transactionRepository.getCategoryBreakdown();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : raw) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("category", row[0]);
            entry.put("total", row[1]);
            result.add(entry);
        }
        return result;
    }
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMonthlyTrend() {
        List<Object[]> raw = transactionRepository.getMonthlyTrend();
        return mapTrendData(raw);
    }
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getWeeklyTrend() {
        List<Object[]> raw = transactionRepository.getWeeklyTrend();
        return mapTrendData(raw);
    }
    @Transactional(readOnly = true)
    public List<TransactionResponse> getRecentActivity() {
        return transactionRepository
                .findTop10ByIsDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .map(TransactionResponse::from)
                .toList();
    }
    @Transactional(readOnly = true)
    private List<Map<String, Object>> mapTrendData(List<Object[]> raw) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : raw) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("period", row[0]);
            entry.put("type", row[1]);
            entry.put("total", row[2]);
            result.add(entry);
        }
        return result;
    }
}