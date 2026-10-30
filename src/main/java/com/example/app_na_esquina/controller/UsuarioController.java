package com.example.app_na_esquina.controller;

import com.example.app_na_esquina.dto.usuario.UsuarioRequestDTO;
import com.example.app_na_esquina.dto.usuario.UsuarioResponseDTO;
import com.example.app_na_esquina.model.UsuarioModel;
import com.example.app_na_esquina.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(@RequestBody UsuarioRequestDTO dto) {
        UsuarioModel model = new UsuarioModel();
        model.setNome(dto.getNome());
        model.setEmail(dto.getEmail());
        model.setSenha(dto.getSenha());
        model.setTelefone(dto.getTelefone());
        model.setPerfil(dto.getPerfil());

        UsuarioModel salvo = usuarioService.salvar(model);

        UsuarioResponseDTO response = new UsuarioResponseDTO();
        response.setIdUsuario(salvo.getIdUsuario());
        response.setNome(salvo.getNome());
        response.setEmail(salvo.getEmail());
        response.setTelefone(salvo.getTelefone());
        response.setPerfil(salvo.getPerfil());

        return ResponseEntity.status(201).body(response);
    }
}