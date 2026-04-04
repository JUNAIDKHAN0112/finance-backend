package com.zorvyn.finance.controller;

import com.zorvyn.finance.common.response.ApiResponse;
import com.zorvyn.finance.service.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

//
@Tag(name = "Dashboard", description = "Summary Analytics and Trend APIs")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSummary() {
        return ResponseEntity.ok(
                ApiResponse.success("Summary fetched successfully", dashboardService.getSummary()));
    }

    @GetMapping("/category-breakdown")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getCategoryBreakdown() {
        return ResponseEntity.ok(
                ApiResponse.success("Category breakdown fetched", dashboardService.getCategoryBreakdown()));
    }

    @GetMapping("/monthly-trend")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getMonthlyTrend() {
        return ResponseEntity.ok(
                ApiResponse.success("Monthly trend fetched", dashboardService.getMonthlyTrend()));
    }

    @GetMapping("/weekly-trend")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getWeeklyTrend() {
        return ResponseEntity.ok(
                ApiResponse.success("Weekly trend fetched", dashboardService.getWeeklyTrend()));
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<?>> getRecentActivity() {
        return ResponseEntity.ok(
                ApiResponse.success("Recent activity fetched", dashboardService.getRecentActivity()));
    }
}