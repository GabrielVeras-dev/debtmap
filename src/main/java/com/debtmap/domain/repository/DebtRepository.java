package com.debtmap.domain.repository;

import com.debtmap.domain.entity.Debt;
import com.debtmap.domain.enums.DebtStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DebtRepository extends JpaRepository<Debt, UUID> {

    // lista todas as dívidas de um usuário
    List<Debt> findByUserId(UUID userId);

    // busca dívida por id garantindo que pertence ao usuário
    Optional<Debt> findByIdAndUserId(UUID id, UUID userId);

    // filtra dívidas por status (OPEN, PARTIAL, PAID, OVERDUE)
    List<Debt> findByUserIdAndStatus(UUID userId, DebtStatus status);
}