package com.example.app_na_esquina.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_categorias")
public class CategoriaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategoria;

    @Column(nullable = false, unique = true)
    private String nome;

    private String iconeUrl;

    public CategoriaModel() {
    }

    public CategoriaModel(Long idCategoria, String nome, String iconeUrl) {
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