package com.debtmap.infrastructure.mapper;

import com.debtmap.domain.entity.Category;
import com.debtmap.shared.dto.response.CategoryResponse;
import org.mapstruct.Mapper;

@Mapper
public interface CategoryMapper {

    CategoryResponse toResponse(Category category);
}