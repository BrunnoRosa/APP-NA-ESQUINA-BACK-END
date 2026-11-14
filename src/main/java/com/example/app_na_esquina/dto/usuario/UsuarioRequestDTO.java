
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

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).*$", message = "A senha deve conter pelo menos uma letra e um número")
    private String senha;

    private String confirmarSenha;

    @NotBlank(message = "O telefone é obrigatório")
    @Pattern(
            regexp = "^[1-9][1-9](9\\d{8}|[2-5]\\d{7})$",
            message = "Telefone inválido. Informe DDD + número (ex.: 71 99999-9999)"
    )
    private String telefone;

    // Endereço
    private String cep;
    private String rua;
    private String numero;
    private String bairro;
    private String complemento;
    private String cidade;
    private String uf;

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

    // Nome
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = (nome == null) ? null : nome.trim().replaceAll("\\s+", " ");
    }

    // E-mail
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = (email == null) ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    // Senha
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getConfirmarSenha() {
        return confirmarSenha;
    }

    public void setConfirmarSenha(String confirmarSenha) {
        this.confirmarSenha = confirmarSenha;
    }

    // Telefone
    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = (telefone == null) ? null : telefone.replaceAll("\\D", "");
    }

    // CEP
    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    // Rua
    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    // Número
    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    // Bairro
    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    // Complemento
    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    // Cidade
    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    // UF
    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    // Perfil
    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }
}
