package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.*;
import com.deliverytech.delivery.entity.Usuario;
import com.deliverytech.delivery.security.JwtUtil;
import com.deliverytech.delivery.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Autenticação", description = "Endpoints para login e registro de usuários")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/login")
    @Operation(summary = "Realizar login", description = "Autentica o usuário e retorna um token JWT.")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        // Autentica usando o Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getSenha())
        );

        // Se chegou aqui, a autenticação foi bem sucedida
        Usuario usuario = (Usuario) authentication.getPrincipal();
        String token = jwtUtil.generateToken(usuario);

        return ResponseEntity.ok(new LoginResponse(token, usuario.getNome(), usuario.getEmail(), usuario.getRole().name()));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário", description = "Cria uma nova conta de usuário no sistema.")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        Usuario usuarioSalvo = usuarioService.registrar(registerRequest);
        UserResponse response = modelMapper.map(usuarioSalvo, UserResponse.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Obter usuário atual", description = "Retorna os dados do usuário logado com base no token JWT.")
    public ResponseEntity<UserResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();

        UserResponse response = modelMapper.map(usuario, UserResponse.class);
        return ResponseEntity.ok(response);
    }
}