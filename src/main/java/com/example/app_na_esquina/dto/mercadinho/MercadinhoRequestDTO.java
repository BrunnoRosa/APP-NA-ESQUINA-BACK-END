package com.example.app_na_esquina.dto.mercadinho;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalTime;

@Getter
@Setter
public class MercadinhoRequestDTO {
    private String nomeFantasia;
    private String cnpj;
    private String endereco;
    private Double latitude;
    private Double longitude;
    private LocalTime horarioAbertura;
    private LocalTime horarioFechamento;
    private Long idComerciante; // Recebemos apenas o ID do dono
}