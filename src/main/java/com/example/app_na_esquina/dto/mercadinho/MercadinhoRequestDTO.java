
package com.example.app_na_esquina.dto.mercadinho;

import java.time.LocalTime;

public class MercadinhoRequestDTO {

    private String nomeFantasia;
    private String cnpj;
    private String endereco;
    private String cep;

    // Endereço separado
    private String rua;
    private String numero;
    private String bairro;
    private String complemento;
    private String cidade;
    private String uf;

    private String telefone;

    // Coordenadas para localização interna do mercadinho
    private Double latitude;
    private Double longitude;

    private LocalTime horarioAbertura;
    private LocalTime horarioFechamento;

    private Long idComerciante;

    public MercadinhoRequestDTO() {
    }

    public MercadinhoRequestDTO(
            String nomeFantasia,
            String cnpj,
            String endereco,
            String cep,
            String telefone,
            Double latitude,
            Double longitude,
            LocalTime horarioAbertura,
            LocalTime horarioFechamento,
            Long idComerciante) {

        this.nomeFantasia = nomeFantasia;
        this.cnpj = cnpj;
        this.endereco = endereco;
        this.cep = cep;
        this.telefone = telefone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.horarioAbertura = horarioAbertura;
        this.horarioFechamento = horarioFechamento;
        this.idComerciante = idComerciante;
    }

    // Nome fantasia
    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    // CNPJ
    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    // Endereço completo
    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
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

    // Telefone
    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    // Latitude
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    // Longitude
    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    // Horário de abertura
    public LocalTime getHorarioAbertura() {
        return horarioAbertura;
    }

    public void setHorarioAbertura(LocalTime horarioAbertura) {
        this.horarioAbertura = horarioAbertura;
    }

    // Horário de fechamento
    public LocalTime getHorarioFechamento() {
        return horarioFechamento;
    }

    public void setHorarioFechamento(LocalTime horarioFechamento) {
        this.horarioFechamento = horarioFechamento;
    }

    // ID do comerciante
    public Long getIdComerciante() {
        return idComerciante;
    }

    public void setIdComerciante(Long idComerciante) {
        this.idComerciante = idComerciante;
    }
}
