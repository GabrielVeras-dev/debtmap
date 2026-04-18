package com.debtmap.domain.repository;

import com.debtmap.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    // lista todas as categorias de um usuário
    List<Category> findByUserId(UUID userId);

    // verifica se já existe uma categoria com o mesmo nome para o usuário
    boolean existsByUserIdAndName(UUID userId, String name);

    // busca categoria por id garantindo que pertence ao usuário
    Optional<Category> findByIdAndUserId(UUID id, UUID userId);
}