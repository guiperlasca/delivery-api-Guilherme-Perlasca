package com.deliverytech.delivery.service;

import com.deliverytech.delivery.dto.ClienteRequestDTO; // NOVO
import com.deliverytech.delivery.entity.Cliente;
import com.deliverytech.delivery.repository.ClienteRepository;
import org.modelmapper.ModelMapper; // NOVO
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

    // Cadastrar novo cliente
    public Cliente cadastrar(ClienteRequestDTO clienteDTO) {
        // Validação: email único
        if (clienteRepository.existsByEmail(clienteDTO.getEmail())) {
            throw new RuntimeException("Email já cadastrado: " + clienteDTO.getEmail());
        }

        // Mapeia DTO para Entidade
        Cliente cliente = modelMapper.map(clienteDTO, Cliente.class);

        // Validações básicas
        validarCliente(cliente);

        return clienteRepository.save(cliente);
    }

    // Buscar todos os clientes ativos
    public List<Cliente> buscarTodos() {
        return clienteRepository.findByAtivoTrue();
    }

    // Buscar cliente por ID
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    // Buscar por email
    public Optional<Cliente> buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    // Buscar por nome (contém)
    public List<Cliente> buscarPorNome(String nome) {
        return clienteRepository.findByNomeContainingIgnoreCase(nome);
    }

    // Atualizar cliente
    public Cliente atualizar(Long id, ClienteRequestDTO clienteAtualizadoDTO) { // MODIFICADO
        Optional<Cliente> clienteExistente = clienteRepository.findById(id);

        if (clienteExistente.isEmpty()) {
            throw new RuntimeException("Cliente não encontrado: "+ id);
        }

        Cliente cliente = clienteExistente.get();

        // Verificar se email não está sendo usado por outro cliente
        if (!cliente.getEmail().equals(clienteAtualizadoDTO.getEmail())) {
            if (clienteRepository.existsByEmail(clienteAtualizadoDTO.getEmail())) {
                throw new RuntimeException("Email já cadastrado: " + clienteAtualizadoDTO.getEmail());
            }
        }

        // Atualizar campos (Mapeia DTO para a entidade existente)
        modelMapper.map(clienteAtualizadoDTO, cliente);
        validarCliente(cliente);

        return clienteRepository.save(cliente);
    }

    // Inativar cliente (soft delete)
    public void inativar(Long id) {
        // ... (método inalterado)
        Optional<Cliente> cliente = clienteRepository.findById(id);

        if (cliente.isEmpty()) {
            throw new RuntimeException("Cliente não encontrado: " + id);
        }

        Cliente clienteEntity = cliente.get();
        clienteEntity.setAtivo(false);
        clienteRepository.save(clienteEntity);
    }

    // Reativar cliente
    public void reativar(Long id) {
        Optional<Cliente> cliente = clienteRepository.findById(id);

        if (cliente.isEmpty()) {
            throw new RuntimeException("Cliente não encontrado: " + id);
        }

        Cliente clienteEntity = cliente.get();
        clienteEntity.setAtivo(true);
        clienteRepository.save(clienteEntity);
    }

    // Contar clientes ativos
    public Long contarAtivos() {
        return clienteRepository.contarClientesAtivos();
    }

    // Validações privadas
    private void validarCliente(Cliente cliente) {
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new RuntimeException("Nome é obrigatório");
        }

        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email é obrigatório");
        }

        if (!cliente.getEmail().contains("@")) {
            throw new RuntimeException("Email inválido");
        }

        if (cliente.getTelefone() == null || cliente.getTelefone().trim().isEmpty()) {
            throw new RuntimeException("Telefone é obrigatório");
        }
    }
}