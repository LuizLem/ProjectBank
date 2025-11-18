package com.example.demo.repository;

import com.example.demo.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Busca um usuário por email
     * @param email o email do usuário
     * @return Optional contendo o usuário ou vazio se não encontrado
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca um usuário ativo por email
     * @param email o email do usuário
     * @param active status do usuário (true = ativo)
     * @return Optional contendo o usuário ou vazio se não encontrado
     */
    Optional<User> findByEmailAndActive(String email, Boolean active);

    /**
     * Verifica se um email já existe no banco
     * @param email o email a verificar
     * @return true se existe, false caso contrário
     */
    boolean existsByEmail(String email);
}
