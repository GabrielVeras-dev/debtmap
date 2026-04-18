package com.debtmap.domain.repository;

import com.debtmap.domain.entity.Transaction;
import com.debtmap.domain.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    // lista transações do usuário ordenadas por data decrescente
    List<Transaction> findByUserIdOrderByTransactionDateDesc(UUID userId);

    // busca transação por id garantindo que pertence ao usuário
    Optional<Transaction> findByIdAndUserId(UUID id, UUID userId);

    // filtra por tipo (INCOME ou EXPENSE)
    List<Transaction> findByUserIdAndTypeOrderByTransactionDateDesc(UUID userId, TransactionType type);

    // soma total de um tipo de transação em um período — usado no dashboard
    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.user.id = :userId
              AND t.type = :type
              AND t.transactionDate BETWEEN :startDate AND :endDate
            """)
    BigDecimal sumAmountByUserIdAndTypeAndDateBetween(
            @Param("userId") UUID userId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}