package com.example.app_na_esquina.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

// Código de 6 dígitos da recuperação de senha. Só o HASH (HMAC) fica no banco.
// Um registro por usuário: pedir um código novo apaga o anterior.
@Entity
@Table(name = "tb_tokens_recuperacao")
public class TokenRecuperacaoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private UsuarioModel usuario;

    @Column(name = "codigo_hash", nullable = false, length = 64)
    private String codigoHash;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "expira_em", nullable = false)
    private LocalDateTime expiraEm;

    // Quantas vezes já erraram o código. Passou do limite, o código é invalidado.
    @Column(name = "tentativas", nullable = false)
    private int tentativas = 0;

    public TokenRecuperacaoModel() {
    }

    public TokenRecuperacaoModel(UsuarioModel usuario, String codigoHash, LocalDateTime expiraEm) {
        this.usuario = usuario;
        this.codigoHash = codigoHash;
        this.expiraEm = expiraEm;
    }

    public Long getId() { return id; }
    public UsuarioModel getUsuario() { return usuario; }
    public String getCodigoHash() { return codigoHash; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getExpiraEm() { return expiraEm; }
    public int getTentativas() { return tentativas; }
    public void setTentativas(int tentativas) { this.tentativas = tentativas; }
}
