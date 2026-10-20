package com.example.app_na_esquina.dto.usuario;

import com.example.app_na_esquina.model.enums.PerfilUsuario;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioResponseDTO {
    private Long idUsuario;
    private String nome;
    private String email;
    private String telefone;
    private PerfilUsuario perfil;
}