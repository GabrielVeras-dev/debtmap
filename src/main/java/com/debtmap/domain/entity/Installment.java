package com.debtmap.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "installments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Installment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // dívida à qual esta parcela pertence
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debt_id", nullable = false)
    private Debt debt;

    // número sequencial da parcela (1, 2, 3...)
    @Column(name = "installment_number", nullable = false)
    private Integer installmentNumber;

    // data de vencimento da parcela
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    // valor da parcela já com juros calculados
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    // indica se a parcela foi paga
    @Column(nullable = false)
    private Boolean paid;

    // data e hora em que o pagamento foi registrado
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    // valor efetivamente pago (pode diferir do amount)
    @Column(name = "paid_amount", precision = 15, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (paid == null) paid = false;
    }
}