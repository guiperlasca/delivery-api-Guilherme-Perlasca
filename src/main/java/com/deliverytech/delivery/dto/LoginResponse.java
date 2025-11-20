package com.deliverytech.delivery.dto;

public class LoginResponse {
    private String token;
    private String tipo = "Bearer";
    private String nome;
    private String email;
    private String role;

    public LoginResponse(String token, String nome, String email, String role) {
        this.token = token;
        this.nome = nome;
        this.email = email;
        this.role = role;
    }

    // Getters e Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}