package com.example.app_na_esquina.dto.auth;

import com.example.app_na_esquina.model.enums.PerfilUsuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private Long idUsuario;
    private String nome;
    private PerfilUsuario perfil;
    private Long idMercadinho;
}