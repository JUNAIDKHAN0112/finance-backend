package com.zorvyn.finance.reponsitory;

import com.zorvyn.finance.entity.Transaction;
import com.zorvyn.finance.entity.User;
import com.zorvyn.finance.enums.Role;
import com.zorvyn.finance.enums.TransactionType;
import com.zorvyn.finance.repository.TransactionRepository;
import com.zorvyn.finance.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired private TransactionRepository transactionRepository;
    @Autowired private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(User.builder()
                .name("Test User")
                .email("test@test.com")
                .password("password")
                .role(Role.ADMIN)
                .isActive(true)
                .build());

        transactionRepository.save(Transaction.builder()
                .amount(BigDecimal.valueOf(50000))
                .type(TransactionType.INCOME)
                .category("Salary")
                .date(LocalDate.now())
                .isDeleted(false)
                .createdBy(testUser)
                .build());

        transactionRepository.save(Transaction.builder()
                .amount(BigDecimal.valueOf(10000))
                .type(TransactionType.EXPENSE)
                .category("Rent")
                .date(LocalDate.now())
                .isDeleted(false)
                .createdBy(testUser)
                .build());
    }

    @Test
    void getTotalIncome_ReturnsCorrectSum() {
        BigDecimal total = transactionRepository.getTotalIncome();
        assertEquals(0, BigDecimal.valueOf(50000).compareTo(total));
    }

    @Test
    void getTotalExpense_ReturnsCorrectSum() {
        BigDecimal total = transactionRepository.getTotalExpense();
        assertEquals(0, BigDecimal.valueOf(10000).compareTo(total));
    }

    @Test
    void findAllWithFilters_ByType_ReturnsCorrect() {
        Page<Transaction> result = transactionRepository.findAllWithFilters(
                "INCOME", null, null, null, PageRequest.of(0, 10));
        assertEquals(1, result.getTotalElements());
        assertEquals(TransactionType.INCOME, result.getContent().get(0).getType());
    }

    @Test
    void findByIdAndIsDeletedFalse_DeletedTransaction_ReturnsEmpty() {
        Transaction t = transactionRepository.save(Transaction.builder()
                .amount(BigDecimal.valueOf(5000))
                .type(TransactionType.EXPENSE)
                .category("Food")
                .date(LocalDate.now())
                .isDeleted(true)
                .createdBy(testUser)
                .build());

        assertTrue(transactionRepository
                .findByIdAndIsDeletedFalse(t.getId()).isEmpty());
    }
}