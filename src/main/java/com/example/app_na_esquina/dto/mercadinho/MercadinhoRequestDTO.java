package com.example.app_na_esquina.dto.mercadinho;

import java.time.LocalTime;


public class MercadinhoRequestDTO {
    private String nomeFantasia;
    private String cnpj;
    private String endereco;
    private Double latitude;
    private Double longitude;
    private LocalTime horarioAbertura;
    private LocalTime horarioFechamento;
    private Long idComerciante; // Recebemos apenas o ID do dono

    public MercadinhoRequestDTO() {
    }

    public MercadinhoRequestDTO(String nomeFantasia, String cnpj, String endereco, Double latitude, Double longitude, LocalTime horarioAbertura, LocalTime horarioFechamento, Long idComerciante) {
        this.nomeFantasia = nomeFantasia;
        this.cnpj = cnpj;
        this.endereco = endereco;
        this.latitude = latitude;
        this.longitude = longitude;
        this.horarioAbertura = horarioAbertura;
        this.horarioFechamento = horarioFechamento;
        this.idComerciante = idComerciante;
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

    public Long getIdComerciante() {
        return idComerciante;
    }

    public void setIdComerciante(Long idComerciante) {
        this.idComerciante = idComerciante;
    }
}