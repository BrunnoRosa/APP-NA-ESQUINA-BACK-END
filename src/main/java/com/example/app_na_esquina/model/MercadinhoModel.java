package com.example.app_na_esquina.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Entity
@Table(name = "tb_mercadinhos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}
