package com.deliverytech.delivery.service;

import com.deliverytech.delivery.dto.RegisterRequest;
import com.deliverytech.delivery.entity.Usuario;
import com.deliverytech.delivery.exception.ConflictException;
import com.deliverytech.delivery.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }

    // Método para registrar novo usuário
    public Usuario registrar(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email já cadastrado no sistema");
        }

        Usuario usuario = modelMapper.map(request, Usuario.class);
        // Criptografar a senha antes de salvar
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));

        return usuarioRepository.save(usuario);
    }
}