package com.example.app_na_esquina.model;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "tb_mercadinhos")
public class MercadinhoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMercadinho;

    @Column(nullable = false)
    private String nomeFantasia;

    private String cnpj;

    @Column(nullable = false)
    private String endereco;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    private LocalTime horarioAbertura;
    private LocalTime horarioFechamento;

    @ManyToOne
    @JoinColumn(name = "comerciante_id", nullable = false)
    private UsuarioModel comerciante;

    public MercadinhoModel() {
    }

    public MercadinhoModel(Long idMercadinho, String nomeFantasia, String cnpj, String endereco, Double latitude, Double longitude, LocalTime horarioAbertura, LocalTime horarioFechamento, UsuarioModel comerciante) {
        this.idMercadinho = idMercadinho;
        this.nomeFantasia = nomeFantasia;
        this.cnpj = cnpj;
        this.endereco = endereco;
        this.latitude = latitude;
        this.longitude = longitude;
        this.horarioAbertura = horarioAbertura;
        this.horarioFechamento = horarioFechamento;
        this.comerciante = comerciante;
    }

    public Long getIdMercadinho() {
        return idMercadinho;
    }

    public void setIdMercadinho(Long idMercadinho) {
        this.idMercadinho = idMercadinho;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public LocalTime getHorarioAbertura() {
        return horarioAbertura;
    }

    public void setHorarioAbertura(LocalTime horarioAbertura) {
        this.horarioAbertura = horarioAbertura;
    }

    public LocalTime getHorarioFechamento() {
        return horarioFechamento;
    }

    public void setHorarioFechamento(LocalTime horarioFechamento) {
        this.horarioFechamento = horarioFechamento;
    }

    public UsuarioModel getComerciante() {
        return comerciante;
    }

    public void setComerciante(UsuarioModel comerciante) {
        this.comerciante = comerciante;
    }
}
