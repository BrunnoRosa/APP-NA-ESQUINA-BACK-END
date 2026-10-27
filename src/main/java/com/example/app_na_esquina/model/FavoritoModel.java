package com.example.app_na_esquina.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_favorito", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"usuario_id", "mercadinho_id"})
})
public class FavoritoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioModel usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mercadinho_id", nullable = false)
    private MercadinhoModel mercadinho;

    public FavoritoModel() {}

    public FavoritoModel(UsuarioModel usuario, MercadinhoModel mercadinho) {
        this.usuario = usuario;
        this.mercadinho = mercadinho;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioModel getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioModel usuario) {
        this.usuario = usuario;
    }

    public MercadinhoModel getMercadinho() {
        return mercadinho;
    }

    public void setMercadinho(MercadinhoModel mercadinho) {
        this.mercadinho = mercadinho;
    }
}