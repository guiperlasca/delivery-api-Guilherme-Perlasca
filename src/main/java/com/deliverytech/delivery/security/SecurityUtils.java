package com.deliverytech.delivery.security;

import com.deliverytech.delivery.entity.Usuario;
import com.deliverytech.delivery.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityUtils {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Retorna o usuário logado (ou lança erro se não estiver autenticado)
    public Usuario getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        // O principal foi setado como o objeto Usuario no filtro JWT
        try {
            return (Usuario) authentication.getPrincipal();
        } catch (ClassCastException e) {
            // Fallback caso o principal seja apenas o username
            String email = authentication.getName();
            return usuarioRepository.findByEmail(email).orElse(null);
        }
    }

    // Verifica se o usuário tem ROLE_ADMIN
    public boolean isAdmin() {
        Usuario user = getCurrentUser();
        return user != null && "ADMIN".equals(user.getRole().name());
    }
}