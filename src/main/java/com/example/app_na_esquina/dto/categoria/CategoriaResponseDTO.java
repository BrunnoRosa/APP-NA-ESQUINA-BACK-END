package com.example.app_na_esquina.dto.categoria;

public class CategoriaResponseDTO {
    private Long idCategoria;
    private String nome;
    private String iconeUrl;

    public CategoriaResponseDTO() {
    }

    public CategoriaResponseDTO(Long idCategoria, String nome, String iconeUrl) {
        this.idCategoria = idCategoria;
        this.nome = nome;
        this.iconeUrl = iconeUrl;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIconeUrl() {
        return iconeUrl;
    }

    public void setIconeUrl(String iconeUrl) {
        this.iconeUrl = iconeUrl;
    }
}