package com.example.app_na_esquina.dto.auth;

import com.example.app_na_esquina.model.enums.PerfilUsuario;


public class LoginResponseDTO {
    private String token;
    private Long idUsuario;
    private String nome;
    private PerfilUsuario perfil;
    private Long idMercadinho;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String token, Long idUsuario, String nome, PerfilUsuario perfil, Long idMercadinho) {
        this.token = token;
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.perfil = perfil;
        this.idMercadinho = idMercadinho;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }

    public Long getIdMercadinho() {
        return idMercadinho;
    }

    public void setIdMercadinho(Long idMercadinho) {
        this.idMercadinho = idMercadinho;
    }
}