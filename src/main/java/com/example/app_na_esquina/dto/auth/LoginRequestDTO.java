package com.example.app_na_esquina.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Locale;

public class LoginRequestDTO {

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    private String email;

    // No login só exigimos que a senha não esteja vazia. As regras de tamanho/complexidade
    // valem no CADASTRO; aqui, quem decide se a senha está certa é o BCrypt.
    // (Assim também não revelamos a regra de senha para quem está só tentando adivinhar.)
    @NotBlank(message = "A senha é obrigatória")
    private String senha;

    public LoginRequestDTO() {
    }

    public LoginRequestDTO(String email, String senha) {
        setEmail(email);
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    // Normaliza: tira espaços das pontas e deixa minúsculo ("  Ana@Mail.com " -> "ana@mail.com")
    public void setEmail(String email) {
        this.email = (email == null) ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
