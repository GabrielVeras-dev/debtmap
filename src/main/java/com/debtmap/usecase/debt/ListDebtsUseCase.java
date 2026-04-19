package com.debtmap.usecase.debt;

import com.debtmap.domain.repository.DebtRepository;
import com.debtmap.shared.dto.response.DebtResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListDebtsUseCase {

    private final DebtRepository debtRepository;
    private final GetDebtUseCase getDebtUseCase;

    @Transactional(readOnly = true)
    public List<DebtResponse> execute(UUID userId) {
        return debtRepository.findByUserId(userId)
                .stream()
                .map(getDebtUseCase::buildResponse)
                .toList();
    }
}