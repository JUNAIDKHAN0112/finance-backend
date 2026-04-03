package com.zorvyn.finance.repository;

import com.zorvyn.finance.entity.Transaction;
import com.zorvyn.finance.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @Query(value = """
    SELECT * FROM transactions t
    WHERE t.is_deleted = false
    AND (
        LOWER(t.category) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(t.notes) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(t.type) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
    ORDER BY t.created_at DESC
""", nativeQuery = true,
            countQuery = """
    SELECT COUNT(*) FROM transactions t
    WHERE t.is_deleted = false
    AND (
        LOWER(t.category) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(t.notes) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(t.type) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
""")
    Page<Transaction> searchTransactions(
            @Param("keyword") String keyword,
            Pageable pageable
    );
    // Single transaction (not deleted)
    Optional<Transaction> findByIdAndIsDeletedFalse(UUID id);

    // Filtered + paginated list
    @Query(value = """
    SELECT * FROM transactions t
    WHERE t.is_deleted = false
    AND (CAST(:type AS VARCHAR) IS NULL OR t.type = CAST(:type AS VARCHAR))
    AND (CAST(:category AS VARCHAR) IS NULL OR LOWER(t.category) = LOWER(CAST(:category AS VARCHAR)))
    AND (CAST(:from AS DATE) IS NULL OR t.date >= CAST(:from AS DATE))
    AND (CAST(:to AS DATE) IS NULL OR t.date <= CAST(:to AS DATE))
    ORDER BY t.created_at DESC
""", nativeQuery = true,
            countQuery = """
    SELECT COUNT(*) FROM transactions t
    WHERE t.is_deleted = false
    AND (CAST(:type AS VARCHAR) IS NULL OR t.type = CAST(:type AS VARCHAR))
    AND (CAST(:category AS VARCHAR) IS NULL OR LOWER(t.category) = LOWER(CAST(:category AS VARCHAR)))
    AND (CAST(:from AS DATE) IS NULL OR t.date >= CAST(:from AS DATE))
    AND (CAST(:to AS DATE) IS NULL OR t.date <= CAST(:to AS DATE))
""")
    Page<Transaction> findAllWithFilters(
            @Param("type") String type,
            @Param("category") String category,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            Pageable pageable
    );

    // Dashboard — total income
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.isDeleted = false AND t.type = 'INCOME'")
    BigDecimal getTotalIncome();

    // Dashboard — total expense
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.isDeleted = false AND t.type = 'EXPENSE'")
    BigDecimal getTotalExpense();

    // Dashboard — category breakdown
    @Query("SELECT t.category, SUM(t.amount) FROM Transaction t " +
            "WHERE t.isDeleted = false GROUP BY t.category")
    List<Object[]> getCategoryBreakdown();

    // Dashboard — monthly trend (last 6 months)
    @Query(value = """
        SELECT TO_CHAR(date, 'YYYY-MM') as month,
               type,
               SUM(amount) as total
        FROM transactions
        WHERE is_deleted = false
        AND date >= CURRENT_DATE - INTERVAL '6 months'
        GROUP BY TO_CHAR(date, 'YYYY-MM'), type
        ORDER BY month ASC
    """, nativeQuery = true)
    List<Object[]> getMonthlyTrend();

    // Dashboard — weekly trend (last 7 days)
    @Query(value = """
        SELECT TO_CHAR(date, 'YYYY-MM-DD') as day,
               type,
               SUM(amount) as total
        FROM transactions
        WHERE is_deleted = false
        AND date >= CURRENT_DATE - INTERVAL '7 days'
        GROUP BY TO_CHAR(date, 'YYYY-MM-DD'), type
        ORDER BY day ASC
    """, nativeQuery = true)
    List<Object[]> getWeeklyTrend();

    // Dashboard — recent 10
    List<Transaction> findTop10ByIsDeletedFalseOrderByCreatedAtDesc();
}