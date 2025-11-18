package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.model.dto.request.LoginRequest;
import com.example.demo.model.dto.request.RegisterRequest;
import com.example.demo.model.dto.response.AuthResponse;
import com.example.demo.model.dto.response.UserResponse;
import com.example.demo.model.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * Realiza login de um usuário
     * @param request dados de login (email e senha)
     * @return resposta com token e dados do usuário
     * @throws UnauthorizedException se email ou senha inválidos
     */
    public AuthResponse login(LoginRequest request) {
        log.info("Tentativa de login para email: {}", request.getEmail());

        // Busca usuário ativo por email
        User user = userRepository.findByEmailAndActive(request.getEmail(), true)
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado ou inativo: {}", request.getEmail());
                    return new UnauthorizedException("Email ou senha inválidos");
                });

        // Valida senha
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Senha inválida para email: {}", request.getEmail());
            throw new UnauthorizedException("Email ou senha inválidos");
        }

        log.info("Login bem-sucedido para email: {}", request.getEmail());

        // Gera token JWT
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());

        // Retorna resposta com dados do usuário e token
        return AuthResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationTimeInSeconds())
                .build();
    }

    /**
     * Registra um novo usuário
     * @param request dados de registro (nome, email, senha)
     * @return resposta com token e dados do usuário
     * @throws IllegalArgumentException se email já existe ou senhas não conferem
     */
    public AuthResponse register(RegisterRequest request) {
        log.info("Tentativa de registro para email: {}", request.getEmail());

        // Valida se as senhas conferem
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            log.warn("Senhas não conferem para email: {}", request.getEmail());
            throw new IllegalArgumentException("As senhas não conferem");
        }

        // Valida se email já existe
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email já existe: {}", request.getEmail());
            throw new IllegalArgumentException("Email já está registrado");
        }

        // Cria novo usuário
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .active(true)
                .build();

        // Salva no banco
        user = userRepository.save(user);
        log.info("Novo usuário registrado com email: {}", request.getEmail());

        // Gera token JWT
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());

        // Retorna resposta com dados do usuário e token
        return AuthResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationTimeInSeconds())
                .build();
    }

    /**
     * Busca dados de um usuário por ID
     * @param userId o ID do usuário
     * @return dados do usuário
     * @throws ResourceNotFoundException se usuário não encontrado
     */
    public UserResponse getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
