package com.example.app_na_esquina.controller;

import com.example.app_na_esquina.dto.usuario.UsuarioRequestDTO;
import com.example.app_na_esquina.dto.usuario.UsuarioResponseDTO;
import com.example.app_na_esquina.model.UsuarioModel;
import com.example.app_na_esquina.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Esta rota é pública (SecurityConfig). Antes ela aceitava qualquer perfil, inclusive ADMIN,
    // e não validava nada. Agora passa pelo mesmo caminho validado do /api/auth/register.
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(@Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioModel salvo = usuarioService.cadastrar(dto);

        UsuarioResponseDTO response = new UsuarioResponseDTO();
        response.setIdUsuario(salvo.getIdUsuario());
        response.setNome(salvo.getNome());
        response.setEmail(salvo.getEmail());
        response.setTelefone(salvo.getTelefone());
        response.setPerfil(salvo.getPerfil());

        return ResponseEntity.status(201).body(response);
    }
}
