
package com.example.app_na_esquina.dto.usuario;

import com.example.app_na_esquina.model.enums.PerfilUsuario;

public class UsuarioResponseDTO {

    private Long idUsuario;
    private String nome;
    private String email;
    private String telefone;

    // Endereço
    private String cep;
    private String rua;
    private String numero;
    private String bairro;
    private String complemento;
    private String cidade;
    private String uf;

    private PerfilUsuario perfil;
    private Long idMercadinho;

    public UsuarioResponseDTO() {
    }

    public UsuarioResponseDTO(
            Long idUsuario,
            String nome,
            String email,
            String telefone,
            PerfilUsuario perfil,
            Long idMercadinho
    ) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.perfil = perfil;
        this.idMercadinho = idMercadinho;
    }

    // ID
    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    // Nome
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    // E-mail
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Telefone
    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
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

    // ID do mercadinho
    public Long getIdMercadinho() {
        return idMercadinho;
    }

    public void setIdMercadinho(Long idMercadinho) {
        this.idMercadinho = idMercadinho;
    }
}
