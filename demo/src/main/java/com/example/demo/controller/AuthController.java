package com.example.demo.controller;

import com.example.demo.model.dto.request.LoginRequest;
import com.example.demo.model.dto.request.RegisterRequest;
import com.example.demo.model.dto.response.AuthResponse;
import com.example.demo.model.dto.response.UserResponse;
import com.example.demo.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Endpoint para login de usuário
     * POST /api/auth/login
     * @param request dados de login (email e senha)
     * @return token JWT e dados do usuário
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Recebida requisição de login para email: {}", request.getEmail());
        
        AuthResponse response = authService.login(request);
        
        log.info("Login bem-sucedido para email: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para registro de novo usuário
     * POST /api/auth/register
     * @param request dados de registro (nome, email, senha)
     * @return token JWT e dados do usuário
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Recebida requisição de registro para email: {}", request.getEmail());
        
        AuthResponse response = authService.register(request);
        
        log.info("Usuário registrado com sucesso. Email: {}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint para buscar dados de um usuário
     * GET /api/auth/users/{id}
     * @param id o ID do usuário
     * @return dados do usuário
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        log.info("Recebida requisição para buscar usuário com ID: {}", id);
        
        UserResponse response = authService.getUserById(id);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de health check
     * GET /api/auth/health
     * @return status da aplicação
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth service is running");
    }
}
