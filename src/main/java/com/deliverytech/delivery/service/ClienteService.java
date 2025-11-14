package com.deliverytech.delivery.service;

import com.deliverytech.delivery.dto.ClienteRequestDTO;
import com.deliverytech.delivery.entity.Cliente;
import com.deliverytech.delivery.exception.ConflictException; // Importar
import com.deliverytech.delivery.exception.EntityNotFoundException; // Importar
import com.deliverytech.delivery.repository.ClienteRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Cliente cadastrar(ClienteRequestDTO clienteDTO) {
        // Validação: email único (agora lança 409)
        if (clienteRepository.existsByEmail(clienteDTO.getEmail())) {
            throw new ConflictException("Email já cadastrado: " + clienteDTO.getEmail());
        }

        Cliente cliente = modelMapper.map(clienteDTO, Cliente.class);
        return clienteRepository.save(cliente);
    }

    public List<Cliente> buscarTodos() {
        return clienteRepository.findByAtivoTrue();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado: " + id));
    }

    public Cliente buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com este email"));
    }

    public List<Cliente> buscarPorNome(String nome) {
        return clienteRepository.findByNomeContainingIgnoreCase(nome);
    }

    public Cliente atualizar(Long id, ClienteRequestDTO clienteAtualizadoDTO) {
        // buscarPorId já trata o 404
        Cliente cliente = buscarPorId(id);

        // Verificar se o email está sendo alterado para um já existente
        if (!cliente.getEmail().equals(clienteAtualizadoDTO.getEmail())) {
            if (clienteRepository.existsByEmail(clienteAtualizadoDTO.getEmail())) {
                throw new ConflictException("Email já cadastrado: " + clienteAtualizadoDTO.getEmail());
            }
        }

        modelMapper.map(clienteAtualizadoDTO, cliente);
        return clienteRepository.save(cliente);
    }

    public void inativar(Long id) {
        Cliente cliente = buscarPorId(id); // Garante 404 se não existir
        cliente.setAtivo(false);
        clienteRepository.save(cliente);
    }

    public void reativar(Long id) {
        Cliente cliente = buscarPorId(id); // Garante 404 se não existir
        cliente.setAtivo(true);
        clienteRepository.save(cliente);
    }

    public Long contarAtivos() {
        return clienteRepository.contarClientesAtivos();
    }

}