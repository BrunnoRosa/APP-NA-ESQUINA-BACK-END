package com.example.app_na_esquina.dto.mercadinho;

import java.time.LocalTime;

public class MercadinhoResponseDTO {
    private Long idMercadinho;
    private String nomeFantasia;
    private String endereco;
    private String cep;
    private String telefone;
    private Double latitude;
    private Double longitude;
    private LocalTime horarioAbertura;
    private LocalTime horarioFechamento;

    public MercadinhoResponseDTO() {
    }

    public MercadinhoResponseDTO(Long idMercadinho, String nomeFantasia, String endereco, String cep, String telefone, Double latitude, Double longitude, LocalTime horarioAbertura, LocalTime horarioFechamento) {
        this.idMercadinho = idMercadinho;
        this.nomeFantasia = nomeFantasia;
        this.endereco = endereco;
        this.cep = cep;
        this.telefone = telefone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.horarioAbertura = horarioAbertura;
        this.horarioFechamento = horarioFechamento;
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

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
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
}