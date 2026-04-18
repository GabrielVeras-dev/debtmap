package com.debtmap.domain.repository;

import com.debtmap.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // busca usuário pelo email para autenticação
    Optional<User> findByEmail(String email);

    // verifica se já existe um usuário com o email informado
    boolean existsByEmail(String email);
}