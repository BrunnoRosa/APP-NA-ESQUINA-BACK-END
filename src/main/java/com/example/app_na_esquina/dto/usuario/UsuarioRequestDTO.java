package com.example.app_na_esquina.dto.usuario;

import com.example.app_na_esquina.model.enums.PerfilUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Locale;

public class UsuarioRequestDTO {

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    @Pattern(regexp = "^[\\p{L}][\\p{L} .'-]*$", message = "O nome deve conter apenas letras e espaços")
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]{2,}$", message = "Formato de e-mail inválido")
    @Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres")
    private String email;

    // Máximo de 72 porque o BCrypt só usa os 72 primeiros caracteres da senha.
    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).*$", message = "A senha deve conter pelo menos uma letra e um número")
    private String senha;

    // DDD (11 a 99) + número: celular (9 + 8 dígitos) ou fixo (2 a 5 + 7 dígitos).
    @NotBlank(message = "O telefone é obrigatório")
    @Pattern(regexp = "^[1-9][1-9](9\\d{8}|[2-5]\\d{7})$",
            message = "Telefone inválido. Informe DDD + número (ex.: 71 99999-9999)")
    private String telefone;

    @NotNull(message = "O perfil é obrigatório")
    private PerfilUsuario perfil;

    public UsuarioRequestDTO() {
    }

    public UsuarioRequestDTO(String nome, String email, String senha, String telefone, PerfilUsuario perfil) {
        setNome(nome);
        setEmail(email);
        this.senha = senha;
        setTelefone(telefone);
        this.perfil = perfil;
    }

    public String getNome() {
        return nome;
    }

    // Normaliza: tira espaços das pontas e junta espaços repetidos ("  Ana   Maria " -> "Ana Maria")
    public void setNome(String nome) {
        this.nome = (nome == null) ? null : nome.trim().replaceAll("\\s+", " ");
    }

    public String getEmail() {
        return email;
    }

    // Normaliza: espaços das pontas fora e tudo minúsculo
    public void setEmail(String email) {
        this.email = (email == null) ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    public String getSenha() {
        return senha;
    }

    // A senha NÃO é alterada (nem trim): espaço pode fazer parte dela.
    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTelefone() {
        return telefone;
    }

    // Normaliza: guarda só os dígitos ("(71) 99999-9999" -> "71999999999")
    public void setTelefone(String telefone) {
        this.telefone = (telefone == null) ? null : telefone.replaceAll("\\D", "");
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }
}
