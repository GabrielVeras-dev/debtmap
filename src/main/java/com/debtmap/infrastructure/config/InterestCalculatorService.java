package com.debtmap.infrastructure.config;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Service
public class InterestCalculatorService {

    private static final MathContext MATH_CONTEXT = new MathContext(10, RoundingMode.HALF_UP);
    private static final int SCALE = 2;

    /**
     * Calcula o valor da parcela usando juros compostos (Price/SAC).
     * Fórmula: PMT = PV * [i * (1+i)^n] / [(1+i)^n - 1]
     *
     * @param principal valor principal da dívida
     * @param monthlyRate taxa de juros mensal em decimal (ex: 0.025)
     * @param installments número de parcelas
     * @return valor de cada parcela
     */
    public BigDecimal calculateInstallmentAmount(
            BigDecimal principal,
            BigDecimal monthlyRate,
            int installments
    ) {
        // sem juros — divisão simples
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(
                    BigDecimal.valueOf(installments),
                    SCALE,
                    RoundingMode.HALF_UP
            );
        }

        // (1 + i)^n
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal power = onePlusRate.pow(installments, MATH_CONTEXT);

        // i * (1+i)^n
        BigDecimal numerator = monthlyRate.multiply(power, MATH_CONTEXT);

        // (1+i)^n - 1
        BigDecimal denominator = power.subtract(BigDecimal.ONE);

        // PMT = PV * numerator / denominator
        return principal
                .multiply(numerator, MATH_CONTEXT)
                .divide(denominator, SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Calcula o valor total da dívida com juros.
     */
    public BigDecimal calculateTotalAmount(
            BigDecimal installmentAmount,
            int installments
    ) {
        return installmentAmount
                .multiply(BigDecimal.valueOf(installments))
                .setScale(SCALE, RoundingMode.HALF_UP);
    }
}