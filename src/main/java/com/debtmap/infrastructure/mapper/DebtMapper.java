package com.debtmap.infrastructure.mapper;

import com.debtmap.domain.entity.Debt;
import com.debtmap.domain.entity.Installment;
import com.debtmap.shared.dto.response.DebtResponse;
import com.debtmap.shared.dto.response.InstallmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DebtMapper {

    InstallmentResponse toInstallmentResponse(Installment installment);

    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "totalPaid", ignore = true)
    @Mapping(target = "remainingAmount", ignore = true)
    DebtResponse toResponse(Debt debt);
}