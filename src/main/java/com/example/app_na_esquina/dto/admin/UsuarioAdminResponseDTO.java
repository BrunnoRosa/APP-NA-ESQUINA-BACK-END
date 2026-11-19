package com.example.app_na_esquina.dto.admin;

import com.example.app_na_esquina.model.enums.PerfilUsuario;

public class UsuarioAdminResponseDTO {

    private Long idUsuario;
    private String nome;
    private String email;
    private String telefone;
    private PerfilUsuario perfil;
    private String cidade;
    private String uf;

    // Dados da loja vinculada (quando COMERCIANTE)
    private Long idMercadinho;
    private String nomeFantasia;
    private String cnpj;
    private String enderecoMercadinho;
    private long totalProdutos;
    private long totalPedidos;

    public UsuarioAdminResponseDTO() {
    }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public PerfilUsuario getPerfil() { return perfil; }
    public void setPerfil(PerfilUsuario perfil) { this.perfil = perfil; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    public Long getIdMercadinho() { return idMercadinho; }
    public void setIdMercadinho(Long idMercadinho) { this.idMercadinho = idMercadinho; }

    public String getNomeFantasia() { return nomeFantasia; }
    public void setNomeFantasia(String nomeFantasia) { this.nomeFantasia = nomeFantasia; }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getEnderecoMercadinho() { return enderecoMercadinho; }
    public void setEnderecoMercadinho(String enderecoMercadinho) { this.enderecoMercadinho = enderecoMercadinho; }

    public long getTotalProdutos() { return totalProdutos; }
    public void setTotalProdutos(long totalProdutos) { this.totalProdutos = totalProdutos; }

    public long getTotalPedidos() { return totalPedidos; }
    public void setTotalPedidos(long totalPedidos) { this.totalPedidos = totalPedidos; }
}