package com.debtmap.domain.repository;

import com.debtmap.domain.entity.Installment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface InstallmentRepository extends JpaRepository<Installment, UUID> {

    // lista todas as parcelas de uma dívida ordenadas pelo número
    List<Installment> findByDebtIdOrderByInstallmentNumberAsc(UUID debtId);

    // lista parcelas não pagas de uma dívida
    List<Installment> findByDebtIdAndPaidFalseOrderByDueDateAsc(UUID debtId);

    // lista parcelas vencidas e não pagas — usado para atualizar status da dívida
    List<Installment> findByPaidFalseAndDueDateBefore(LocalDate date);
}