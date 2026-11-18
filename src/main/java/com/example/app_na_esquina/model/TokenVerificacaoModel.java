package com.example.app_na_esquina.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

// Guarda o link de confirmação de e-mail. Só o HASH (SHA-256) do token fica no banco:
// quem ler a tabela não consegue montar um link válido.
@Entity
@Table(name = "tb_tokens_verificacao")
public class TokenVerificacaoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private UsuarioModel usuario;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "expira_em", nullable = false)
    private LocalDateTime expiraEm;

    @Column(name = "usado_em")
    private LocalDateTime usadoEm;

    public TokenVerificacaoModel() {
    }

    public TokenVerificacaoModel(String tokenHash, UsuarioModel usuario, LocalDateTime expiraEm) {
        this.tokenHash = tokenHash;
        this.usuario = usuario;
        this.expiraEm = expiraEm;
    }

    public Long getId() { return id; }
    public String getTokenHash() { return tokenHash; }
    public UsuarioModel getUsuario() { return usuario; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getExpiraEm() { return expiraEm; }
    public LocalDateTime getUsadoEm() { return usadoEm; }
    public void setUsadoEm(LocalDateTime usadoEm) { this.usadoEm = usadoEm; }
}
